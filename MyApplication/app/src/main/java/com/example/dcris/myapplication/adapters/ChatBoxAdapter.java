package com.example.dcris.myapplication.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dcris.myapplication.R;
import com.example.dcris.myapplication.model.Message;

import java.util.List;

public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {
    private List<Message> MessageList;

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;
        public String usertype = (String) Message.getUsertype();
        public int student;
        public int prof;

        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            message = (TextView) view.findViewById(R.id.message);
        }
    }
// in this adaper constructor we add the list of messages as a parameter so that
// we will passe  it when making an instance of the adapter object in our activity
    public ChatBoxAdapter(List<Message>MessagesList) {

       // setHasStableIds(true);
        this.MessageList = MessagesList;
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }
    @Override
    public ChatBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new ChatBoxAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatBoxAdapter.MyViewHolder holder, final int position) {
        //binding the data from our ArrayList of object to the message_item.xml using the viewholder

        Message m = MessageList.get(position);
        holder.nickname.setText(m.getNickname());
        holder.message.setText(m.getMessage());

        holder.prof= ContextCompat.getColor(holder.nickname.getContext(), R.color.colorPlayer1);
        holder.student=ContextCompat.getColor(holder.nickname.getContext(), R.color.wrongAnswer);
/*
        if(holder.usertype.equals("professor")) {
            holder.message.setTextColor(holder.prof);
            holder.nickname.setTextColor(holder.prof);
        }
        else {
            holder.message.setTextColor(holder.student);
            holder.nickname.setTextColor(holder.student);
        }
        */
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