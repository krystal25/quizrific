package com.example.dcris.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.adapters.QuestionAdapter;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.dcris.myapplication.fragments.ViewQuizzesFragment.EXTRA_COURSE;
import static com.example.dcris.myapplication.fragments.ViewQuizzesFragment.EXTRA_PROF;
import static com.example.dcris.myapplication.fragments.ViewQuizzesFragment.EXTRA_QUIZ_NAME;


public class QuestionsActivity extends AppCompatActivity implements QuestionAdapter.OnItemClickListener {

    String quizName;
    String course;
    String profUsername;


    private ArrayList<QuestionItem> questionsList;

    private RecyclerView mRecyclerView;
    private QuestionAdapter mQuestionAdapter;


    private static final String URL = "http://" + Common.serverIP + "/quizrific/get_questions.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        quizName = intent.getStringExtra(EXTRA_QUIZ_NAME);
        profUsername = intent.getStringExtra(EXTRA_PROF);
        course = intent.getStringExtra(EXTRA_COURSE);

        questionsList = new ArrayList<>();

        getQuestions(quizName,profUsername,course);

    }


    private void getQuestions(final String quizName, final String profUsername, final String course) {
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

                        JSONArray incorrectAnswers = jresponse.getJSONArray("incorrect_answers");
                        String[] arrayIncorrectA = new String[incorrectAnswers.length()];
                        for (int wrongIndex = 0; wrongIndex  < incorrectAnswers.length(); wrongIndex ++) {
                            arrayIncorrectA[wrongIndex ] = incorrectAnswers.get(wrongIndex ).toString();
                        }

                        JSONArray correctAnswers = jresponse.getJSONArray("correct_answers");
                        String[] arrayCorrectA = new String[correctAnswers.length()];
                        for (int correctIndex = 0; correctIndex  < correctAnswers.length(); correctIndex ++) {
                            arrayCorrectA[correctIndex ] = correctAnswers.get(correctIndex ).toString();
                        }
                            questionsList.add(new QuestionItem(question, points,imageStr, arrayCorrectA, arrayIncorrectA));


                    }
                /*
                    questionTV.setText(questionsList.get(0).getQuestion());
                    pointsTV.setText(questionsList.get(0).getPoints());
                    correctTV.setText(questionsList.get(0).getCorrect()[0]);
                    incorrectTV.setText(questionsList.get(0).getIncorrectA()[0]);
                   // textViewIncorrect.append(quizList.get(0).getIncorrectA()[1]);*/
                    mQuestionAdapter = new QuestionAdapter(QuestionsActivity.this, questionsList);
                    mRecyclerView.setAdapter(mQuestionAdapter);

                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                            mQuestionAdapter.deleteItem(viewHolder.getAdapterPosition());
                        }
                    }).attachToRecyclerView(mRecyclerView);

                    mQuestionAdapter.setOnItemClickListener(QuestionsActivity.this);

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
                hashMap.put("professor",profUsername);
                hashMap.put("course", course);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public void onItemClick(int position) {

    }
}