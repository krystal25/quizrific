package com.example.dcris.myapplication.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dcris.myapplication.ChallengeActivity;
import com.example.dcris.myapplication.ChatActivity;
import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.adapters.ChatBoxAdapter;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.model.Message;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    public String userType;
    Button startBtn;

    private FloatingActionButton broadcastFAB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        userType = KeyValueDB.getUsertype(getActivity());
        broadcastFAB = rootView.findViewById(R.id.home_broadcast_fab);
        TextView descrTV =rootView.findViewById(R.id.fraghome_tv_description);

        if(userType.equals("professor")){
            //broadcastFAB.show();
            descrTV.append(" Take care of your students by actively engaging in conversations.");

        }else{
            descrTV.append(" Share your ideas and thoughts with other students like you. Ask professors about the problems you encountered.");
        }

        broadcastFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                BroadcastFragment dialogFragment = new BroadcastFragment ();
                dialogFragment.show(fm, "Sample Fragment");
            }
        });


        startBtn = rootView.findViewById(R.id.fraghome_btn_start);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
