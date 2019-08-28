package com.example.dcris.myapplication;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class BarActivity extends AppCompatActivity {

    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;
    private ArrayList<String> res;
    HashMap<String, Integer> gradesStudents= new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        chart = (BarChart) findViewById(R.id.barchart);

        final Bundle bundle = getIntent().getExtras();
        res = bundle.getStringArrayList("resArray");
        int count;
        for(int i=0; i<res.size(); i++){
            if(Float.parseFloat(res.get(i)) < 5) {
                Log.d("hello", " value: " + res.get(i));
                gradesStudents.remove(res.get(i));
            }else{
                count =0; //count how many students got the same grade so have to reinitialize it for a new grade
                for(int j=0; j<res.size(); j++) {
                    if (res.get(i).equals(res.get(j))) {
                        count++;
                        gradesStudents.put(res.get(i), count);
                    }
                }
            }
        }

        // Print keys and values
        for (String i : gradesStudents.keySet()) {
            Log.d("keymap","key: " + i + " value: " + gradesStudents.get(i));
        }

        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

       AddValuesToBARENTRY();
        AddValuesToBarEntryLabels();

        final int color1= ContextCompat.getColor(this, R.color.colorBar5);
        final int color2=ContextCompat.getColor(this, R.color.colorBar3);
        final int color3=ContextCompat.getColor(this, R.color.colorBar4);
        final int color4=ContextCompat.getColor(this, R.color.colorBar2);
        final int color5=ContextCompat.getColor(this, R.color.colorBar6);
        final int color6=ContextCompat.getColor(this, R.color.colorBar);

        Bardataset = new BarDataSet(BARENTRY, "Results");
       // MyBarDataSet set = new MyBarDataSet(BARENTRY, "Results");
       // set.setColors(color1, color2, color3);
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(Bardataset);

        BARDATA = new BarData( Bardataset);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(color1);
        colors.add(color2);
        colors.add(color3);
        colors.add(color4);
        colors.add(color5);
        colors.add(color6);
        Bardataset.setColors(colors);

        BARDATA.setBarWidth(0.3f);
        chart.setData(BARDATA);
        chart.getDescription().setText("");    // Hide the description
        chart.getLegend().setEnabled(false);   // Hide the legend
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setFitBars(true);
        chart.setDrawValueAboveBar(true);
        //chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setAxisMinValue(0f);
        chart.getAxisLeft().setAxisMaxValue(10f);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getXAxis().setDrawGridLines(true);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

       // chart.setVisibleXRange(5,10);

       // chart.getAxisLeft().setGranularity(1f);
        XAxis x= chart.getXAxis();
        x.setLabelCount(5);
        x.setAxisMaxValue(10);
        x.setAxisMinValue(5);

        // chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(BarEntryLabels));
        chart.animateY(3000);

    }


    public void AddValuesToBARENTRY(){
        //pe y am nr de studenti pe x notele de la 5-10
        //2 studenti au luat 5

        for (String i : gradesStudents.keySet()) {
            BARENTRY.add(new BarEntry(Float.parseFloat(i), gradesStudents.get(i)));
        }

       /*
        for(int i=0; i<res.size(); i++){
            BARENTRY.add(new BarEntry(Float.parseFloat(res.get(i)), 2));
        }
        */

      /*  BARENTRY.add(new BarEntry(5f, 2));
        BARENTRY.add(new BarEntry(6f, 8));
        BARENTRY.add(new BarEntry(7f, 6));
        BARENTRY.add(new BarEntry(8f, 5));
        BARENTRY.add(new BarEntry(9f, 7));
        BARENTRY.add(new BarEntry(10f, 5));
        */
    }


    public void AddValuesToBarEntryLabels(){
        BarEntryLabels.add("");
        BarEntryLabels.add("");
        BarEntryLabels.add("");
        BarEntryLabels.add("");
        BarEntryLabels.add("");
        BarEntryLabels.add("");
    }


}