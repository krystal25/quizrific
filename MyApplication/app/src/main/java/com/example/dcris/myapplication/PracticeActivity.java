package com.example.dcris.myapplication;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PracticeActivity extends AppCompatActivity {

    private ArrayList<QuestionItem> quizList;
    private RequestQueue mRequestQueue;

    private TextView questionTV, countTV,categoryTV,diffTV;
    private ImageButton nextBtn,prevBtn;
    private Button answerBtn;
    private LinearLayout ll;

    private int questionIndex=0;
    private int answerIndex=0;

    private int noOfAnswers;
    private String[] allAnswers;
    String correctAnswer;
    String amount,category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizList = new ArrayList<>();

        questionTV = findViewById(R.id.quiz_tv_question);
        nextBtn = findViewById(R.id.quiz_btn_next);
        prevBtn = findViewById(R.id.quiz_btn_prev);
        countTV = findViewById(R.id.quiz_tv_count);
        countTV.setVisibility(View.VISIBLE);
        categoryTV = findViewById(R.id.quiz_tv_category);
        categoryTV.setVisibility(View.VISIBLE);
        diffTV = findViewById(R.id.quiz_tv_diff);
        diffTV.setVisibility(View.VISIBLE);
        ll = findViewById(R.id.lin);
        ll.setGravity(Gravity.CENTER);

        String url = null;
        Bundle bundle = getIntent().getExtras();
        category = bundle.getString("category");
        int catID = 0;
        switch(category){
            case "General Knowledge": catID=9; break;
            case "Books": catID=10 ; break;
            case "Movies": catID=11 ; break;
            case "Video Games": catID=15; break;
            case "Science&Nature": catID=17; break;
            case "Computers": catID=18; break;
            case "Geography": catID= 22; break;
            case "History": catID= 23; break;
            case "Any": catID=0; break;
        }
            String difficulty = bundle.getString("difficulty");
            amount = bundle.getString("amount");
            String categoryId = String.valueOf(catID);

            if (!categoryId.equals("0") && !difficulty.equals("any")) {
                url = "https://opentdb.com/api.php?amount=" + amount + "&category=" + categoryId + "&difficulty=" + difficulty + "";
            } else if (categoryId.equals("0") && !difficulty.equals("any")) {
                url = "https://opentdb.com/api.php?amount=" + amount + "&difficulty=" + difficulty + "";
            } else if (!categoryId.equals("0") && difficulty.equals("any")) {
                url = "https://opentdb.com/api.php?amount=" + amount + "&category=" + categoryId + "";
            } else if (categoryId.equals("0") && difficulty.equals("any")) {
                url = "https://opentdb.com/api.php?amount=" + amount + "";
            }

            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
            parseJSON(url);
            countTV.setText("1/" +amount);
         categoryTV.append(category);
        diffTV.append(difficulty);


        //prevBtn.setVisibility(View.INVISIBLE);
        prevBtn.setEnabled(false);
        prevBtn.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(questionIndex == quizList.size()-2){
                    //nextBtn.setVisibility(View.INVISIBLE);
                    nextBtn.setEnabled(false);
                    nextBtn.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                }
                //prevBtn.setVisibility(View.VISIBLE);
                prevBtn.setEnabled(true);
                prevBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                //setez noua intrebare
                questionTV.setText(quizList.get(++questionIndex).getQuestion());
                countTV.setText(String.valueOf(questionIndex+1) +"/"+amount);
                //scot butoanele precedente ( ca poate sunt mai multe/putine)
                ll.removeAllViews();
                //iau nr total de rasp (gresite + 1 corect)
                noOfAnswers = quizList.get(answerIndex+1).getWrongArr().length+1;
                //fac un array cu rapsunruile
                allAnswers =new String[noOfAnswers];
                Log.d("aaaaaaa",String.valueOf(allAnswers.length));
                //iau rasp corect
                correctAnswer = quizList.get(++answerIndex).getCorrect();
                //addAnswerBtn(correctAnswer);
                //iau rasp gresite si le bag in array
                for(int j=0; j<quizList.get(answerIndex).getWrongArr().length;j++){
                    String incorrectAnswer = quizList.get(answerIndex).getWrongArr()[j];
                    allAnswers[j] = incorrectAnswer;
                    //addAnswerBtn(incorrectAnswer);
                }
                //bag si rasp corect dar ce naibii e indexul asta, deci ma uitla nr de rasp gresite al intrebarii curente ok lol
                allAnswers[quizList.get(answerIndex).getWrongArr().length] = correctAnswer;
                Log.d("bbbb",allAnswers[0]);
                //aicia le amestec
                ArrayList<Integer> numberList = new ArrayList<Integer>();
                for (int i = 0; i < allAnswers.length; i++) numberList.add(i);
                Collections.shuffle(numberList);

                for (int answersNo = 0; answersNo < allAnswers.length; answersNo++){
                    String randomAnswer =allAnswers[numberList.get(answersNo)];
                    addAnswerBtn(randomAnswer,answersNo);
                }

                Log.d("ccccc",correctAnswer);

            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (questionIndex != quizList.size() - 2) {
                    //nextBtn.setVisibility(View.VISIBLE);
                    nextBtn.setEnabled(true);
                    nextBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                }
                if (questionIndex == 1) {
                    //prevBtn.setVisibility(View.INVISIBLE);
                    prevBtn.setEnabled(false);
                    prevBtn.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                }
                questionTV.setText(quizList.get(--questionIndex).getQuestion());
                countTV.setText(String.valueOf(questionIndex+1) +"/"+amount);
                ll.removeAllViews();


                noOfAnswers = quizList.get(answerIndex-1).getWrongArr().length+1;
                allAnswers =new String[noOfAnswers];

                correctAnswer = quizList.get(--answerIndex).getCorrect();
                //addAnswerBtn(quizList.get(--answerIndex).getCorrect());
                for (int j = 0; j < quizList.get(answerIndex).getWrongArr().length; j++) {
                    String incorrectAnswer = quizList.get(answerIndex).getWrongArr()[j];
                    allAnswers[j] = incorrectAnswer;
                    //addAnswerBtn(quizList.get(answerIndex).getIncorrectA()[j]);
                }
                allAnswers[quizList.get(answerIndex).getWrongArr().length] = correctAnswer;

                ArrayList<Integer> numberList = new ArrayList<Integer>();
                for (int i = 0; i < allAnswers.length; ++i) numberList.add(i);
                Collections.shuffle(numberList);

                for (int answersNo = 0; answersNo < allAnswers.length; answersNo++){
                    String randomAnswer =allAnswers[numberList.get(answersNo)];
                    addAnswerBtn(randomAnswer,answersNo);
                }

            }
        });

    }


    private void parseJSON(String url) {
        //String url ="https://opentdb.com/api.php?amount=5&category=9&difficulty=easy";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);

                        String question = hit.getString("question");
                        String decodedQuestion =  Html.fromHtml((question),Html.FROM_HTML_MODE_LEGACY).toString();
                        String correctA = hit.getString("correct_answer");
                        String decodedAnswerC =Html.fromHtml(correctA,Html.FROM_HTML_MODE_LEGACY).toString();

                        JSONArray incorrectAnswers = hit.getJSONArray("incorrect_answers");

                        String[] arrayIncorrectA = new String[incorrectAnswers.length()];
                        for (int j = 0; j < incorrectAnswers.length(); j++) {
                            arrayIncorrectA[j] = Html.fromHtml((incorrectAnswers.get(j).toString()),Html.FROM_HTML_MODE_LEGACY).toString();;
                        }

                        quizList.add(new QuestionItem(decodedQuestion, decodedAnswerC, arrayIncorrectA));
                    }
                    questionTV.setText(quizList.get(questionIndex).getQuestion());

                    int j;
                    for( j=0; j<quizList.get(answerIndex).getWrongArr().length;j++){
                        addAnswerBtn(quizList.get(answerIndex).getWrongArr()[j], j);
                    }

                    correctAnswer = quizList.get(answerIndex).getCorrect();
                    addAnswerBtn(correctAnswer,j);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }



    private void addAnswerBtn(String answer, final int answerId) {

            answerBtn = new Button(this);
            answerBtn.setText(answer);
            answerBtn.setId(answerId);
            ll.addView(answerBtn);

            final int wrongColor= ContextCompat.getColor(this, R.color.wrongAnswer);
            final int correctColor=ContextCompat.getColor(this, R.color.correctAnswer);

            answerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button clickedBtn =(Button)v;
                    if (clickedBtn.getText().toString().equals(correctAnswer)) {
                        clickedBtn.setBackgroundColor(correctColor);
                    } else {
                        clickedBtn.setBackgroundColor(wrongColor);
                    }
            }
        });
    }
}


