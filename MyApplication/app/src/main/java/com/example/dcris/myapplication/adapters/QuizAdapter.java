package com.example.dcris.myapplication.adapters;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.dcris.myapplication.MenuProfActivity;
import com.example.dcris.myapplication.SchedulerActivity;
import com.example.dcris.myapplication.fragments.DatePickerFragment;
import com.example.dcris.myapplication.QuizCreatorActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuizItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ExampleViewHolder> implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Context mContext;
    private ArrayList<QuizItem> mQuizList;
    private OnItemClickListener mListener;
    private StringRequest request;
    private static final String URL = "http://" + Common.serverIP + "/quizrific/delete_quizzes.php";
    private String currentDate;
    String currentTime;
    private String username;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public QuizAdapter(Context context, ArrayList<QuizItem> quizList) {
        mContext = context;
        mQuizList = quizList;
        username = KeyValueDB.getUsername(mContext);
        setHasStableIds(true);
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.quiz_item, parent, false);
        return new ExampleViewHolder(v);
    }

    public void deleteItem(final int position){
        final QuizItem mRecentlyDeletedItem = mQuizList.get(position);
       final int mRecentlyDeletedItemPosition = position;
        final String quizName = mRecentlyDeletedItem.getQuiz();
        final String course = mRecentlyDeletedItem.getCourse();

        mQuizList.remove(position);
        notifyItemRemoved(position);
       //notifyItemRangeRemoved(position, position);
        //notifyItemChanged(position);
        //notifyDataSetChanged();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete this quiz and all its related data (questions, student results)?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.names().get(0).equals("success")){
                                        Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                                        undoDelete(mQuizList,mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
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
                                return hashMap;
                                //return super.getParams();
                            }
                        };
                        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                      //  mQuizList.add(mRecentlyDeletedItemPosition,
                      //          mRecentlyDeletedItem);
                      //  notifyItemInserted(position);
                        undoDelete(mQuizList,mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        final QuizItem currentItem = mQuizList.get(position);

        final String quizName = currentItem.getQuiz();
        final String course = currentItem.getCourse();
        int questionsCount = currentItem.getQuestionsCount();

        holder.mTextViewQuiz.setText(quizName);
        holder.mTextViewCourse.setText("Course: " + course);
        holder.mTextViewQuestions.setText("Questions: " + questionsCount);

        holder.mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.mButtonAdd.getContext(), QuizCreatorActivity.class);
                intent.putExtra("course", course );
                intent.putExtra("quizName", quizName);
                v.getContext().startActivity(intent);


            }
        });

        holder.mButtonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // DialogFragment datePicker = new DatePickerFragment();
               // datePicker.show( ((AppCompatActivity) mContext).getSupportFragmentManager(), "time picker");

                Intent intent = new Intent(holder.mButtonSchedule.getContext(), SchedulerActivity.class);
                intent.putExtra("course", course );
                intent.putExtra("quizName", quizName);
                v.getContext().startActivity(intent);

                // Toast.makeText(holder.mButtonSchedule.getContext(), currentDate,Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mQuizList.size();
    }

    
    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewQuiz;
        public TextView mTextViewCourse;
        public TextView mTextViewQuestions;
        public ImageButton mButtonAdd;
        public ImageButton mButtonSchedule;


        public ExampleViewHolder(View itemView) {
            super(itemView);

            mTextViewQuiz = itemView.findViewById(R.id.quizItem_tv_quiz);
            mTextViewCourse = itemView.findViewById(R.id.quizItem_tv_course);
            mTextViewQuestions = itemView.findViewById(R.id.quizItem_tv_questions);
            mButtonAdd = itemView.findViewById(R.id.quizItem_btn_add);
            mButtonSchedule = itemView.findViewById(R.id.quizItem_btn_schedule);

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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void undoDelete( ArrayList<QuizItem> mListItems, final int mRecentlyDeletedItemPosition, final QuizItem mRecentlyDeletedItem) {



       /* ArrayList<QuizItem> res = new ArrayList();
        mListItems.add(mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
        for(int i=0; i< mListItems.size();i++)
          res.add(mListItems.get(i));  //add item or modify list
        mListItems = res;
        notifyDataSetChanged();*/

      mListItems.add(mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
      notifyItemInserted(mRecentlyDeletedItemPosition);
       // notifyItemChanged(mRecentlyDeletedItemPosition);
      //notifyDataSetChanged();
       //

        //   notifyItemRangeInserted(mRecentlyDeletedItemPosition, mListItems.size());
      // notifyItemChanged(mRecentlyDeletedItemPosition);

    }

}