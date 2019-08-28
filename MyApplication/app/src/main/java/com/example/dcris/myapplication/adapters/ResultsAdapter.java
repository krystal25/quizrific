package com.example.dcris.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.QuizCreatorActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<QuestionItem> mExampleList;
    private OnItemClickListener mListener;
    private ResultsAdapter adapter;
    private StringRequest request;
    private String question;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ResultsAdapter(Context context, ArrayList<QuestionItem> exampleList) {
        mContext = context;
        mExampleList = exampleList;
        setHasStableIds(true);
        this.adapter = this;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.question_item, parent, false);
        return new ExampleViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        final QuestionItem currentItem = mExampleList.get(position);

        final String questionName = currentItem.getQuestion();
        final String points = currentItem.getPoints();
        final String imageStr = currentItem.getImage();

        if(imageStr.equals("null")){
        }else{
            Picasso.get().load(imageStr).into(holder.mImageViewImage);
        }

        final String[] correctArr = currentItem.getCorrectArr();
        final String[] incorrectArr = currentItem.getWrongArr();

        final int wrongColor= ContextCompat.getColor(holder.mTextViewQuestion.getContext(), R.color.wrongAnswer);
        final int correctColor=ContextCompat.getColor(holder.mTextViewQuestion.getContext(), R.color.correctAnswer);
        holder.mTextViewQuestion.setText(questionName);
        holder.mTextViewPoints.setText("Question Points: " + points);

        //student answ
        for(int correctIdx=0; correctIdx < correctArr.length; correctIdx++) {
            //tv containing the answer
           final TextView tv = new TextView(mContext);
            tv.setText(correctArr[correctIdx]);
            tv.setTextSize(18);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                    (850,(int) ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            //layout containg tv
            LinearLayout tv_ib_ll =  new LinearLayout(mContext);
            tv_ib_ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv_ib_ll.setOrientation(LinearLayout.HORIZONTAL);

            tv_ib_ll.addView(tv);


            //tv containing the points
            final TextView tv2 = new TextView(mContext);
            tv2.setText(incorrectArr[correctIdx]);
            tv2.setTextSize(18);
            tv2.setTypeface(null, Typeface.BOLD);

            for(int idx2=0; idx2 < correctArr.length; idx2++) {
                if (tv2.getText().toString().equals("0")) {
                    tv.setTextColor(wrongColor);
                    break;
                } else {
                    tv.setTextColor(correctColor);
                }
            }

            tv2.setLayoutParams(params);
            tv_ib_ll.addView(tv2);

            holder.ll.addView(tv_ib_ll);

        }



    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewQuestion;
        public TextView mTextViewPoints;
        public ImageView mImageViewImage;
        public ImageButton mImageButtonEdit;
        LinearLayout ll;


        public ExampleViewHolder(View itemView) {
            super(itemView);

            mTextViewQuestion = itemView.findViewById(R.id.questionItem_tv_question);
            mTextViewPoints = itemView.findViewById(R.id.questionItem_tv_points);
            mImageViewImage = itemView.findViewById(R.id.questionItem_iv_image);
            mImageButtonEdit = itemView.findViewById(R.id.questionItem_btn_edit);
            mImageButtonEdit.setVisibility(View.INVISIBLE);
            ll = itemView.findViewById(R.id.ll_answers);

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}
