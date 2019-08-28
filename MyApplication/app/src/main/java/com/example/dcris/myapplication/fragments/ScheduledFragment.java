package com.example.dcris.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.QuestionsActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.StatsViewerActivity;
import com.example.dcris.myapplication.adapters.QuizAdapter;
import com.example.dcris.myapplication.adapters.ScheduledAdapter;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuizItem;
import com.example.dcris.myapplication.model.ScheduledItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScheduledFragment extends Fragment {

    private String username;
    private RecyclerView mRecyclerView;
    private ScheduledAdapter mScheduledAdapter;
    private ArrayList<ScheduledItem> mQuizList;


    public static final String EXTRA_QUIZ_NAME = "quizName";
    public static final String EXTRA_COURSE = "course";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        String url = "http://" + Common.serverIP + "/quizrific/get_scheduled_quizzes.php";

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

                                mQuizList.add(new ScheduledItem(quizName, course,prof, dateStr,time,duration));
                            }

                            mScheduledAdapter = new ScheduledAdapter(getActivity(), mQuizList);
                            mRecyclerView.setAdapter(mScheduledAdapter);

                            mScheduledAdapter.setOnItemClickListener(new ScheduledAdapter.OnItemClickListener() {

                                @Override
                                public void onItemClick(int position) {
                                    Intent detailIntent = new Intent(getActivity(), StatsViewerActivity.class);
                                    ScheduledItem clickedItem = mQuizList.get(position);
                                    detailIntent.putExtra(EXTRA_QUIZ_NAME, clickedItem.getQuiz());
                                    detailIntent.putExtra(EXTRA_COURSE, clickedItem.getCourse());

                                    startActivity(detailIntent);
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
                hashMap.put("professor", username);
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


}