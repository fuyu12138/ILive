package com.graduation.fuyu.ilive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.graduation.fuyu.ilive.LiveRoomActivity;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.service.FocusPostService;
import com.graduation.fuyu.ilive.util.JidTransform;
import com.graduation.fuyu.ilive.widget.CircleImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 关注列表
 * Created by root on 18-3-25.
 */

public class FocusRecyclerAdapter extends RecyclerView.Adapter<FocusRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<LiveRoom> roomList;
    private String username;

    public FocusRecyclerAdapter(Context context,List<LiveRoom> roomList,String username) {
        this.context=context;
        this.roomList=roomList;
        this.username=username;
    }

    public void setData(List<LiveRoom> roomList){
        this.roomList=roomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //实例化展示view;
        View view= LayoutInflater.from(context).inflate(R.layout.fragment_focus_item,parent,false);
        //实例化viewHolder
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView head;
        TextView anchor;
        TextView roomName;
        Button focus;
        String roomJid;

        ViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.fragment_focus_item_iv_head);
            anchor = itemView.findViewById(R.id.fragment_focus_item_tv_room_owner);
            roomName = itemView.findViewById(R.id.fragment_focus_item_tv_room_name);
            focus = itemView.findViewById(R.id.fragment_focus_item_btn_focus);
            focus.setSelected(true);
            focus.setTextColor(ContextCompat.getColor(context, R.color.colorGrey666666));
            focus.setText("取消关注");
            focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (focus.isSelected()) {//取消关注
                        String param = "roomname=" + roomJid + "&username=" + username;
                        new cancleFocusTask().execute(param);
                    } else {//关注
                        new FocusTask().execute();
                    }
                }
            });
        }
        /**
         * 取消关注
         */
        @SuppressLint("StaticFieldLeak")
        class cancleFocusTask extends AsyncTask<String,Void,String>{
            String result;
            @Override
            protected String doInBackground(String... strings) {
                result=FocusPostService.cancleFocus(strings[0]);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                if(Objects.equals(s, "error")){
                    Toast.makeText(context,"连接服务器出错",Toast.LENGTH_SHORT).show();
                }else if (Objects.equals(s, "0")){
                    Toast.makeText(context,"取消关注失败",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"取消关注成功",Toast.LENGTH_SHORT).show();
                    focus.setSelected(false);
                    focus.setText("+关注");
                    focus.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
                }
            }
        }

        /**
         * 关注
         */
        @SuppressLint("StaticFieldLeak")
        class FocusTask extends AsyncTask<Void,Void,Boolean>{
            String responseMsg;
            @Override
            protected Boolean doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("roomname", roomJid));
                responseMsg = FocusPostService.send(params);
                return Objects.equals(responseMsg, "SUCCEEDED");
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean){
                    Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                    focus.setSelected(true);
                    focus.setTextColor(ContextCompat.getColor(context,R.color.colorGrey666666));
                    focus.setText("取消关注");
                }else {
                    Toast.makeText(context,"关注失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 进入直播间
     */
    private String Jid;
    private MultiUserChat multiUserChat;
    private LiveRoom mLiveRoom;
    @SuppressLint("StaticFieldLeak")
    class JoinChatTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = XMPPConnectionManager.getConnection();
            String nickname = XmppStringUtils.parseBareJid(connection.getUser());
            try {
                multiUserChat= ChatRoomManager.joinChatRoom(connection,Jid+"@conference.izf813mgctz7h8z", nickname);
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
                LiveRoomActivity.from_RoomList=multiUserChat;
                LiveRoomActivity.mLiveRoom=mLiveRoom;
                context.startActivity(intent);
            }else {
                Toast.makeText(context,"join fail",Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    public void onBindViewHolder(FocusRecyclerAdapter.ViewHolder holder, final int position) {
        LiveRoom info=roomList.get(position);
        Glide.with(context).load("http://139.196.124.195:8080/LoginServer/head/"+ info.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(holder.head);
        Log.e("FocusRada", "http://139.196.124.195:8080/LoginServer/head/"+ info.getCover());
        holder.anchor.setText(info.getName());
        holder.roomName.setText(info.getRoomname());
        holder.roomJid=info.getName();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLiveRoom=roomList.get(position);
                Jid=mLiveRoom.getName();
                new JoinChatTask().execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList == null ? 0 : roomList.size();
    }


}
