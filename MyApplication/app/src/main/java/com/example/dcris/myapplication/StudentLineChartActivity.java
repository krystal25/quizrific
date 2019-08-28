package com.example.dcris.myapplication;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StudentLineChartActivity extends AppCompatActivity {

    String course;
    private static final String urlResults = "http://" + Common.serverIP + "/quizrific/get_results_linechart.php";
    private StringRequest request;
    private ArrayList<String> res;
    private ProgressBar pb;
    private String username;

    String result;
    String quiz;
    String quizzes[];
    String resultsArray[];
    String quiz_date;
    HashMap<String,Float> results= new HashMap<String,Float>();

    LineChart chart;
    int test=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);


        final Bundle bundle = getIntent().getExtras();
        course = bundle.getString("course");

        username = KeyValueDB.getUsername(this);
        chart = findViewById(R.id.linechart);

        parseJSON();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createLine();
            }
        }, 2000); //wait for 3sec to let the connection be made

    }

    private void parseJSON(){
        // prepare the Request
        request = new StringRequest(Request.Method.POST, urlResults, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j= new JSONArray(response);
                    quizzes = new String[j.length()];
                    resultsArray = new String[j.length()];
                    for(int i=0;i<j.length();i++){
                        JSONObject jresponse = j.getJSONObject(i);
                      result = jresponse.getString("result");
                      quiz = jresponse.getString("quiz");
                      resultsArray[i]=result;
                      quizzes[i] = quiz;
                      Log.d("quizz", quizzes[i]);
                      results.put(quiz,Float.parseFloat(result));
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
                hashMap.put("course",course);
                hashMap.put("username",username);
                return hashMap;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void createLine(){

        chart.getDescription().setEnabled(false);


        ArrayList<Entry> entries = new ArrayList<>();

        //deci pe x trb sa am quiz-urile sau data lor si pe y notele

        for (int i=0;i<resultsArray.length;i++) {
           entries.add(new Entry(Float.valueOf(i),Float.parseFloat(resultsArray[i])));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //quizzes = new String[]{"Jan", "Feb", "Mar", "Apr"};
        xAxis.setLabelCount(quizzes.length);
        xAxis.setLabelRotationAngle(-30f);

        final ArrayList<String> xLabel = new ArrayList<>();
        for(int i=0; i<quizzes.length; i++) {
            xLabel.add(quizzes[i]);
            //xLabel.add("Midterm Exam");
           // xLabel.add("Final Quiz");
        }
        xAxis.setGranularity(1);
        xAxis.setTextSize(10);
        xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                //    Log.d("quizz", String.valueOf(value));
                    return xLabel.get((int)value);
                }
            });

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularityEnabled(true);
        dataSet.setValueTextSize(15);
        final int failed= ContextCompat.getColor(this, R.color.colorAccent);
        final int x= ContextCompat.getColor(this, R.color.colorPrimary);
        dataSet.setCircleColor(x);
        dataSet.setColor(failed);
        LineData data = new LineData(dataSet);

        chart.setData(data);
        chart.animateX(2500);
        chart.invalidate();
    }
}