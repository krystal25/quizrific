package com.example.dcris.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StudentStatsActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    String quizName;
    String course;
    private static final String urlResults = "http://" + Common.serverIP + "/quizrific/get_results_student.php";
    private StringRequest request;
    TextView passTV;
    TextView failTV;
    TextView titleTV;
    TextView descrTV;

    private ArrayList<String> res;
    private ProgressBar pb;
    private String username;
    int correctAnswers;
    String totalAnswers;
    String wrongAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statsviewer);

        final Bundle bundle = getIntent().getExtras();
        course = bundle.getString("course");
        quizName = bundle.getString("quizName");
        username = KeyValueDB.getUsername(this);
        passTV = findViewById(R.id.statsviewer_tv_legend1);
        failTV = findViewById(R.id.statsviewer_tv_legend2);
        titleTV = findViewById(R.id.statsviewer_tv_title);
        descrTV= findViewById(R.id.statsviewer_tv_descr);
        descrTV.setText("Click here to see a detailed chart for this course");
        descrTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StudentLineChartActivity.class);
                intent.putExtra("course", course );
                intent.putExtra("quizName", quizName);
                startActivity(intent);
            }
        });
        pb = findViewById(R.id.statsviewer_pb);

        res = new ArrayList<>();

        parseJSON();

        titleTV.setText(quizName + " " + course);
        titleTV.setGravity(Gravity.CENTER);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createPie();
            }
        }, 2000); //wait for 3sec to let the connection be made



    }

    private void createPie(){

        pb.setVisibility(View.GONE);
        titleTV.setVisibility(View.VISIBLE);
        descrTV.setVisibility(View.VISIBLE);
        passTV.setVisibility(View.VISIBLE);
        failTV.setVisibility(View.VISIBLE);

       // Toast.makeText(getApplicationContext(),String.valueOf(res.size()),Toast.LENGTH_SHORT).show();
        PieChart pieChart = findViewById(R.id.pie);
        pieChart.setVisibility(View.VISIBLE);
        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
        yvalues.add(new PieEntry(correctAnswers, "correct"));
        yvalues.add(new PieEntry(Integer.parseInt(wrongAnswers), "wrong"));

        PieDataSet dataSet = new PieDataSet(yvalues, "");
        dataSet.setSliceSpace(4);
        dataSet.setSelectionShift(5f);
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("correct");
        xVals.add("wrong");

        final int failed= ContextCompat.getColor(this, R.color.colorAccent);
        final int passed=ContextCompat.getColor(this, R.color.correctAnswer);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(passed);
        colors.add(failed);


        PieData data = new PieData(dataSet);

        // In Percentage term
        data.setValueFormatter(new PercentFormatter(pieChart));
        // Default value
        // data.setValueFormatter(new DefaultValueFormatter(1));
        pieChart.setUsePercentValues(true);
        pieChart.setData(data);
        pieChart.getDescription().setText(" ");
        pieChart.setRotationEnabled(false);
        //pieChart.getDescription().setTextSize(30);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(28f);
        pieChart.setHoleRadius(25f);
        pieChart.setDrawEntryLabels(true); //passed / failed on pie
        pieChart.setEntryLabelTextSize(15);
        pieChart.setEntryLabelColor(Color.DKGRAY);

        dataSet.setColors(colors);
        // dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        data.setValueTextSize(20f);
        data.setValueTextColor(Color.DKGRAY);
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateXY(1400, 1400);

        pieChart.getLegend().setEnabled(false);
       /*
        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);*/
        // legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        int correctA =(int)yvalues.get(0).getValue();
        int wrongA =(int)yvalues.get(1).getValue();
        if(correctA > 1)
            passTV.setText(String.valueOf(correctA) + " correct answers \n");
        else
            passTV.setText(String.valueOf(correctA) + " correct answer \n");

        passTV.setTextColor(passed);

        if(wrongA > 1)
             failTV.setText(String.valueOf(wrongA) + " wrong answers");
        else
            failTV.setText(String.valueOf(wrongA) + " wrong answer");

        failTV.setTextColor(failed);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
    }

    @Override
    public void onNothingSelected() {
        //Log.i("PieChart", "nothing selected");
    }


    private void parseJSON(){

        // prepare the Request
        request = new StringRequest(Request.Method.POST, urlResults, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j= new JSONArray(response);
                    for(int i=0;i<j.length();i++){
                        JSONObject jresponse = j.getJSONObject(i);
                      totalAnswers = jresponse.getString("questionsNo");
                      wrongAnswers = jresponse.getString("wrong");
                    }

                    correctAnswers = Integer.parseInt(totalAnswers) - Integer.parseInt(wrongAnswers);

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
                hashMap.put("course",course);
                hashMap.put("quiz",quizName);
                hashMap.put("student", username);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}