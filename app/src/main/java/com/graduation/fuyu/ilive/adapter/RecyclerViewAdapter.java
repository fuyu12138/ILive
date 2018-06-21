package com.graduation.fuyu.ilive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.LiveRoomActivity;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.util.JidTransform;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 直播间列表 recyclerView
 * Created by root on 18-2-26.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.ViewHolder> {

    private Context context;
//    private LayoutInflater inflater;
    private List<ChatRoom> roomList;
//    private String LiveJid;
    public RecyclerViewAdapter(Context context, List<ChatRoom> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    public void setData(List<ChatRoom> roomList){
        this.roomList=roomList;
    }

    /**
     * 刷新房间数据
     *
     */
    public void updateData(ArrayList<ChatRoom> roomList){
        this.roomList=roomList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //实例化展示view;
        View view= LayoutInflater.from(context).inflate(R.layout.item_card,parent,false);
        //实例化viewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatRoom info=roomList.get(position);
        holder.tv_anchor.setText(JidTransform.parseBareJid(info.getJid()));
        holder.tv_name.setText(info.getName());
        holder.tv_people_num.setText(String.valueOf(info.getOccupantsCount()));
        holder.tv_description.setText(info.getDescription());
    }




    @Override
    public int getItemCount() {
        return roomList == null ? 0 : roomList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_anchor;
        TextView tv_name;
        TextView tv_people_num;
        TextView tv_description;
        ViewHolder(View itemView) {
            super(itemView);
            tv_anchor=itemView.findViewById(R.id.item_card_tv_anchor);
            tv_name=itemView.findViewById(R.id.item_card_tv_room_name);
            tv_people_num=itemView.findViewById(R.id.item_card_tv_people_num);
            tv_description=itemView.findViewById(R.id.item_card_tv_field);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ChatRoom chatRoom=roomList.get(getAdapterPosition());
            mChatRoom=chatRoom;
            Jid=chatRoom.getJid();
            new JoinChatTask().execute();
        }
    }



    private String Jid;
    private MultiUserChat multiUserChat;
    private ChatRoom mChatRoom;
    @SuppressLint("StaticFieldLeak")
    class JoinChatTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = XMPPConnectionManager.getConnection();
            String nickname = XmppStringUtils.parseBareJid(connection.getUser());
            try {
                multiUserChat= ChatRoomManager.joinChatRoom(connection,Jid, nickname);
            } catch (Exception e) {
                Toast.makeText(context,"connection fail",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Intent intent=new Intent(context,LiveRoomActivity.class);
//                LiveRoomActivity.mChatRoom=mChatRoom;
                LiveRoomActivity.from_RoomList=multiUserChat;
//                LiveRoomActivity.liveJid=JidTransform.parseBareJid(Jid);
                Log.e("Jid",Jid);
                Log.e("liveJid",JidTransform.parseBareJid(Jid));
                context.startActivity(intent);
            }else {
                Toast.makeText(context,"join fail",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
