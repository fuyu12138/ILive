package com.graduation.fuyu.ilive.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.pojo.ChatMessage;

import java.util.List;


public class ChatMsgRecyclerViewAdapter extends RecyclerView.Adapter<ChatMsgRecyclerViewAdapter.ViewHolder> {

    private List<ChatMessage> mChatMessages;

    public ChatMsgRecyclerViewAdapter(List<ChatMessage> chatMessages) {
        this.mChatMessages = chatMessages;
    }
    public void setData(List<ChatMessage> chatMessages){
        this.mChatMessages=chatMessages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatMessage msg=mChatMessages.get(position);
        holder.nickname.setText(msg.getNickname());
        holder.message.setText(msg.getMsg());
    }

    @Override
    public int getItemCount() {
        return mChatMessages==null?0:mChatMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;

        public ViewHolder(View view) {
            super(view);
            nickname =  view.findViewById(R.id.nickname);
            message = view.findViewById(R.id.message);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + message.getText() + "'";
        }
    }
}
