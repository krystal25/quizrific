package com.example.dcris.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.QuizTakingActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.adapters.ScheduledAdapter;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.ScheduledItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment {


    private String username;
    private RecyclerView mRecyclerView;
    private ScheduledAdapter mScheduledAdapter;
    private ArrayList<ScheduledItem> mQuizList;

    public static final String EXTRA_QUIZ_NAME = "quizName";
    public static final String EXTRA_COURSE = "course";
    public static final String EXTRA_PROF = "prof";
    public static final String EXTRA_DURATION = "duration";

    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recyclerview, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mQuizList = new ArrayList<>();

        username = KeyValueDB.getUsername(getActivity());

        parseJSON();
        return rootView;
    }

    private void parseJSON() {
        String url = "http://" + Common.serverIP + "/quizrific/get_scheduled_quizzes_students.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);

                                String quizName = hit.getString("quizName");
                                String course = hit.getString("course");
                                String prof = hit.getString("professor");
                                String dateStr = hit.getString("quiz_date");

                                String time = hit.getString("quiz_hour");
                                String duration = hit.getString("duration");

                                mQuizList.add(new ScheduledItem(quizName, course, prof,dateStr,time,duration));
                            }

                            mScheduledAdapter = new ScheduledAdapter(getActivity(), mQuizList);
                            mRecyclerView.setAdapter(mScheduledAdapter);

                            mScheduledAdapter.setOnItemClickListener(new ScheduledAdapter.OnItemClickListener() {

                                @Override
                                public void onItemClick(int position) {
                                    ScheduledItem clickedItem = mQuizList.get(position);
                                    Date dateBD=null;
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        dateBD = format.parse(clickedItem.getDate());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Date currDate = null;
                                    //get local date on device
                                    String currDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    try {
                                        currDate = format.parse(currDateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Date timeBD=null;
                                    SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                                    try {
                                        timeBD = format2.parse(clickedItem.getTime());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Date strLocalTime = getCurrentTime();

                                    //if quiz can be taken
                                    if (dateBD.compareTo(currDate) == 0) {//quiz today

                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(timeBD);
                                        cal.add(Calendar.MINUTE, Integer.parseInt(clickedItem.getDuration()));
                                        String newTime = format2.format(cal.getTime());

                                        Date end=null;
                                        try {
                                            end = format2.parse(newTime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if((strLocalTime.after(timeBD) && strLocalTime.before(end)) || strLocalTime.compareTo(timeBD)==0) { //quiz ongoing
                                            Intent detailIntent = new Intent(getActivity(), QuizTakingActivity.class);
                                            detailIntent.putExtra(EXTRA_QUIZ_NAME, clickedItem.getQuiz());
                                            detailIntent.putExtra(EXTRA_COURSE, clickedItem.getCourse());
                                            detailIntent.putExtra(EXTRA_PROF, clickedItem.getProf());
                                            long mills = strLocalTime.getTime() - end.getTime();
                                            int mins = -(int)mills/(1000*60) % 60;
                                            Log.d("x",String.valueOf(mins));
                                            detailIntent.putExtra(EXTRA_DURATION, String.valueOf(mins));
                                            startActivity(detailIntent);
                                        }else{
                                            Toast.makeText(getActivity(),"quiz cannot be accessed",Toast.LENGTH_SHORT).show();
                                        }
                                    }else if((dateBD.compareTo(currDate) < 0)){
                                        Toast.makeText(getActivity(),"quiz cannot be re-taken",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(),"come back when the quiz is ready",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("student", username);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }

    }

    public Date getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strTime= mdformat.format(calendar.getTime());
        Date d = null;
        try {
            d = mdformat.parse(strTime);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

}
