package com.example.dcris.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.QuizCreatorActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.StatsViewerActivity;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class GrowthFragment extends Fragment {

    private String username;

    private Button showBtn;
    private Spinner departmentSpn;
    private Spinner courseSpn;
    private Spinner quizzesSpn;

    private static final String url = "http://" + Common.serverIP + "/quizrific/get_departments.php";
    private static final String urlCourses = "http://" + Common.serverIP + "/quizrific/get_courses.php";
    private static final String urlQuizzes = "http://" + Common.serverIP + "/quizrific/get_quizzes_d.php";

    private StringRequest request;

    private String quizName;
    private String course;

    public GrowthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_growth, container, false);
        showBtn = rootView.findViewById(R.id.fraggrowth_btn_show);
        departmentSpn = rootView.findViewById(R.id.fraggrowth_spn_departments);
        courseSpn = rootView.findViewById(R.id.fraggrowth_spn_courses);
        quizzesSpn = rootView.findViewById(R.id.fraggrowth_spn_quizzes);
        username = KeyValueDB.getUsername(getActivity());

        final List<String> departmArray =  new ArrayList<>();
        departmArray.add("Select department");

        final List<String> coursesArray = new ArrayList<>();
        //coursesArray.add("Select department");
        courseSpn.setEnabled(false);
        quizzesSpn.setEnabled(false);
        final List<String> quizzesArray = new ArrayList<>();

        // prepare the Request
        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray j= new JSONArray(response);
                    for(int i=0;i<j.length();i++){

                        JSONObject jresponse = j.getJSONObject(i);
                        String department = jresponse.getString("department");
                        departmArray.add(department);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("professor",username);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, departmArray );

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpn.setAdapter(adapter);

        departmentSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

               //((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                coursesArray.clear();

                final String selectedItem = parent.getItemAtPosition(position).toString();

                request = new StringRequest(Request.Method.POST, urlCourses, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray j= new JSONArray(response);

                            for(int i=0;i<j.length();i++){

                                JSONObject jresponse = j.getJSONObject(i);
                                if(jresponse.names().get(0).equals("course")) {
                                    courseSpn.setEnabled(true);
                                    String course = jresponse.getString("course");
                                    coursesArray.add(course);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, coursesArray);
                                    courseSpn.setAdapter(adapter);
                                }else if(jresponse.names().get(0).equals("error")){
                                    // quizzesArray.add("no available quiz");
                                    coursesArray.clear();
                                    coursesArray.add("Select course");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, coursesArray);
                                    courseSpn.setAdapter(adapter);
                                    courseSpn.setEnabled(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }){
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("department",selectedItem);
                        hashMap.put("professor", username);
                        return hashMap;
                        //return super.getParams();
                    }
                };

                VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);


            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        courseSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               // ((TextView) parent.getChildAt(0)).setGravity(Gravity.LEFT);
                quizzesArray.clear();
               // quizzesSpn.setEnabled(true);
                final String selectedItem = parent.getItemAtPosition(position).toString();

                request = new StringRequest(Request.Method.POST, urlQuizzes, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray j= new JSONArray(response);

                                for (int i = 0; i < j.length(); i++) {

                                    JSONObject jresponse = j.getJSONObject(i);
                                    if(jresponse.names().get(0).equals("quizName")) {
                                        quizzesSpn.setEnabled(true);
                                        String quiz = jresponse.getString("quizName");
                                        quizzesArray.add(quiz);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, quizzesArray);
                                        quizzesSpn.setAdapter(adapter);
                                    }else if(jresponse.names().get(0).equals("error")){
                                       // quizzesArray.add("no available quiz");
                                        quizzesArray.clear();
                                        quizzesArray.add("Select quiz");
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, quizzesArray);
                                       quizzesSpn.setAdapter(adapter);
                                        quizzesSpn.setEnabled(false);
                                    }
                                }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }){
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("course",selectedItem);
                        hashMap.put("professor", username);
                        return hashMap;
                        //return super.getParams();
                    }
                };

                VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
        quizzesSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.LEFT);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                course = courseSpn.getSelectedItem().toString();
                quizName = quizzesSpn.getSelectedItem().toString();

                goToStatsViewer();


            }
        });
        return rootView;
    }

    private void goToStatsViewer() {

        Intent intent = new Intent(getActivity().getApplicationContext(), StatsViewerActivity.class);
        intent.putExtra("course", course );
       intent.putExtra("quizName", quizName);
        startActivity(intent);
    }

}
