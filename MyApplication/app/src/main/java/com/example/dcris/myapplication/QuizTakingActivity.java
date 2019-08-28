package com.example.dcris.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;
import com.example.dcris.myapplication.model.StudentAnswerItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuizTakingActivity extends AppCompatActivity {

    private ArrayList<QuestionItem> quizList;
    private RequestQueue mRequestQueue;

    private TextView questionTV,timerTV;
    private ImageButton nextBtn,prevBtn;
    private Button answerBtn,submitBtn;
    private LinearLayout ll;

    private int questionIndex=0;
    private int answerIndex=0;

    private int noOfAnswers;
    private String[] allAnswers;
    private String[] wrong;
    String correctAnswers[];
    String quiz;
    String prof;
    String course;
    String duration;
    int minutes,counter;
    private ImageView img;
    boolean clicked;
    int chosenAnswers=0;
    ArrayList<StudentAnswerItem> studentAnswersList = new ArrayList<>();
    String[] givenAnswers;
    List<Button> allABs = new ArrayList<>();
    String URL_insert =  "http://" + Common.serverIP + "/quizrific/insert_students_answers.php";
    String username;
    String points_awarded[];
    float result;
    String URL_result =  "http://" + Common.serverIP + "/quizrific/insert_results.php";
    private int EnabledButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizList = new ArrayList<>();

        questionTV = findViewById(R.id.quiz_tv_question);
        submitBtn = findViewById(R.id.quiz_btn_submit);
        submitBtn.setVisibility(View.VISIBLE);
        nextBtn = findViewById(R.id.quiz_btn_next);
        prevBtn = findViewById(R.id.quiz_btn_prev);
        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        timerTV = findViewById(R.id.quiz_tv_timer);
        timerTV.setVisibility(View.VISIBLE);
        img = findViewById(R.id.quiz_iv_image);

        username = KeyValueDB.getUsername(getApplicationContext());

        //String urlImage = "http://" + Common.serverIP + "/quizrific/images/1947395025_1556309938.jpeg";


        ll = findViewById(R.id.lin);
        ll.setGravity(Gravity.CENTER);

        Bundle bundle = getIntent().getExtras();
        quiz = bundle.getString("quizName");
        course = bundle.getString("course");
        prof = bundle.getString("prof");
        duration = bundle.getString("duration");
        minutes = Integer.parseInt(duration);
        counter = minutes*1000*60;

        new CountDownTimer(counter, 1000){
            public void onTick(long millisUntilFinished){
                timerTV.setText("Time left: "+String.format("%d:%d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                counter--;
            }
            public  void onFinish(){
                timerTV.setText("FINISH!!");
            }
        }.start();


        timerTV.setText("Timer: " + duration);

        String url =  "http://" + Common.serverIP + "/quizrific/get_questions.php";
        parseJSONquiz(url);

        prevBtn.setEnabled(false);
        prevBtn.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("index",String.valueOf(questionIndex));

                StudentAnswerItem st = new StudentAnswerItem(questionTV.getText().toString(),givenAnswers);
                Log.d("q",String.valueOf(chosenAnswers));
                Log.d("q2",String.valueOf(correctAnswers.length));
               // Log.d("a",st.getGivenAnsArr()[0]);
                if(chosenAnswers != correctAnswers.length){
                    Toast.makeText(getApplicationContext(),String.valueOf(correctAnswers.length) + "answer(s) required",Toast.LENGTH_SHORT).show();
                    return;
                }
                float points = Float.parseFloat(quizList.get(questionIndex).getPoints());
                int noOfcorrectAnswers = correctAnswers.length;
                float partialPoints = points / noOfcorrectAnswers;
                points_awarded = new String[noOfcorrectAnswers];

                for(int i=0; i< st.getGivenAnsArr().length;i++){
                    for(int j=0; j < correctAnswers.length;j++)
                        if(st.getGivenAnsArr()[i].equals(correctAnswers[j])){
                            points_awarded[i] = String.valueOf(partialPoints);
                            result = result + partialPoints;
                            break;
                         }else{
                            Log.d("givenansw",String.valueOf(st.getGivenAnsArr()[i]));
                            Log.d("correctansw",String.valueOf(correctAnswers[j]));
                            points_awarded[i] = "0";
                        }
                }
              insertAnsw(st.getQuestion(),st.getGivenAnsArr(), points_awarded);
                //studentAnswersList.add(questionIndex,st);
               //studentAnswersList.set(questionIndex,st);

               // studentAnswersList.get(questionIndex).setQuestion(questionTV.getText().toString());
               // studentAnswersList.get(questionIndex).getGivenAnsArr()[0] = st.getGivenAnsArr()[0] ;

               // Log.d("q",studentAnswersList.get(questionIndex).getQuestion());
               // Log.d("a",studentAnswersList.get(questionIndex).getGivenAnsArr()[0]);
                ++questionIndex;

                if(questionIndex == quizList.size()){
                    //pct din oficiu
                    result = result + 1;
                    //Toast.makeText(getApplicationContext(),"result: "+ String.valueOf(result),Toast.LENGTH_SHORT).show();
                   registerResult(String.valueOf(result));
                    Intent i = new Intent(getApplication(),ResultsActivity.class);
                    i.putExtra("quiz", quiz);
                    i.putExtra("result",String.valueOf(result));
                    startActivity(i);
                    finish();
                } else {
                    // prevBtn.setEnabled(true);
                    //  prevBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                    //setez noua intrebare
                    questionTV.setText(quizList.get(questionIndex).getQuestion());
                    if (quizList.get(questionIndex).getImage().equals("null")) {
                        img.setVisibility(View.GONE);
                    } else {
                        img.setVisibility(View.VISIBLE);
                        Picasso.get().load(quizList.get(questionIndex).getImage()).into(img);
                    }
                    //scot butoanele precedente ( ca poate sunt mai multe/putine)
                    ll.removeAllViews();
                    //iau nr total de rasp, am array pt corecte si gresite
                    noOfAnswers = quizList.get(questionIndex).getWrongArr().length + quizList.get(questionIndex).getCorrectArr().length;
                    //fac un array cu rapsunruile
                    allAnswers = new String[noOfAnswers];
                    //iau rasp gresite si le bag in array
                    wrong = new String[quizList.get(questionIndex).getWrongArr().length];
                    for (int j = 0; j < quizList.get(questionIndex).getWrongArr().length; j++) {
                        String incorrectAnswer = quizList.get(questionIndex).getWrongArr()[j];
                        wrong[j] = incorrectAnswer;
                    }
                    //si pe alea corecte
                    correctAnswers = new String[quizList.get(questionIndex).getCorrectArr().length];
                    for (int j = 0; j < quizList.get(questionIndex).getCorrectArr().length; j++) {
                        String correctAnswer = quizList.get(questionIndex).getCorrectArr()[j];
                        correctAnswers[j] = correctAnswer;
                        //allAnswers[j] = correctAnswer;
                    }
                    givenAnswers = new String[correctAnswers.length];
                    allAnswers = concat(wrong, correctAnswers);
                    //aicia le amestec
                    ArrayList<Integer> numberList = new ArrayList<Integer>();
                    for (int i = 0; i < allAnswers.length; i++) numberList.add(i);
                    Collections.shuffle(numberList);

                    for (int answersNo = 0; answersNo < allAnswers.length; answersNo++) {
                        String randomAnswer = allAnswers[numberList.get(answersNo)];
                        addAnswerBtn(randomAnswer, answersNo);
                    }
                }
            }
        });
    }

    private void parseJSONquiz(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray= new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);

                        String question = hit.getString("question");
                        String points = hit.getString("points");
                        String image = hit.getString("image");
                        JSONArray correctAnswers = hit.getJSONArray("correct_answers");
                        String[] arrayCorrectA = new String[correctAnswers.length()];
                        for (int j = 0; j < correctAnswers.length(); j++) {
                            arrayCorrectA[j] = correctAnswers.get(j).toString();
                        }

                        JSONArray incorrectAnswers = hit.getJSONArray("incorrect_answers");
                        String[] arrayIncorrectA = new String[incorrectAnswers.length()];
                        for (int j = 0; j < incorrectAnswers.length(); j++) {
                            arrayIncorrectA[j] = incorrectAnswers.get(j).toString();
                        }

                        quizList.add(new QuestionItem(question, points, image, arrayCorrectA, arrayIncorrectA));
                    }

                    questionTV.setText(quizList.get(questionIndex).getQuestion());
                    if(quizList.get(questionIndex).getImage().equals("null")){
                        img.setVisibility(View.GONE);
                    }else{
                        img.setVisibility(View.VISIBLE);
                        Picasso.get().load(quizList.get(questionIndex).getImage()).into(img);
                    }

                    int j;
                    for( j=0; j<quizList.get(answerIndex).getWrongArr().length;j++){
                        addAnswerBtn(quizList.get(answerIndex).getWrongArr()[j], j);
                    }

                    correctAnswers = new String[quizList.get(answerIndex).getCorrectArr().length];
                    for( j=0; j<quizList.get(answerIndex).getCorrectArr().length;j++){
                        correctAnswers[j] = quizList.get(answerIndex).getCorrectArr()[j];
                        addAnswerBtn(quizList.get(answerIndex).getCorrectArr()[j], j);
                    }
                    givenAnswers = new String[correctAnswers.length];

                    if(quizList.size() == 1){
                        nextBtn.setEnabled(false);
                        nextBtn.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
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
                hashMap.put("quiz_name",quiz);
                hashMap.put("professor","prof");
                hashMap.put("course",course);
                return hashMap;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void addAnswerBtn(final String answer, final int answerId) {

        chosenAnswers=0;
        answerBtn = new Button(this);
        answerBtn.setText(answer);
        answerBtn.setId(answerId);
        allABs.add(answerBtn);
        ll.addView(answerBtn);
        final Drawable d = answerBtn.getBackground();

        final int color= ContextCompat.getColor(this, R.color.colorBar4);
        final int color2= ContextCompat.getColor(this, R.color.activeItemBottomNav);

        answerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button clickedBtn = (Button) v;
                if(!clickedBtn.isSelected()) {
                    if (chosenAnswers == correctAnswers.length) {
                        Toast.makeText(getApplicationContext(), "no more answers", Toast.LENGTH_SHORT).show();
                    } else if (chosenAnswers < correctAnswers.length) {
                        clickedBtn.setBackgroundColor(color);
                        clickedBtn.setSelected(true);
                        givenAnswers[chosenAnswers] = clickedBtn.getText().toString();
                        chosenAnswers++;
                    }
                }else{
                    chosenAnswers--;
                    clickedBtn.setSelected(false);
                    //clickedBtn.setBackgroundResource(android.R.drawable.btn_default);
                   // clickedBtn.setBackgroundDrawable(d);
                    clickedBtn.setBackground(d);
                }
                   // chosenAnswers--;
                    //clickedBtn.setBackgroundColor(color2);


            }
        });
    }

    private String[] concat(String[] A, String[] B) {
        int aLen = A.length;
        int bLen = B.length;
        String[] C= new String[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    private void colorBtn(final String answer){
        final int color= ContextCompat.getColor(this, R.color.colorBar4);
        Log.d("answ", answer);
        for (int i = 0; i < allABs.size(); i++) {
                String option = allABs.get(i).getText().toString();
                if(option.equals(answer)){
                    Log.d("answ2", allABs.get(i).getText().toString());
                    allABs.get(i).setBackgroundColor(color);
                }
        }
    }

    private void insertAnsw(final String question, final String[] answers,  final String[] points_awarded){
        StringRequest request = new StringRequest(Request.Method.POST, URL_insert, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("error"),Toast.LENGTH_LONG).show();
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
                hashMap.put("student",username);
                hashMap.put("quiz_name",quiz);
                hashMap.put("professor",prof);
                hashMap.put("course",course);
                hashMap.put("question",question);
                for(int answerIndex=0; answerIndex < answers.length; answerIndex++) {
                    hashMap.put("answer["+answerIndex+"]", answers[answerIndex]);
                    hashMap.put("points_awarded["+answerIndex+"]", points_awarded[answerIndex]);
                }
                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void registerResult(final String final_result){
        StringRequest request = new StringRequest(Request.Method.POST, URL_result, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("error"),Toast.LENGTH_LONG).show();
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
                hashMap.put("student",username);
                hashMap.put("quiz",quiz);
                hashMap.put("professor",prof);
                hashMap.put("course",course);
                hashMap.put("result",final_result);

                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


}

