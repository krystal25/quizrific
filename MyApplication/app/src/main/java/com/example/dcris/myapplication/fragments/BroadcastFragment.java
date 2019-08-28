package com.example.dcris.myapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadcastFragment extends DialogFragment {

    private EditText msg;

    private Spinner departmentSpn;
    private Spinner courseSpn;
    private String username;

    private static final String url = "http://" + Common.serverIP + "/quizrific/get_departments.php";
    private static final String urlCourses = "http://" + Common.serverIP + "/quizrific/get_courses.php";
    private StringRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_broadcast, container, false);
        getDialog().setTitle("Important Message");

        departmentSpn = rootView.findViewById(R.id.fragbroad_spn_dept);
        courseSpn = rootView.findViewById(R.id.fragbroad_spn_course);

        username = KeyValueDB.getUsername(getActivity());


        final List<String> departmArray =  new ArrayList<>();
        departmArray.add("Select department");

        final List<String> coursesArray = new ArrayList<>();

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

        msg = rootView.findViewById(R.id.fragbroad_et_msg);
        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button send = (Button) rootView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(msg.getText().toString().equals("")){
                    return;
                }else{
                    String message = msg.getText().toString();
                    Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });


        return rootView;
    }
}