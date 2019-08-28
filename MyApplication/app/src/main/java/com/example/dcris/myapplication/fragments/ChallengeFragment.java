package com.example.dcris.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dcris.myapplication.ChallengeActivity;
import com.example.dcris.myapplication.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChallengeFragment extends Fragment {


    public ChallengeFragment() {
        // Required empty public constructor
    }

    Button startBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_challenge, container, false);
        startBtn = rootView.findViewById(R.id.fragchallenge_btn_start);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChallengeActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

}
