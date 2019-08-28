package com.example.dcris.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizCreatorActivity extends AppCompatActivity {

    private EditText questionET;
    private EditText pointsET;
    private EditText answerDynamET;
    private CheckBox correctDynamCB;
    private ImageButton addAnswerBtn, addFileBtn;
    private Button insertQuestionBtn;
    private LinearLayout ll;
    private LinearLayout ll2;
    private TextView validateTV;

    private static final String URL_insert = "http://" + Common.serverIP + "/quizrific/insert_questions.php";
    private static final String URL_update = "http://" + Common.serverIP + "/quizrific/update_questions.php";
    private StringRequest request;

    String quizName = null;
    String course = null;
    String username = null;

    int answerId = 0;
    int answersNo = 0;

    List<EditText> allETs = new ArrayList<>();
    List<CheckBox> allCBs = new ArrayList<>();
    List<ImageButton> allRBs = new ArrayList<>();

    String question = null;
    String points = null;

    ImageButton removeAnswer;
    int correctNo = 0;
    int MAX_OPTIONS = 5;

    String questionBundle = null;
    String pointsBundle= null;
    String[] correctArr;
    String[] wrongArr;

    private static final int REQUEST_CODE =43;
    private static final int CODE_GALLERY_REQUEST =999;
    private ImageView iv;
    private LinearLayout imageLayout;
    String filepath;
    Bitmap image;
    Boolean addedImage = false;

    String[] init_answersArray;
    String[] init_isCorrectArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_creator);

        username = KeyValueDB.getUsername(getApplicationContext());

        addAnswerBtn = findViewById(R.id.quizCreator_btn_addAnswer);
        addFileBtn = findViewById(R.id.quizCreator_btn_addFile);
        imageLayout = findViewById(R.id.lin_iv);

        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedImage=true;
                startSearch();
            }
        });

        insertQuestionBtn = findViewById(R.id.quizCreator_btn_insertQuestion);
        questionET = findViewById(R.id.quizCreator_et_question);
        pointsET = findViewById(R.id.quizCreator_et_points);
        validateTV = findViewById(R.id.quizCreator_tv_validate);

        ll2 = findViewById(R.id.lin_vert);


        final Bundle bundle = getIntent().getExtras();
        if(bundle.size()==2) {
            course = bundle.getString("course");
            quizName = bundle.getString("quizName");

            //two options mandatory

            addAnswer("",0);
            addAnswer("",0);
        }else if(bundle.size()==4){
            questionBundle = bundle.getString("question");
            pointsBundle = bundle.getString("points");
            correctArr = bundle.getStringArray("correct");
            wrongArr = bundle.getStringArray("wrong");
            insertQuestionBtn.setText("Edit question");
            questionET.setText(questionBundle);
            pointsET.setText(pointsBundle);

            init_answersArray = new String[(correctArr.length + wrongArr.length)];
            init_isCorrectArray = new String[(correctArr.length + wrongArr.length)];
            int i =0;
            for(int correctIdx=0; correctIdx<correctArr.length;correctIdx++){
               addAnswer(correctArr[correctIdx], 1);
               init_answersArray[i] = correctArr[correctIdx];
               init_isCorrectArray[i] = "1";
               i++;
            }

            for(int wrongIdx=0; wrongIdx<wrongArr.length;wrongIdx++){
               addAnswer(wrongArr[wrongIdx], 0);
               init_answersArray[i] = wrongArr[wrongIdx];
               init_isCorrectArray[i] = "0";
               i++;
            }

        }

        addAnswerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(answersNo < MAX_OPTIONS)
                    addAnswer("",0);
                else
                    Toast.makeText(getApplicationContext(),"no more than " + MAX_OPTIONS + " options",Toast.LENGTH_LONG).show();
            }
        });



        insertQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //for empty data
                if(questionET.getText().toString().equals("") || pointsET.getText().toString().equals("")){
                    validateTV.setText("Complete all fields");
                    return;
                }
                //inputType number does its job for checking whether pointsET is numeric

                String[] answersArray = new String[(allETs.size())];
                boolean[] cbsArray = new boolean[(allETs.size())];

                for(int answerIndex=0; answerIndex < allETs.size(); answerIndex++){
                    answersArray[answerIndex] = allETs.get(answerIndex).getText().toString();
                    //if answers are empty strings
                    if(answersArray[answerIndex].equals("")){
                        validateTV.setText("Complete all fields");
                        return;
                    }
                    cbsArray[answerIndex] = allCBs.get(answerIndex).isChecked();

                    //questionET.append(answersArray[answerIndex]);
                    if(cbsArray[answerIndex]){
                        correctNo++;
                    }
                }
                //check if two answers are repeated
                for (int i = 0; i < allETs.size(); i++) {
                    for (int j = i+1; j < allETs.size(); j++) {
                        // compare list.get(i) and list.get(j)
                        String option = allETs.get(i).getText().toString();
                        String option2 =  allETs.get(j).getText().toString();
                        if(option.equals(option2)){
                            validateTV.setText("No duplicate answers");
                            return;
                        }
                    }
                }

                //in case all answers are correct
                if(correctNo == allCBs.size()) {
                    validateTV.setText("At least 1 option has to be wrong");
                    return;
                }

                //in case no answer is correct
                if(correctNo==0){
                    validateTV.setText("At least 1 option has to be correct");
                    return;
                }


                // insert stuff
                question = questionET.getText().toString();
                points = pointsET.getText().toString();

                if(bundle.size()==2) {
                    insertQuestion(answersArray, cbsArray, question, points);
                }else if(bundle.size()==4){
                    editQuestion(answersArray,cbsArray,question,points);
                }
             clearViews();

            }
        });

    }

    private void insertQuestion(final String[] answer, final boolean[] isCorrect,final String question, final String points){
        request = new StringRequest(Request.Method.POST, URL_insert, new Response.Listener<String>() {
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
                hashMap.put("quiz_name",quizName);
                hashMap.put("professor",username);
                hashMap.put("course",course);
                hashMap.put("question",question);
                hashMap.put("points",points);
                if(addedImage) {
                    String imageData = imageToString(image);
                    hashMap.put("image", imageData);
               }
                for(int answerIndex=0; answerIndex < answer.length; answerIndex++) {
                    hashMap.put("answer["+answerIndex+"]", answer[answerIndex]);
                    hashMap.put("is_correct["+answerIndex+"]", String.valueOf(isCorrect[answerIndex]));
                }

                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void editQuestion(final String[] answer, final boolean[] isCorrect,final String question, final String points){
        request = new StringRequest(Request.Method.POST, URL_update, new Response.Listener<String>() {
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
                hashMap.put("init_question",questionBundle);
                hashMap.put("question",question);
                hashMap.put("init_points",pointsBundle);
                hashMap.put("points",points);

                if(addedImage) {
                    String imageData = imageToString(image);
                    hashMap.put("image", imageData);
                }
                for(int answerIndex=0; answerIndex < answer.length; answerIndex++) {
                    if(answerIndex< init_answersArray.length) {
                        hashMap.put("init_answer[" + answerIndex + "]", init_answersArray[answerIndex]);
                        hashMap.put("answer[" + answerIndex + "]", answer[answerIndex]);
                        hashMap.put("init_is_correct[" + answerIndex + "]", init_isCorrectArray[answerIndex]);
                        hashMap.put("is_correct[" + answerIndex + "]", String.valueOf(isCorrect[answerIndex]));
                    }else{
                        // newly added options
                        hashMap.put("answer[" + answerIndex + "]", answer[answerIndex]);
                        hashMap.put("is_correct[" + answerIndex + "]", String.valueOf(isCorrect[answerIndex]));
                    }
                }

                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void addAnswer(String answer, int checked){

        ll = new LinearLayout(this);
        ll.setId(answerId); //0,1,2,3..
        ll.setTag("ll"+answerId);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER);

        answerDynamET = new EditText(this);
        answerDynamET.setText(answer);
        allETs.add(answerDynamET);
        answerDynamET.setTag(answerId);
        answerDynamET.setHint("Answer");
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(700, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerDynamET.setLayoutParams(params);

        correctDynamCB = new CheckBox(this);
        correctDynamCB.setTag("cb"+answerId);
        if(checked ==1){
            correctDynamCB.setChecked(true);
        }else{
            correctDynamCB.setChecked(false);
        }
        allCBs.add(correctDynamCB);

        removeAnswer = new ImageButton(this);
        removeAnswer.setBackgroundResource(R.drawable.ic_delete_accent_24dp);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(120, 120);
        removeAnswer.setLayoutParams(lp);
        removeAnswer.setId(answerId);
        allRBs.add(removeAnswer);

            removeAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton clickedBtn = (ImageButton) v;
                    View layoutParent = (View) clickedBtn.getParent();
                    int parentId = layoutParent.getId();
                   // questionET.append(String.valueOf(parentId));
                    if (clickedBtn.getId() == parentId) {
                        ll = findViewById(layoutParent.getId());

                        answerDynamET = ll.findViewWithTag(parentId);
                        correctDynamCB = ll.findViewWithTag("cb" + parentId);

                        allETs.remove(answerDynamET);
                        allCBs.remove(correctDynamCB);
                        allRBs.remove(clickedBtn);
                        answersNo--;
                        disableAnsw(answersNo);

                        ll.removeAllViews();
                       // answerId--;

                    }
                }
            });
        disableAnsw(++answersNo);
        //questionET.append(String.valueOf(answersNo));
        ll.addView(answerDynamET);
        ll.addView(correctDynamCB);
        ll.addView(removeAnswer);

        ll2.addView(ll);

        answerId++;
    }

    private void disableAnsw(int answersNo){

        //removeBtn not available for first 2 options

        //2 options
        if(answersNo ==2){
            for(int i=0;i<2;i++) {
                allRBs.get(i).setEnabled(false);
                allRBs.get(i).getBackground().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_IN);
            }
            //more than 2 options
        }else if(answersNo >2){
            for(int i=0;i<2;i++)
                allRBs.get(i).setEnabled(true);
        }else {
            allRBs.get(0).setEnabled(false);
            allRBs.get(0).getBackground().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_IN);
        }
    }

    private void clearViews(){

        validateTV.setText("");

        for(int i= allRBs.size()-1; i>=2; i--){
          allRBs.get(i).performClick();
       }

        answersNo=0;
        correctNo=0;

       for(int j=0; j<2; j++){
           allETs.get(j).setText("");
           if(allCBs.get(j).isChecked())
               allCBs.get(j).setChecked(false);
           disableAnsw(++answersNo);
       }
       allETs.subList(2,allETs.size()).clear();
       allCBs.subList(2,allCBs.size()).clear();
       allRBs.subList(2,allRBs.size()).clear();
       questionET.setText("");
       pointsET.setText("");
        imageLayout.removeAllViews();

    }

    private void startSearch(){

      //  ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
       //         CODE_GALLERY_REQUEST);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,REQUEST_CODE);
        //removing the already existing one
        imageLayout.removeAllViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode  == Activity.RESULT_OK){
            if(data!= null){
                Uri uri = data.getData();
                InputStream inputStream;
                try{
                    inputStream = getContentResolver().openInputStream(uri);
                    image = BitmapFactory.decodeStream(inputStream);
                    ImageView iv = new ImageView(this);
                    Display display = getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, 500);
                    iv.setLayoutParams(lp);
                    iv.setImageBitmap(image);
                  //  Glide.with(this).load(uri).into(iv);
                    imageLayout.addView(iv);
                   // filepath = uri.getPath();
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImg = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImg;
    }
}
