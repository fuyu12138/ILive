package com.graduation.fuyu.ilive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.graduation.fuyu.ilive.LiveRoomActivity;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.util.JidTransform;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 直播间列表 recyclerView
 * Created by root on 18-2-26.
 */

public class LiveRoomRecyclerAdapter extends RecyclerView.Adapter <LiveRoomRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<LiveRoom> roomList;
//    private String LiveJid;
    public LiveRoomRecyclerAdapter(Context context, List<LiveRoom> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    public void setData(List<LiveRoom> roomList){
        this.roomList=roomList;
    }

    /**
     * 刷新房间数据
     *
     */
    public void updateData(List<LiveRoom> roomList){
        this.roomList=roomList;
        notifyDataSetChanged();
    }

    /**
     * 增加房间数据
     * @param roomList
     */
    public void addData(List<LiveRoom> roomList){
        this.roomList.addAll(roomList);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LiveRoom info=roomList.get(position);
        holder.tv_anchor.setText(info.getName());
        holder.tv_name.setText(info.getRoomname());
        holder.tv_people_num.setText("0");
        holder.tv_description.setText(info.getLabel());
        Glide.with(context).load("http://139.196.124.195:8080/LoginServer/cover/"+info.getCover()).placeholder(R.mipmap.default1).error(R.mipmap.default1).into(holder.item_card_iv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLiveRoom=roomList.get(position);
                Jid=mLiveRoom.getName()+"@conference.izf813mgctz7h8z";
                new JoinChatTask().execute();
            }
        });
    }




    @Override
    public int getItemCount() {
        return roomList == null ? 0 : roomList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_anchor;
        TextView tv_name;
        TextView tv_people_num;
        TextView tv_description;
        ImageView item_card_iv;
        ViewHolder(View itemView) {
            super(itemView);
            tv_anchor=itemView.findViewById(R.id.item_card_tv_anchor);
            tv_name=itemView.findViewById(R.id.item_card_tv_room_name);
            tv_people_num=itemView.findViewById(R.id.item_card_tv_people_num);
            tv_description=itemView.findViewById(R.id.item_card_tv_field);
            item_card_iv= itemView.findViewById(R.id.item_card_iv);
//            itemView.setOnClickListener(this);
        }
//
//        @Override
//        public void onClick(View view) {
////            ChatRoom chatRoom=roomList.get(getAdapterPosition());
////            mChatRoom=chatRoom;
////            Jid=chatRoom.getJid();
////            new JoinChatTask().execute();
//        }
    }



    private String Jid;
    private MultiUserChat multiUserChat;
//    private ChatRoom mChatRoom;
    private LiveRoom mLiveRoom;
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
                LiveRoomActivity.mLiveRoom=mLiveRoom;
//                LiveRoomActivity.liveJid=JidTransform.parseBareJid(Jid);
                context.startActivity(intent);
            }else {
                Toast.makeText(context,"join fail",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
