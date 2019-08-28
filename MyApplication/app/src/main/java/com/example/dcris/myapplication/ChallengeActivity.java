package com.example.dcris.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChallengeActivity  extends AppCompatActivity {

    String username;

    TextView txt_timer,txt_question,txt_score;
    Socket socket;
    int color =0;
    int count=0;
    ArrayList<Button> btnList= new ArrayList<>();
    String playerAnswer;
    String correctA;
    boolean answerChosen=false,canPlay=true, gameStarted=false;

    private ArrayList<QuestionItem> quizList;
    private Button answerBtn;
    private LinearLayout ll;

    private int questionIndex=0;
    private int answerIndex=0;

    private int noOfAnswers;
    private String[] allAnswers;
    String correctAnswer;

    int score =0;
    int opponentScore=0;
    String opponentName;

    int colorPlayer1;
    int colorPlayer2;

    private ImageView iv_msg;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        quizList = new ArrayList<>();
        username = KeyValueDB.getUsername(getApplicationContext());

        txt_timer = findViewById(R.id.txt_timer);
        //txt_result = findViewById(R.id.txt_result);

        txt_score = findViewById(R.id.txt_score);
        txt_question = findViewById(R.id.txt_question);
        iv_msg = findViewById(R.id.iv_final);
        pb = findViewById(R.id.challenge_pb);

        ll = findViewById(R.id.layout_btns);
        ll.setGravity(Gravity.CENTER);


        colorPlayer1= ContextCompat.getColor(this, R.color.colorPlayer1);
        colorPlayer2=ContextCompat.getColor(this, R.color.colorPlayer2);

        //question = txt_question.getText().toString();
       // correctA = "Orange";

        //connect to socket server
        try{
            socket = IO.socket("http://35.204.4.163:3000");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", username);
                                socket.emit("client_connects", jsonObject);
                                Toast.makeText(ChallengeActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                            }catch(Exception e){
                                Toast.makeText(ChallengeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            });

            socket.connect();
            registerAllEventsforGame();
        }catch(Exception e){
            Toast.makeText(ChallengeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

       /* //generate same url for each player
        socket.on("url", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        url =  args[0].toString();
                        Toast.makeText(ChallengeActivity.this, url,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
/*
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
        parseJSON();
            }
       }, 1000); //wait for 3sec to let the connection be made

*/



    }



    private void registerAllEventsforGame() {
//configure colors
        socket.on("color", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(args[0].toString().equals("mov")) {
                            color = colorPlayer1;
                        }else{ //we have the first player
                            color = colorPlayer2;
                        }
                    }
                });
            }
        });

        //correct choice
        socket.on("show_correct", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                // correctA = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(final Button btn : btnList) {
                            if(btn.getText().toString().equals(correctAnswer)){
                                Animation mAnimation = new AlphaAnimation(1, 0);
                                mAnimation.setDuration(200);
                                mAnimation.setInterpolator(new LinearInterpolator());
                                mAnimation.setRepeatCount(Animation.INFINITE);
                                mAnimation.setRepeatMode(Animation.REVERSE);
                                btn.startAnimation(mAnimation);
                            }
                        }
                    }
                });
            }
        });

        //broadcast to send timer to all users
        socket.on("broadcast", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                //retrive timer
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_timer.setText(new StringBuilder("Timer: ").append(args[0]));
                        // txt_result.setText("");

                    }
                });
            }
        });
        //after one round, wait 5 sec
        socket.on("wait_before_restart", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                canPlay = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        txt_timer.setText("Wait..");

                    }
                });
            }
        });

