package com.example.dcris.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.adapters.QuestionAdapter;
import com.example.dcris.myapplication.adapters.ResultsAdapter;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class ResultsActivity extends AppCompatActivity {
    String quizName;
    String studUsername;
    private ArrayList<QuestionItem> questionsList;
    private RecyclerView mRecyclerView;
    private ResultsAdapter resultsAdapter;
    private static final String URL = "http://" + Common.serverIP + "/quizrific/get_students_answers.php";
    private StringRequest request;
    private TextView gradeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        quizName = bundle.getString("quiz");
        String result = bundle.getString("result");

        gradeTV = findViewById(R.id.tv_grade);
        gradeTV.setVisibility(View.VISIBLE);
        gradeTV.append(result);

        studUsername = KeyValueDB.getUsername(getApplicationContext());

        questionsList = new ArrayList<>();
        getQuestions(quizName,studUsername);
    }


    private void getQuestions(final String quizName, final String studUsername) {
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);
                    for (int i = 0; i < j.length(); i++) {

                        JSONObject jresponse = j.getJSONObject(i);
                        String question = jresponse.getString("question");

                        String points = jresponse.getString("points");
                        String imageStr = jresponse.getString("image");

                        JSONArray studentsAnswers = jresponse.getJSONArray("students_answers");
                        String[] arrayStudentsAns = new String[studentsAnswers.length()];
                        for (int idx= 0; idx  < studentsAnswers.length(); idx ++) {
                            arrayStudentsAns[idx] = studentsAnswers.get(idx ).toString();
                        }
                        JSONArray pointsAwarded = jresponse.getJSONArray("points_awarded");
                        String[] arrayPointsAwarded = new String[pointsAwarded.length()];
                        for (int idx = 0; idx  < pointsAwarded.length(); idx ++) {
                            arrayPointsAwarded[idx] = pointsAwarded.get(idx ).toString();
                        }
                        questionsList.add(new QuestionItem(question, points,imageStr, arrayStudentsAns, arrayPointsAwarded));
                    }
                    resultsAdapter = new ResultsAdapter(ResultsActivity.this, questionsList);
                    mRecyclerView.setAdapter(resultsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("quiz_name",quizName);
                hashMap.put("student",studUsername);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


}
