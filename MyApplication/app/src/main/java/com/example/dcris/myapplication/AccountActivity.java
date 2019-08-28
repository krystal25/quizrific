package com.example.dcris.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountActivity  extends AppCompatActivity {

    private Spinner departmentSpn;
    private Spinner courseSpn;
    private Button insertBtn;
    private static final String url = "http://" + Common.serverIP + "/quizrific/get_all_departments.php";
    private static final String urlCourses = "http://" + Common.serverIP + "/quizrific/get_courses_dept.php";
    private static final String urlRegister = "http://" + Common.serverIP + "/quizrific/insert_offerings.php";
    private StringRequest request;
    private String username;
    private String course;
    private String department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        departmentSpn = findViewById(R.id.account_spn_departments);
        courseSpn = findViewById(R.id.account_spn_courses);
        insertBtn = findViewById(R.id.account_btn_insert);

        username = KeyValueDB.getUsername(this);

        final List<String> departmArray =  new ArrayList<>();
        departmArray.add("Select department");


        final List<String> coursesArray = new ArrayList<>();
        courseSpn.setEnabled(false);

        // prepare the Request
        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, departmArray );
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
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, coursesArray);
                                    courseSpn.setAdapter(adapter);
                                }else if(jresponse.names().get(0).equals("error")){
                                    // quizzesArray.add("no available quiz");
                                    coursesArray.clear();
                                    coursesArray.add("Select course");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, coursesArray);
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
                        return hashMap;
                        //return super.getParams();
                    }
                };

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        insertBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                course = courseSpn.getSelectedItem().toString();
                department = departmentSpn.getSelectedItem().toString();

                request = new StringRequest(Request.Method.POST, urlRegister, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.names().get(0).equals("success")) {
                                Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                        hashMap.put("department",department);
                        return hashMap;
                        //return super.getParams();
                    }
                };

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


            }
        });
    }
}
