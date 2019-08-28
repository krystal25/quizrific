package com.example.dcris.myapplication.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.Common;
import com.example.dcris.myapplication.QuestionsActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.adapters.QuizAdapter;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;
import com.example.dcris.myapplication.model.QuizItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewQuizzesFragment extends Fragment implements QuizAdapter.OnItemClickListener {

    public static final String EXTRA_QUIZ_NAME = "quizName";
    public static final String EXTRA_COURSE = "course";
    public static final String EXTRA_PROF = "professor";

    private String username;

    private RecyclerView mRecyclerView;
    private QuizAdapter mQuizAdapter;
    private ArrayList<QuizItem> mQuizList;

    private Button addQuestionBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recyclerview, container, false);

        // mRecyclerView.setAdapter(new QuizAdapter(getActivity(), mQuizList));
        //  mRecyclerView.invalidate();

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mQuizList = new ArrayList<>();

        username = KeyValueDB.getUsername(getActivity());

        parseJSON();
        return rootView;

    }

    private void parseJSON() {
        String url = "http://" + Common.serverIP + "/quizrific/get_quizzes.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);

                                String quizName = hit.getString("quizName");
                                int questionsCount = hit.getInt("questionsNo");
                                String course = hit.getString("course");

                                mQuizList.add(new QuizItem(quizName, course, questionsCount));
                            }

                            mQuizAdapter = new QuizAdapter(getActivity(), mQuizList);
                            mRecyclerView.getRecycledViewPool().clear();
                            mRecyclerView.setAdapter(mQuizAdapter);
                            //mRecyclerView.swapAdapter(mQuizAdapter, true);

                            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                                @Override
                                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                                    return false;
                                }

                                @Override
                                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                                            mQuizAdapter.deleteItem(viewHolder.getAdapterPosition());
                                       // mRecyclerView.getRecycledViewPool().clear();
                                    //mRecyclerView.setItemAnimator(null);
                                        //mQuizAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                    mQuizAdapter.notifyDataSetChanged();
                                }
                            }).attachToRecyclerView(mRecyclerView);

                            mQuizAdapter.setOnItemClickListener(new QuizAdapter.OnItemClickListener() {

                                @Override
                                public void onItemClick(int position) {
                                    Intent detailIntent = new Intent(getActivity(), QuestionsActivity.class);
                                    QuizItem clickedItem = mQuizList.get(position);
                                    detailIntent.putExtra(EXTRA_QUIZ_NAME, clickedItem.getQuiz());
                                    detailIntent.putExtra(EXTRA_COURSE, clickedItem.getCourse());
                                    detailIntent.putExtra(EXTRA_PROF, username);

                                    startActivity(detailIntent);
                                }


                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("professor", username);
                return hashMap;
                //return super.getParams();
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }


    //idk if this one is necessary to have the same things as above

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }

    }
}