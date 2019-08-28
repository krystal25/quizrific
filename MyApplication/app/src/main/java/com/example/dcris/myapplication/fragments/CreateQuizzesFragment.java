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
import android.widget.EditText;
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
import com.example.dcris.myapplication.adapters.QuizAdapter;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuizItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateQuizzesFragment extends Fragment {

    private String username;
    private Button createQuizBtn;
    private EditText quizET;
    private Spinner departmentSpn;
    private Spinner courseSpn;
    private TextView wrongTV;

    private static final String url = "http://" + Common.serverIP + "/quizrific/get_departments.php";
    private static final String urlCourses = "http://" + Common.serverIP + "/quizrific/get_courses.php";
    private static final String urlQuizzes = "http://" + Common.serverIP + "/quizrific/insert_quizzes.php";
    private StringRequest request;

    String quizName = null;
    String course = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_one, container, false);

        final View rootView = inflater.inflate(R.layout.fragment_create_quizzes, container, false);

        createQuizBtn = rootView.findViewById(R.id.fragquizzes_btn_createQuiz);
        wrongTV = rootView.findViewById(R.id.fragquizzes_tv_wrong);
        quizET = rootView.findViewById(R.id.fragquizzes_et_quizname);
        departmentSpn = rootView.findViewById(R.id.fragquizzes_spn_departments);
        courseSpn = rootView.findViewById(R.id.fragquizzes_spn_courses);

        username = KeyValueDB.getUsername(getActivity());

        final List<String> departmArray =  new ArrayList<>();
        departmArray.add("Select department");

        final List<String> coursesArray = new ArrayList<>();
        courseSpn.setEnabled(false);

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

        createQuizBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                wrongTV.setText("");


                if (quizET.getText().toString().equals("")) {
                    wrongTV.setText("Complete the name of the quiz");
                    return;
                }

                quizName = quizET.getText().toString();
                course = courseSpn.getSelectedItem().toString();
                // final String professor = UsernameRetrieval.getUsername();


                request = new StringRequest(Request.Method.POST, urlQuizzes, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.names().get(0).equals("success")) {
                                goToQuizCreator();
                                Toast.makeText(getActivity(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                    protected Map<String,String> getParams() {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("professor",username);
                        hashMap.put("course", course);
                        hashMap.put("quiz_name",quizName);
                        return hashMap;
                        //return super.getParams();
                    }
                };

                VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);


            }
        });


        return rootView;
    }


    private void goToQuizCreator() {

        quizET.setText("");

        Intent intent = new Intent(getActivity().getApplicationContext(), QuizCreatorActivity.class);
        intent.putExtra("course", course );
        intent.putExtra("quizName", quizName);
        startActivity(intent);
    }

    //refresh the fragment for when a new quiz is created
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }

    }

}