//after waiting, restart for the next round
        socket.on("restart", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                canPlay = true;
                answerChosen =false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txt_result.setVisibility(View.GONE);
                        if(args[0].toString().equals("new_question")) {
                            for (final Button btn : btnList) {
                                btn.setBackgroundResource(android.R.drawable.btn_default_small);
                                btn.clearAnimation();
                            }

                            if (++questionIndex < quizList.size()) {
                                    socket.emit("is_last_question", "false");

                                //setez noua intrebare
                                txt_question.setText(quizList.get(questionIndex).getQuestion());
                                //scot butoanele precedente ( ca poate sunt mai multe/putine)
                                ll.removeAllViews();
                                //iau nr total de rasp (gresite + 1 corect)
                                noOfAnswers = quizList.get(answerIndex + 1).getWrongArr().length + 1;
                                //fac un array cu rapsunruile
                                allAnswers = new String[noOfAnswers];
                                Log.d("aaaaaaa", String.valueOf(allAnswers.length));
                                //iau rasp corect
                                correctAnswer = quizList.get(++answerIndex).getCorrect();
                                //addAnswerBtn(correctAnswer);
                                //iau rasp gresite si le bag in array
                                for (int j = 0; j < quizList.get(answerIndex).getWrongArr().length; j++) {
                                    String incorrectAnswer = quizList.get(answerIndex).getWrongArr()[j];
                                    allAnswers[j] = incorrectAnswer;
                                    //addAnswerBtn(incorrectAnswer);
                                }
                                //bag si rasp corect dar ce naibii e indexul asta, deci ma uitla nr de rasp gresite al intrebarii curente ok lol
                                allAnswers[quizList.get(answerIndex).getWrongArr().length] = correctAnswer;
                                Log.d("bbbb", allAnswers[0]);
                                //aicia le amestec
                                ArrayList<Integer> numberList = new ArrayList<Integer>();
                                for (int i = 0; i < allAnswers.length; i++) numberList.add(i);
                                Collections.shuffle(numberList);

                                for (int answersNo = 0; answersNo < allAnswers.length; answersNo++) {
                                    String randomAnswer = allAnswers[numberList.get(answersNo)];
                                    addAnswerBtn(randomAnswer, answersNo);
                                }

                            }


                            if(questionIndex==quizList.size()-1){
                                socket.emit("is_last_question", "true");
                            }
                        }
                        else {
                            txt_question.setText("Your final score is: "+ score);
                            txt_question.append("\n"+opponentName+"'s score is: "+ String.valueOf(opponentScore));
                            txt_score.setText("");
                            txt_timer.setVisibility(View.INVISIBLE);
                            iv_msg.setVisibility(View.VISIBLE);
                            if(score < opponentScore) {
                                iv_msg.setBackgroundResource(R.drawable.failure);
                            }else{
                                iv_msg.setBackgroundResource(R.drawable.confetti);
                            }
                            ll.removeAllViews();
                        }
                    }
                });
            }
        });

