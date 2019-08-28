package com.example.dcris.myapplication.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dcris.myapplication.adapters.TabAdapter;
import com.example.dcris.myapplication.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizzesFragment extends Fragment {


    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public QuizzesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_quizzes, container, false);


        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new CreateQuizzesFragment(), "Create");
        adapter.addFragment(new ViewQuizzesFragment(), "View");
        adapter.addFragment(new ScheduledFragment(), "Scheduled");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;

    }
}
