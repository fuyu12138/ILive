package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.graduation.fuyu.ilive.adapter.FocusRecyclerAdapter;
import com.graduation.fuyu.ilive.adapter.FragmentChatRoomAdapter;
import com.graduation.fuyu.ilive.fragment.ChatMsgFragment;
import com.graduation.fuyu.ilive.fragment.RoomInfoFragment;
import com.graduation.fuyu.ilive.media.IjkPlayerView;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.pojo.User;
import com.graduation.fuyu.ilive.service.FocusPostService;
import com.graduation.fuyu.ilive.service.RegisterPostService;
import com.graduation.fuyu.ilive.service.UserPostService;
import com.graduation.fuyu.ilive.widget.CircleImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LiveRoomActivity extends AppCompatActivity implements RoomInfoFragment.OnFragmentInteractionListener{

    private static final String VIDEO_URL = "rtmp://23.105.206.93/live/";

    private ViewPager viewPager;
    private FragmentChatRoomAdapter mFragmentAdapter;
    private ChatMsgFragment chatMsgFragment;
    private RoomInfoFragment roomInfoFragment;
    private List<Fragment> mListFragment=new ArrayList<>();
    private TabLayout mTabLayout;

    private RelativeLayout smallScreen;
    private RelativeLayout fullScreen;
    private ImageView mIvFullscreen;
    private IjkPlayerView mPlayerView;
    public static MultiUserChat from_RoomList;
//    public static String liveJid;
//    public static ChatRoom mChatRoom;
    public static LiveRoom mLiveRoom;

    public Boolean isFocus;

    private CircleImageView head;
    private TextView RoomDescription;
    private TextView RoomOwner;
    private TextView PeopleNum;
    private Button focus;


    private String username;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){getSupportActionBar().hide();}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_room);
        viewPager=findViewById(R.id.activity_liveRoom_viewPager);
        mTabLayout=findViewById(R.id.activity_liveRoom_tabLayout);
        smallScreen=findViewById(R.id.activity_liveRoom_small_screen);
        fullScreen=findViewById(R.id.activity_liveRoom_full_screen);
        head=findViewById(R.id.activity_liveRoom_iv_head);
        RoomDescription=findViewById(R.id.activity_liveRoom_tv_room_name);
        RoomOwner=findViewById(R.id.activity_liveRoom_Room_owner);
        PeopleNum=findViewById(R.id.activity_liveROom_tv_room_people_num);
        focus=findViewById(R.id.activity_liveRoom_btn_focus);
        Glide.with(this).load("http://139.196.124.195:8080/LoginServer/head/"+ mLiveRoom.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(head);
        new getHeadByName().execute();
        /*
        ===========================================viewPager绑定Fragment=================================================
         */
        chatMsgFragment=new ChatMsgFragment();
        roomInfoFragment=new RoomInfoFragment();
        mListFragment.add(chatMsgFragment);
        mListFragment.add(roomInfoFragment);
        mFragmentAdapter=new FragmentChatRoomAdapter(this.getSupportFragmentManager(),mListFragment);
        viewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(viewPager);

        /*
        ============================================ijkplayer播放器=======================================================
         */
        mPlayerView=new IjkPlayerView(this);
        mIvFullscreen=mPlayerView.getFullScreenView();
        smallScreen.addView(mPlayerView);
        mPlayerView.init()
                .setVideoPath(Uri.parse(VIDEO_URL+mLiveRoom.getName()))
                .initDanmaku()
                .start();
        //监听小屏与全屏切换
        mIvFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIvFullscreen.isSelected()){
                    fullScreen.removeAllViews();
                    fullScreen.setVisibility(View.GONE);
                    mPlayerView.setSmallScreen();
                    smallScreen.addView(mPlayerView);
                    smallScreen.setVisibility(View.VISIBLE);
                    mIvFullscreen.setSelected(false);
                }else {//full screen
                    smallScreen.removeAllViews();
                    smallScreen.setVisibility(View.GONE);
                    mPlayerView.setFullScreen();
                    fullScreen.addView(mPlayerView);
                    fullScreen.setVisibility(View.VISIBLE);
                    mIvFullscreen.setSelected(true);
                }
            }
        });

        /*
        ==========================================MultiUserChat聊天==========================================================
         */
        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences=getSharedPreferences("ilive", Context.MODE_APPEND);
        username=sharedPreferences.getString("username","匿名");

        /*
        ==========================================fragment通信===============================================================
         */
        RoomDescription.setText(String.valueOf(mLiveRoom.getRoomname()));
        RoomOwner.setText(mLiveRoom.getName());
        PeopleNum.setText(String.valueOf(0));
        /*
        ============================================关注处理==================================================================
         */
        focus.setSelected(false);
        focus.setText("+关注");
        focus.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
        focus.setClickable(false);
        new InquireFocusTask().execute();
        focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (focus.isSelected()){//取消关注
                    String param="roomname="+mLiveRoom.getName()+"&username="+username;
                    new cancleFocusTask().execute(param);
                }else {//关注
                    new FocusTask().execute();
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    class getHeadByName extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("username", mLiveRoom.getName()));
            return UserPostService.getHeadByName(params);
        }

        @Override
        protected void onPostExecute(String s) {
            Glide.with(LiveRoomActivity.this).load("http://139.196.124.195:8080/LoginServer/head/"+ mLiveRoom.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(head);
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
            params.add(new BasicNameValuePair("roomname", mLiveRoom.getName()));
            responseMsg = FocusPostService.send(params);
            return Objects.equals(responseMsg, "SUCCEEDED");

        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                Toast.makeText(LiveRoomActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                focus.setSelected(true);
                focus.setTextColor(ContextCompat.getColor(LiveRoomActivity.this,R.color.colorGrey666666));
                focus.setText("取消关注");
            }else {
                Toast.makeText(LiveRoomActivity.this,"关注失败",Toast.LENGTH_SHORT).show();
            }
        }
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
                Toast.makeText(LiveRoomActivity.this,"连接服务器出错",Toast.LENGTH_SHORT).show();
            }else if (Objects.equals(s, "0")){
                Toast.makeText(LiveRoomActivity.this,"取消关注失败",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LiveRoomActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
                focus.setSelected(false);
                focus.setText("+关注");
                focus.setTextColor(ContextCompat.getColor(LiveRoomActivity.this,R.color.colorAccent));
            }
        }
    }

    /**
     * 关注
     */
    @SuppressLint("StaticFieldLeak")
    class InquireFocusTask extends AsyncTask<Void,Void,String>{
        String responseMsg;
        @Override
        protected String doInBackground(Void... voids) {
            String param="roomname="+mLiveRoom.getName()+"&username="+username;
            responseMsg = FocusPostService.inquire(param);
            return responseMsg;
        }
        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "true")){
                focus.setSelected(true);
                focus.setTextColor(ContextCompat.getColor(LiveRoomActivity.this,R.color.colorGrey666666));
                focus.setText("取消关注");
                focus.setClickable(true);
            }else if (Objects.equals(s,"false")){
                focus.setSelected(false);
                focus.setText("+关注");
                focus.setTextColor(ContextCompat.getColor(LiveRoomActivity.this,R.color.colorAccent));
                focus.setClickable(true);
            }else {
                Toast.makeText(LiveRoomActivity.this,"查询关注失败",Toast.LENGTH_SHORT).show();
                focus.setClickable(false);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public MultiUserChat getMultiUserChat(){
        return from_RoomList;
    }

    public String getUsername(){
        return username;
    }

    public String getIntroduce(){
        return mLiveRoom.getIntroduce();
    }
    public IjkPlayerView getmPlayerView(){
        return mPlayerView;
    }
    /**
     * 可见到部分可见
     * resume到pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
    }

    /**
     * 部分可见到可见
     * resume到pause
     * start到resume
     */
    @Override
    protected void onResume() {
        if (!(mFragmentAdapter == null)) {
            mFragmentAdapter.notifyDataSetChanged();
        }
        super.onResume();
        mPlayerView.onResume();
    }

    /**
     * onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();
    }

    /**
     * 返回键
     */
    @Override
    public void onBackPressed() {
        try {
            from_RoomList.leave();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        mPlayerView.onBackPressed();
        finish();
    }
}
