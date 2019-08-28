package com.example.dcris.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.fragments.DatePickerFragment;
import com.example.dcris.myapplication.fragments.TimePickerFragment;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SchedulerActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    String quizName = null;
    String course = null;
    String username = null;

    private TextView dateTV,timeTV,titleTV,validationTV;
    private ImageButton dateIB, timeIB;
    private EditText durationET;
    private Spinner durationSpn;
    private Button scheduleBtn;

    private static final String URL = "http://" + Common.serverIP + "/quizrific/schedule_quiz.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        username = KeyValueDB.getUsername(getApplicationContext());

        dateIB = findViewById(R.id.scheduler_ib_date);
        timeIB = findViewById(R.id.scheduler_ib_time);

        dateTV = findViewById(R.id.scheduler_tv_date);
        timeTV = findViewById(R.id.scheduler_tv_time);

        titleTV = findViewById(R.id.scheduler_tv_title);

        durationSpn = findViewById(R.id.scheduler_spn_duration);

        scheduleBtn = findViewById(R.id.scheduler_btn_schedule);
        validationTV = findViewById(R.id.scheduler_tv_validate);



        final Bundle bundle = getIntent().getExtras();
        course = bundle.getString("course");
        quizName = bundle.getString("quizName");

        titleTV.setText(quizName + " for " + course);

        dateIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        timeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });


        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationTV.setText("");
                if(dateTV.getText().toString().equals("")){
                    validationTV.setText("Pick a date");
                    return;
                }
                if(timeTV.getText().toString().equals("")){
                    validationTV.setText("Pick a time");
                    return;
                }

                scheduleQuiz();
            }
        });
    }


    private void scheduleQuiz(){

        final Intent i = new Intent(this,MenuProfActivity.class);

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        dateTV.setText("");
                        timeTV.setText("");
                        //startActivity(i);
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
                hashMap.put("professor",username);
                hashMap.put("course",course);
                hashMap.put("quizName",quizName);
                hashMap.put("quizDate",dateTV.getText().toString());
                hashMap.put("quizHour",timeTV.getText().toString());
                hashMap.put("duration",durationSpn.getSelectedItem().toString());

                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        dateTV.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeTV.setText(hourOfDay +":" + minute);
    }
}
