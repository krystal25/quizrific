package com.example.dcris.myapplication.adapters;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.QuizCreatorActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.SchedulerActivity;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuizItem;
import com.example.dcris.myapplication.model.ScheduledItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ScheduledAdapter extends RecyclerView.Adapter<ScheduledAdapter.ExampleViewHolder> implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Context mContext;
    private ArrayList<ScheduledItem> mQuizList;
    private OnItemClickListener mListener;
    private StringRequest request;
    private String currentDate;
    String currentTime;
    private String username;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ScheduledAdapter(Context context, ArrayList<ScheduledItem> quizList) {
        mContext = context;
        mQuizList = quizList;
        username = KeyValueDB.getUsername(mContext);
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.scheduled_item, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        ScheduledItem currentItem = mQuizList.get(position);

        final String quizName = currentItem.getQuiz();
        final String course = currentItem.getCourse();
        final String dateStr = currentItem.getDate();
        final String time= currentItem.getTime();
        final String duration = currentItem.getDuration();

        holder.mTextViewQuiz.setText(quizName);
        holder.mTextViewCourse.setText("Course: " + course);
        holder.mTextViewDate.setText("Date: " + dateStr);
        holder.mTextViewTime.setText("Time: " + time);
        holder.mTextViewDuration.setText("Duration: " +duration);

        Date dateBD=null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {

            dateBD = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currDate = null;
        //get local date on device
        String currDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            currDate = format.parse(currDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date timeBD=null;
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        try {
            timeBD = format2.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date strLocalTime = getCurrentTime();
        if (dateBD.compareTo(currDate) == 0) {//quiz today

            Calendar cal = Calendar.getInstance();
            cal.setTime(timeBD);
            cal.add(Calendar.MINUTE, Integer.parseInt(duration));
            String newTime = format2.format(cal.getTime());

            Date end=null;
            try {
                end = format2.parse(newTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if((strLocalTime.after(timeBD) && strLocalTime.before(end)) || strLocalTime.compareTo(timeBD)==0)  { //quiz ongoing
                final int correctColor= ContextCompat.getColor(mContext, R.color.correctAnswer);
                holder.mTextViewQuiz.setTextColor(correctColor);
                blink(holder.mTextViewQuiz);
            }else  if(timeBD.compareTo(strLocalTime)<0) { //was todat at an earlier hour
                Log.d("datex", dateBD.toString());
                holder.mTextViewQuiz.setPaintFlags(holder.mTextViewQuiz.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                //if quiz is today in a few mins/hours
                final int correctColor= ContextCompat.getColor(mContext, R.color.correctAnswer);
                holder.mTextViewQuiz.setTextColor(correctColor);
            }
        }else if((dateBD.compareTo(currDate) < 0)){
            holder.mTextViewQuiz.setPaintFlags(holder.mTextViewQuiz.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return mQuizList.size();
    }

    
    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewQuiz;
        public TextView mTextViewCourse;
        public TextView mTextViewDate;
        public TextView mTextViewTime;
        public TextView mTextViewDuration;


        public ExampleViewHolder(View itemView) {
            super(itemView);

            mTextViewQuiz = itemView.findViewById(R.id.scheduledItem_tv_quiz);
            mTextViewCourse = itemView.findViewById(R.id.scheduledItem_tv_course);
            mTextViewDate = itemView.findViewById(R.id.scheduledItem_tv_date);
            mTextViewTime = itemView.findViewById(R.id.scheduledItem_tv_time);
            mTextViewDuration = itemView.findViewById(R.id.scheduledItem_tv_duration);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        currentDate = DateFormat.getDateInstance().format(c.getTime());
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        currentTime ="Hour: " + hourOfDay + " Minute: " + minute;
    }

    public Date getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strTime= mdformat.format(calendar.getTime());
        Date d = null;
        try {
            d = mdformat.parse(strTime);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    private void blink(final TextView tv){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(tv.getVisibility() == View.VISIBLE){
                            tv.setVisibility(View.INVISIBLE);
                        }else{
                            tv.setVisibility(View.VISIBLE);
                        }
                        blink(tv);
                    }
                });
            }
        }).start();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}