package com.example.dcris.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatsViewerActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    String quizName;
    String course;
    private static final String urlResults = "http://" + Common.serverIP + "/quizrific/get_quiz_results.php";
    private StringRequest request;
    int countPass =0;
    int countFail =0;
    TextView passTV;
    TextView failTV;
    TextView titleTV;
    TextView descrTV;

    private ArrayList<String> res;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statsviewer);

        passTV = findViewById(R.id.statsviewer_tv_legend1);
        failTV = findViewById(R.id.statsviewer_tv_legend2);
        titleTV = findViewById(R.id.statsviewer_tv_title);
        descrTV= findViewById(R.id.statsviewer_tv_descr);

        pb = findViewById(R.id.statsviewer_pb);

        res = new ArrayList<>();
        final Bundle bundle = getIntent().getExtras();
        course = bundle.getString("course");
        quizName = bundle.getString("quizName");
        parseJSON();

        titleTV.append("\n"+quizName);
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
        yvalues.add(new PieEntry(countPass, "passed"));
        yvalues.add(new PieEntry(countFail, "failed"));

        PieDataSet dataSet = new PieDataSet(yvalues, "");
        dataSet.setSliceSpace(4);
        dataSet.setSelectionShift(5f);
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("passed");
        xVals.add("failed");

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

        int studentsNoPassed =(int)yvalues.get(0).getValue();
        int studentsNoFailed =(int)yvalues.get(1).getValue();
        if(studentsNoPassed > 1)
            passTV.setText(String.valueOf(studentsNoPassed) + " students passed\n");
        else
            passTV.setText(String.valueOf(studentsNoPassed) + " student passed\n");

        passTV.setTextColor(passed);

        if(studentsNoFailed > 1)
             failTV.setText(String.valueOf(studentsNoFailed) + " students failed");
        else
            failTV.setText(String.valueOf(studentsNoFailed) + " student failed");

        failTV.setTextColor(failed);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        int studentsNo =(int)e.getY();
        if(studentsNo == countPass) {
            goToBarStats();
        }
            //Toast.makeText(getApplicationContext(),String.valueOf(studentsNo),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
        //Log.i("PieChart", "nothing selected");
    }

    private void goToBarStats() {

       Intent intent = new Intent(getApplicationContext().getApplicationContext(), BarActivity.class);
       intent.putExtra("resArray", res);
        startActivity(intent);
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
                        //    Toast.makeText(getApplicationContext(),jresponse.getString("result"),Toast.LENGTH_SHORT).show();
                        String result = jresponse.getString("result");

                        float resultFloat = Float.parseFloat(result);
                        if(resultFloat >=5.0){
                         //   Toast.makeText(getApplicationContext(),"pass",Toast.LENGTH_SHORT).show();
                            countPass++;
                        }else{
                            countFail++;
                        }
                        res.add(result);
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
                hashMap.put("course",course);
                hashMap.put("quiz",quizName);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}