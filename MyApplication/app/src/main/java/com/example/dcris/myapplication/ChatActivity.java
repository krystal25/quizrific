package com.example.dcris.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dcris.myapplication.adapters.ChatBoxAdapter;
import com.example.dcris.myapplication.fragments.BroadcastFragment;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.model.Message;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private Button btn;

    public RecyclerView myRecylerView ;
    public List<Message> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messageET ;
    public  Button send ;
    //declare socket object
    private Socket socket;
    public String Nickname ;
    public String userType;
    public TextView nicknameTV;
    public TextView messageTV, typingTV;
    public int student;
    public int prof;
    private TextView connectTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageET =  findViewById(R.id.message_et) ;
        send = findViewById(R.id.send);
        prof= ContextCompat.getColor(getApplicationContext(), R.color.colorPlayer1);
        student=ContextCompat.getColor(getApplicationContext(), R.color.wrongAnswer);
        Nickname= KeyValueDB.getUsername(getApplicationContext());
        userType = KeyValueDB.getUsertype(getApplicationContext());
        connectTV = findViewById(R.id.chat_tv_connect);
        typingTV = findViewById(R.id.chat_tv_typing);

       // messageET.requestFocus();
        messageET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    socket.emit("usertyping",Nickname);

                   // Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("gello","hasFocus");
                    socket.emit("usertyping","0");
                    //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
        nicknameTV = (TextView) findViewById(R.id.nickname);
        messageTV = (TextView) findViewById(R.id.message);
        // Toast.makeText(getActivity(),userType,Toast.LENGTH_SHORT).show();
        //connect you socket client to the server
        try {
            socket = IO.socket("http://35.204.4.163:3001");
            socket.connect();
            socket.emit("join", Nickname);
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }
        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());

        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //retrieve the nickname and the message content and fire the event   //messagedetection
                if(!messageET.getText().toString().trim().equals("")){
                    socket.emit("messagedetection",Nickname,messageET.getText().toString(),userType);
                    connectTV.setVisibility(View.GONE);
                    messageET.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(messageET.getWindowToken(), 0);
                    messageET.setText("");
                }
            }
        });
        //implementing socket listeners
        socket.on("typing", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                      if(data.equals("0")){
                          typingTV.setText("");
                      }else{
                          typingTV.setText(data + " is typing...");
                      }
                    }
                });
            }
        });
        socket.on("userjoinedthechat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        socket.on("userdisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                         Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectTV.setVisibility(View.GONE);
                        JSONObject data = (JSONObject) args[0];
                        try {
                            //extract data from fired event
                            String message = data.getString("message");
                            String nickname = data.getString("senderNickname");
                            String usertype = data.getString("usertype");
                            // make instance of message
                            Message m = new Message(usertype,nickname,message);
                            //add the message to the messageList
                            MessageList.add(m);
                            // add the new updated list to the adapter
                            chatBoxAdapter = new ChatBoxAdapter(MessageList);
                            // notify the adapter to update the recycler view
                            chatBoxAdapter.notifyDataSetChanged();
                            //set the adapter for the recycler view
                            myRecylerView.setAdapter(chatBoxAdapter);
                        /*
                            if(usertype.equals("professor")) {
                                nicknameTV.setTextColor(prof);
                                nicknameTV.setTextColor(prof);
                            }
                            else {
                                nicknameTV.setTextColor(student);
                                nicknameTV.setTextColor(student);
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
