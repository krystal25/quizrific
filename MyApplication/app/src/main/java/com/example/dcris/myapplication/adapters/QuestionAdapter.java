package com.example.dcris.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.dcris.myapplication.QuestionsActivity;
import com.example.dcris.myapplication.QuizCreatorActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuestionItem;
import com.example.dcris.myapplication.model.QuizItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<QuestionItem> mExampleList;
    private OnItemClickListener mListener;
    private QuestionAdapter adapter;
    private StringRequest request;
    private String init_question;
    private String updated_question;
    String init_points;
    String updated_points;
    Boolean updated =false;
    private static final String URLquest = "http://" + Common.serverIP + "/quizrific/update_question.php";
    private static final String URLpoints = "http://" + Common.serverIP + "/quizrific/update_question_points.php";
    private static final String URLansw = "http://" + Common.serverIP + "/quizrific/update_question_answer.php";
    private static final String URLdelete = "http://" + Common.serverIP + "/quizrific/delete_answer.php";
    private static final String URLdeleteQUESTION = "http://" + Common.serverIP + "/quizrific/delete_questions.php";


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public QuestionAdapter(Context context, ArrayList<QuestionItem> exampleList) {
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

    public void deleteItem(final int position){
        final QuestionItem mRecentlyDeletedItem = mExampleList.get(position);
        final int mRecentlyDeletedItemPosition = position;
        final String question = mRecentlyDeletedItem.getQuestion();

        mExampleList.remove(position);
        notifyItemRemoved(position);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete this question?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


        request = new StringRequest(Request.Method.POST, URLdeleteQUESTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        //notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                hashMap.put("question",question);
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
                        undoDelete(mExampleList,mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        final QuestionItem currentItem = mExampleList.get(position);

        final String questionName = currentItem.getQuestion();
        final String points = currentItem.getPoints();
        final String imageStr = currentItem.getImage();

        Bitmap image = StringToBitMap(imageStr);

        if(imageStr.equals("null")){

        }else{
           // holder.mImageViewImage.setImageBitmap(image);
            Picasso.get().load(imageStr).into(holder.mImageViewImage);
        }

        final String[] correctArr = currentItem.getCorrectArr();
        final String[] incorrectArr = currentItem.getWrongArr();

        final int wrongColor= ContextCompat.getColor(holder.mTextViewQuestion.getContext(), R.color.wrongAnswer);
        final int correctColor=ContextCompat.getColor(holder.mTextViewQuestion.getContext(), R.color.correctAnswer);
        holder.mTextViewQuestion.setText(questionName);

        holder.mTextViewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_question = holder.mTextViewQuestion.getText().toString();
                holder.mTextViewQuestion.setVisibility(View.GONE);
                holder.mEditTextQuestion.setText( holder.mTextViewQuestion.getText().toString());
                holder.mEditTextQuestion.setVisibility(View.VISIBLE);
                holder.mEditTextQuestion.requestFocus();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.mEditTextQuestion, InputMethodManager.SHOW_IMPLICIT);

                holder.mEditTextQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            // code to execute when EditText loses focus
                            updated_question =holder.mEditTextQuestion.getText().toString();
                            holder.mTextViewQuestion.setText(updated_question);
                            holder.mTextViewQuestion.setVisibility(View.VISIBLE);
                            holder.mEditTextQuestion.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(holder.mTextViewQuestion.getWindowToken(), 0);

                            //also handle php script!!!
                            if(!init_question.equals(updated_question)) {
                                updateQuestion(init_question,updated_question);
                            }
                        }
                    }
                });
            }
        });
        holder.mTextViewPoints.setText("Points: " + points);

        holder.mTextViewPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String question = holder.mTextViewQuestion.getText().toString();

                holder.mTextViewPoints.setVisibility(View.GONE);
                holder.mTextViewPoints.setText(points);
                init_points = holder.mTextViewPoints.getText().toString();
                holder.mEditTextPoints.setText( holder.mTextViewPoints.getText().toString());
                holder.mEditTextPoints.setVisibility(View.VISIBLE);
                holder.mEditTextPoints.requestFocus();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.mEditTextPoints, InputMethodManager.SHOW_IMPLICIT);

                holder.mEditTextPoints.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            // code to execute when EditText loses focus
                            updated_points =holder.mEditTextPoints.getText().toString();
                            holder.mEditTextPoints.clearFocus();
                            holder.mTextViewPoints.setText("Points: " + updated_points);
                            holder.mTextViewPoints.setVisibility(View.VISIBLE);
                            holder.mEditTextPoints.setVisibility(View.GONE);

                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(holder.mTextViewPoints.getWindowToken(), 0);

                            //also handle php script!!!
                            if(!init_points.equals(updated_points)) {
                                updatePoints(question,init_points,updated_points);
                            }

                            // IT SHOULD UPDATE ONLY WHEN IT ACUTALLY DOES IN THE DB
                        /*
                            if(updated)
                                holder.mTextViewPoints.setText("Points: " + updated_points);
                            else
                                holder.mTextViewPoints.setText("Points: " + init_points);
                        */
                        }
                    }
                });
            }
        });


        final String question = holder.mTextViewQuestion.getText().toString();

        for(int correctIdx=0; correctIdx < correctArr.length; correctIdx++) {

            //tv containing the answer
           final TextView tv = new TextView(mContext);
            tv.setText(correctArr[correctIdx]);
            tv.setTextSize(18);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                    (850,(int) ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            tv.setTextColor(correctColor);

            //layout containg tv and delete btn
            LinearLayout tv_ib_ll =  new LinearLayout(mContext);
            tv_ib_ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv_ib_ll.setOrientation(LinearLayout.HORIZONTAL);

            tv_ib_ll.addView(tv);

            final ImageButton ib = new ImageButton(mContext);
            ib.setBackgroundResource(R.drawable.ic_delete_primary_24dp);
            LinearLayout.LayoutParams paramsBtn=new LinearLayout.LayoutParams
                    (100,100);
            ib.setLayoutParams(paramsBtn);
            tv_ib_ll.addView(ib);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setMessage("Are you sure you want to delete this answer?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    request = new StringRequest(Request.Method.POST, URLdelete, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if(jsonObject.names().get(0).equals("success")){
                                                    Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                                    tv.setVisibility(View.GONE);
                                                    ib.setVisibility(View.GONE);
                                                } else {
                                                    Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                                            String answer = tv.getText().toString();
                                            HashMap<String,String> hashMap = new HashMap<>();
                                            hashMap.put("question",question);
                                            hashMap.put("answer",answer);
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
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String init_answer = tv.getText().toString();
                    tv.setVisibility(View.GONE);
                    ib.setVisibility(View.GONE);

                    //ll containg edittext and checkbox
                    LinearLayout et_cb_ll =  new LinearLayout(mContext);
                    et_cb_ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    et_cb_ll.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                            ( 850, ViewGroup.LayoutParams.WRAP_CONTENT);

                    final EditText et = new EditText(mContext);
                    et.setVisibility(View.VISIBLE);
                    et.setLayoutParams(params);
                    et_cb_ll.addView(et);
                    et.setText(init_answer);
                    et.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

                    final CheckBox cb = new CheckBox(mContext);
                    cb.setVisibility(View.VISIBLE);
                    cb.setChecked(true);
                    final boolean init_cb = true;
                    et_cb_ll.addView(cb);

                    holder.ll.addView(et_cb_ll);

                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                String updated_answer = et.getText().toString();
                                boolean updated_cb = cb.isChecked();
                                if(!updated_cb)
                                    tv.setTextColor(wrongColor);
                                et.setVisibility(View.GONE);
                                cb.setVisibility(View.GONE);
                                tv.setText(updated_answer);
                                tv.setVisibility(View.VISIBLE);
                                ib.setVisibility(View.VISIBLE);

                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(holder.mTextViewPoints.getWindowToken(), 0);

                                //also handle php script!!!
                                if((!init_answer.equals(updated_answer)) || (init_cb != updated_cb)) {
                                    updateAnswer(question, init_answer,updated_answer, init_cb, updated_cb);
                                }
                            }
                        }
                    });
                }
            });
            holder.ll.addView(tv_ib_ll);
        }

        ///WRONG
        //ANSWERS
        for(int wrongIndex=0; wrongIndex < incorrectArr.length; wrongIndex++) {

            final TextView tv = new TextView(mContext);
            tv.setText(incorrectArr[wrongIndex]);
            tv.setTextSize(18);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                    (850,(int) ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            tv.setTextColor(wrongColor);

            LinearLayout tv_ib_ll =  new LinearLayout(mContext);
            tv_ib_ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv_ib_ll.setOrientation(LinearLayout.HORIZONTAL);

            tv_ib_ll.addView(tv);

            final ImageButton ib = new ImageButton(mContext);
            ib.setBackgroundResource(R.drawable.ic_delete_primary_24dp);
            LinearLayout.LayoutParams paramsBtn=new LinearLayout.LayoutParams
                    (100,100);
            ib.setLayoutParams(paramsBtn);
            tv_ib_ll.addView(ib);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setMessage("Are you sure you want to delete this answer?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    request = new StringRequest(Request.Method.POST, URLdelete, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if(jsonObject.names().get(0).equals("success")){
                                                    Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                                    tv.setVisibility(View.GONE);
                                                    ib.setVisibility(View.GONE);
                                                } else {
                                                    Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                                            String answer = tv.getText().toString();
                                            HashMap<String,String> hashMap = new HashMap<>();
                                            hashMap.put("question",question);
                                            hashMap.put("answer",answer);
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
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String question = holder.mTextViewQuestion.getText().toString();
                    final String init_answer = tv.getText().toString();
                    tv.setVisibility(View.GONE);
                    ib.setVisibility(View.GONE);

                    LinearLayout et_cb_ll =  new LinearLayout(mContext);
                    et_cb_ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    et_cb_ll.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                            ( 850, ViewGroup.LayoutParams.WRAP_CONTENT);

                    final EditText et = new EditText(mContext);
                    et_cb_ll.addView(et);
                    et.setLayoutParams(params);
                    et.setVisibility(View.VISIBLE);
                    et.setText(init_answer);
                    et.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);


                    final CheckBox cb = new CheckBox(mContext);
                    cb.setVisibility(View.VISIBLE);
                    cb.setChecked(false);
                    final boolean init_cb = false;
                    et_cb_ll.addView(cb);

                    holder.ll.addView(et_cb_ll);

                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                String updated_answer = et.getText().toString();
                                boolean updated_cb = cb.isChecked();
                                if(updated_cb)
                                    tv.setTextColor(correctColor);

                                et.setVisibility(View.GONE);
                                cb.setVisibility(View.GONE);
                                tv.setText(updated_answer);
                                tv.setVisibility(View.VISIBLE);
                                ib.setVisibility(View.VISIBLE);

                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(holder.mTextViewPoints.getWindowToken(), 0);

                                //also handle php script!!!
                                if((!init_answer.equals(updated_answer)) || (init_cb != updated_cb)) {
                                  //  Toast.makeText(holder.mButtonEdit.getContext(),String.valueOf(init_cb),Toast.LENGTH_LONG).show();
                                    updateAnswer(question, init_answer,updated_answer, init_cb, updated_cb);
                                }
                            }
                        }
                    });


                }
            });
            holder.ll.addView(tv_ib_ll);
        }

        holder.mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(holder.mButtonEdit.getContext(),quizName + course,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(holder.mButtonEdit.getContext(), QuizCreatorActivity.class);
                intent.putExtra("question", questionName );
                intent.putExtra("points", points);
                intent.putExtra("correct", correctArr);
                intent.putExtra("wrong", incorrectArr);
                v.getContext().startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewQuestion;
        public EditText mEditTextQuestion;
        public TextView mTextViewPoints;
        public EditText mEditTextPoints;
        public ImageView mImageViewImage;

        public ImageButton mButtonEdit;

        List<ImageButton> allDBs = new ArrayList<>();
        LinearLayout ll;
        List<TextView> allTVAs = new ArrayList<>();
        List<TextView> allETAs = new ArrayList<>();

        public ExampleViewHolder(View itemView) {
            super(itemView);

            mTextViewQuestion = itemView.findViewById(R.id.questionItem_tv_question);
            mEditTextQuestion = itemView.findViewById(R.id.questionItem_et_question);
            mTextViewPoints = itemView.findViewById(R.id.questionItem_tv_points);
            mEditTextPoints = itemView.findViewById(R.id.questionItem_et_points);

            mImageViewImage = itemView.findViewById(R.id.questionItem_iv_image);

            mButtonEdit = itemView.findViewById(R.id.questionItem_btn_edit);

            ll = itemView.findViewById(R.id.ll_answers);
           // mButtonDelete = itemView.findViewById(R.id.questionItem_btn_delete);


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

    private void updateQuestion(final String init, final String quest){
        request = new StringRequest(Request.Method.POST, URLquest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                hashMap.put("init_question",init);
                hashMap.put("question",quest);
                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void updatePoints(final String question, final String init, final String points){
        request = new StringRequest(Request.Method.POST, URLpoints, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        updated = true;
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                hashMap.put("question",question);
                hashMap.put("init_points",init);
                hashMap.put("points",points);
                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void updateAnswer(final String question, final String init, final String answ, final boolean init_c, final boolean c){
        request = new StringRequest(Request.Method.POST, URLansw, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(mContext, jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        updated = true;
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
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
                hashMap.put("question",question);
                hashMap.put("init_answer",init);
                hashMap.put("answer",answ);
                hashMap.put("init_isCorrect",String.valueOf(init_c));
                hashMap.put("isCorrect",String.valueOf(c));
                return hashMap;
                //return super.getParams();
            }
        };
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void undoDelete( ArrayList<QuestionItem> mListItems, final int mRecentlyDeletedItemPosition, final QuestionItem mRecentlyDeletedItem) {

        // RecyclerView.getRecycledViewPool().clear();
        mListItems.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        //ArrayList<QuestionItem> x =  (ArrayList<QuestionItem>)mListItems.clone();
        //mListItems.clear();
     //   mListItems = (ArrayList<QuestionItem>)x.clone();
       // mListItems.addAll(mListItems);
     adapter.notifyItemInserted(mRecentlyDeletedItemPosition);

       // adapter.notifyItemRangeChanged(mRecentlyDeletedItemPosition,mListItems.size());
      //adapter.notifyItemChanged(mRecentlyDeletedItemPosition);
      //adapter.notifyDataSetChanged();
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