//if user is winner

        socket.on("reward", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //user wins
                        txt_score.setText("Score: "+String.valueOf(score));
                       // Toast.makeText(ChallengeActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
//if user is loser
        socket.on("lose", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //user loses
                //        if(score ==0) {
                        if(args[0].toString().equals("no answer") && score !=0){
                            //didn't answer so i didnt calculate his score when he clicked btn
                                txt_score.setText("Score: " + String.valueOf(--score));

                        }else {
                            txt_score.setText("Score: " + String.valueOf(score));
                        }
                 //       }else{
                 //           txt_score.setText(String.valueOf(--score));
                //        }
                       // Toast.makeText(ChallengeActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        //if user is loser,show him who won
        socket.on("winner", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //user loses
                        String winner =  args[0].toString();
                       // Toast.makeText(ChallengeActivity.this,winner,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        socket.on("opponent_answer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       // Toast.makeText(ChallengeActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                        try{
                        JSONObject j = new JSONObject(args[0].toString());
                        String name= j.getString("name");
                        String opponent_answer = j.getString("answer");
                        //show the other's player answer
                            if(!name.equals(username)) {


                                // Toast.makeText(ChallengeActivity.this, answer, Toast.LENGTH_SHORT).show();
                                for(final Button btn : btnList) {
                                    if(btn.getText().equals(opponent_answer)){
                                        if(opponent_answer.equals(playerAnswer)){
                                            btn.setBackgroundResource(R.drawable.btn_backgr_multiplayer);
                                        }
                                        else if(color == colorPlayer1) {
                                            btn.setBackgroundColor(colorPlayer2);
                                        }
                                        else{
                                            btn.setBackgroundColor(colorPlayer1);
                                        }
                                    }
                                }

                            }
                            //Toast.makeText(ChallengeActivity.this,name,Toast.LENGTH_LONG).show();
                        } catch(Exception e){
                            Toast.makeText(ChallengeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });

        socket.on("opponent_score", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(ChallengeActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject j = new JSONObject(args[0].toString());
                            String name= j.getString("name");
                            String opponent_score = j.getString("score");
                            //show the other's player answer
                            if(!name.equals(username)) {
                                opponentScore = Integer.valueOf(opponent_score);
                               opponentName = name;
                            }
                        } catch(Exception e){
                            Toast.makeText(ChallengeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        socket.on("player_num", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int players_no = Integer.valueOf(args[0].toString());
                        if(players_no==2){
                            parseJSON();
                            txt_question.setVisibility(View.VISIBLE);
                            txt_timer.setVisibility(View.VISIBLE);
                            txt_score.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                            gameStarted = true;
                        }
                    }
                });
            }
        });

        socket.on("opponent_disconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        new AlertDialog.Builder(ChallengeActivity.this)
                .setTitle("Oops")
                .setMessage("Looks like your opponent disconnected")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        ChallengeActivity.super.onBackPressed();
                        socket.disconnect();
                    }
                }).create().show();
                    }
                });
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txt_result.setText("Disconnect");
                        txt_timer.setText("Disconnected");
                        //txt_money.setText("Disconnected");

                    }
                });
            }
        });

    }

    private void parseJSON() {

      String url = "http://" + Common.serverIP + "/quizrific/quiz2.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);

                        String question = hit.getString("question");
                        //String decodedQuestion =  Html.fromHtml((question),Html.FROM_HTML_MODE_LEGACY).toString();
                        String correctA = hit.getString("correct_answer");
                        //String decodedAnswerC =Html.fromHtml(correctA,Html.FROM_HTML_MODE_LEGACY).toString();

                        JSONArray incorrectAnswers = hit.getJSONArray("incorrect_answers");

                        String[] arrayIncorrectA = new String[incorrectAnswers.length()];
                        for (int j = 0; j < incorrectAnswers.length(); j++) {
                            //arrayIncorrectA[j] = Html.fromHtml((incorrectAnswers.get(j).toString()),Html.FROM_HTML_MODE_LEGACY).toString();;
                            arrayIncorrectA[j] = incorrectAnswers.get(j).toString();

                        }
                        Log.d("question",question);
                        Log.d("correctA",correctA);
                        quizList.add(new QuestionItem(question, correctA, arrayIncorrectA));

                    }
                    socket.emit("is_last_question","false");
                    txt_question.setText(quizList.get(questionIndex).getQuestion());
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
       // answerBtn = new MaterialButton(this);
        answerBtn.setBackgroundResource(R.drawable.answer);
        answerBtn.setText(answer);
        answerBtn.setId(answerId);
        btnList.add(answerBtn);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);        params.setMargins(30, 10, 0, 0);
        params.setMargins(10,10,10,10);
        answerBtn.setLayoutParams(params);
        ll.addView(answerBtn);

            answerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedBtn =(Button)v;
                    try{
                        if(canPlay){
                            if(!answerChosen) {
                                playerAnswer = clickedBtn.getText().toString();
                                clickedBtn.setBackgroundColor(color);
                                answerChosen = true;

                                //create json obejct to send
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",username);
                                jsonObject.put("question",txt_question.getText().toString());
                                jsonObject.put("correct_answer",correctAnswer);
                                jsonObject.put("player_answer",playerAnswer);
                                if(correctAnswer.equals(playerAnswer)) {
                                    jsonObject.put("score", ++score);
                                }else {
                                    if (score == 0) {
                                        jsonObject.put("score", score);
                                    } else{
                                        jsonObject.put("score", --score);
                                    }
                                }
                                socket.emit("client_send_answer",jsonObject);

                            }else{
                                Toast.makeText(ChallengeActivity.this,"you already chose your answer",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(ChallengeActivity.this,"please wait for next round",Toast.LENGTH_SHORT).show();
                        }

                    }catch(Exception e){
                        Toast.makeText(ChallengeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }

    @Override
    public void onBackPressed() {
        if(gameStarted) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            ChallengeActivity.super.onBackPressed();
                            socket.disconnect();
                        }
                    }).create().show();
        }else {
            ChallengeActivity.super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }


}
