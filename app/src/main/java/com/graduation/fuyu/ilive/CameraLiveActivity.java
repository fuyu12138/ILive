package com.graduation.fuyu.ilive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.adapter.ChatMsgRecyclerViewAdapter;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.fragment.ChatMsgFragment;
import com.graduation.fuyu.ilive.pojo.ChatMessage;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.util.SetIconColor;
import com.graduation.fuyu.ilive.widget.DragFloatActionButton;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class CameraLiveActivity extends AppCompatActivity implements SrsEncodeHandler.SrsEncodeListener, RtmpHandler.RtmpListener, SrsRecordHandler.SrsRecordListener{

    private String rtmpUrl="rtmp://23.105.206.93/live/";

    private SrsCameraView srsCameraView;
    private SrsPublisher mPublisher;
    private DragFloatActionButton btn_float;

    private RecyclerView recyclerView;
    private List<ChatMessage> chatMessages;
    private ChatMsgRecyclerViewAdapter chatListAdapter;
    private String username;
    public static MultiUserChat muli;
    private String getMsg;
    private String getName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){getSupportActionBar().hide();}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        hideBottomUIMenu();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_my_live);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        srsCameraView=findViewById(R.id.activity_myLive_srsCamera);
//        btn_punish=findViewById(R.id.btn_punish);
        btn_float=findViewById(R.id.activity_myLive_btn_float);
        SharedPreferences sharedPreferences = getSharedPreferences("ilive", Context.MODE_PRIVATE);
        username= sharedPreferences.getString("username","");
        mPublisher=new SrsPublisher(srsCameraView);
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewResolution(1920, 1080);
        mPublisher.setOutputResolution(1080, 1920);
        mPublisher.setScreenOrientation(2);
        mPublisher.setVideoHDMode();
        mPublisher.startCamera();
        mPublisher.switchToSoftEncoder();
        final PopupMenu popupMenu=new PopupMenu(CameraLiveActivity.this,btn_float);
        setIconEnable(popupMenu);
        getMenuInflater().inflate(R.menu.activity_my_live_pop_item,popupMenu.getMenu());
        setEnable(popupMenu.getMenu().findItem(R.id.action_stop),false);
        btn_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_punish:
                                mPublisher.startPublish(rtmpUrl+username);
                                mPublisher.startCamera();
                                setEnable(menuItem,false);
                                setEnable(popupMenu.getMenu().findItem(R.id.action_stop),true);
                                setEnable(popupMenu.getMenu().findItem(R.id.action_switch),true);
                                break;
                            case R.id.action_switch:
                                mPublisher.switchCameraFace((mPublisher.getCamraId()+1)%
                                        android.hardware.Camera.getNumberOfCameras());
                                break;
                            case R.id.action_stop:
                                mPublisher.stopPublish();
                                mPublisher.startCamera();
                                setEnable(menuItem,false);
                                setEnable(popupMenu.getMenu().findItem(R.id.action_switch),true);
                                setEnable(popupMenu.getMenu().findItem(R.id.action_punish),true);
                                break;
                            case R.id.action_back:
                                onBackPressed();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        recyclerView=findViewById(R.id.activity_camera_live_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        chatMessages=new ArrayList<>();
//        setData(chatMessages);
        chatListAdapter=new ChatMsgRecyclerViewAdapter(chatMessages);
        recyclerView.setAdapter(chatListAdapter);
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMsg("加入聊天室成功");
        chatMessage.setNickname("系统消息");
        chatMessages.add(chatMessage);
        chatListAdapter.notifyDataSetChanged();
        muli.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                if(!TextUtils.isEmpty(message.getBody())){
                    try {
                        JSONObject obj=new JSONObject(message.getBody());
                        getMsg=obj.getString("msg");
                        getName=obj.getString("nickname");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.setMsg(getMsg);
                    chatMessage.setNickname(getName);
                    chatMessages.add(chatMessage);
                    chatListAdapter.setData(chatMessages);
                    chatListAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatListAdapter.getItemCount()-1);
                }
            }
        });
    }
//
//
//    private void requestPermissions() {
//        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
//            @Override
//            public void permissionGranted(@NonNull String[] permissions) {
//            }
//
//            @Override
//            public void permissionDenied(@NonNull String[] permissions) {
//                Toast.makeText(CameraLiveActivity.this, "请求权限失败", Toast.LENGTH_LONG).show();
//            }
//        }, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA}, false, null);
//    }

    private MultiUserChat multiUserChat;
//    @SuppressLint("StaticFieldLeak")
//    class JoinChatTask extends AsyncTask<Void,Void,Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            AbstractXMPPConnection connection = XMPPConnectionManager.getConnection();
//            String nickname = XmppStringUtils.parseBareJid(connection.getUser());
//            try {
//                multiUserChat= ChatRoomManager.joinChatRoom(connection,username+"@conference.izf813mgctz7h8z", nickname);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            if(!aBoolean){
//                ChatMessage chatMessage=new ChatMessage();
//                chatMessage.setMsg("加入聊天室失败");
//                chatMessage.setNickname("系统消息");
//                chatMessages.add(chatMessage);
//                chatListAdapter.notifyDataSetChanged();
//            }else {
//                ChatMessage chatMessage=new ChatMessage();
//                chatMessage.setMsg("加入聊天室成功");
//                chatMessage.setNickname("系统消息");
//                chatMessages.add(chatMessage);
//                chatListAdapter.notifyDataSetChanged();
//                multiUserChat.addMessageListener(new MessageListener() {
//                    @Override
//                    public void processMessage(Message message) {
//                        if(!TextUtils.isEmpty(message.getBody())){
//                            try {
//                                JSONObject obj=new JSONObject(message.getBody());
//                                getMsg=obj.getString("msg");
//                                getName=obj.getString("nickname");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            ChatMessage chatMessage=new ChatMessage();
//                            chatMessage.setMsg(getMsg);
//                            chatMessage.setNickname(getName);
//                            chatMessages.add(chatMessage);
//                            chatListAdapter.notifyDataSetChanged();
//                            recyclerView.scrollToPosition(chatListAdapter.getItemCount()-1);
//                        }
//                    }
//                });
//            }
//        }
//    }







    /**
     * 利用反射机制显示popupMenu图标
     */
    @SuppressLint("RestrictedApi")
    private void setIconEnable(PopupMenu popupMenu)
    {
        try
        {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 设置MenuItem选中状态
     * @param menuItem
     * @param b
     */
    private void setEnable(MenuItem menuItem,Boolean b){
        menuItem.setEnabled(b);
        menuItem.getIcon().setColorFilter(b? SetIconColor.setIconColor(0,0,0,255):SetIconColor.setIconColor(204,204,204,255));
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu(){
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        final Button btn=findViewById(R.id.btn_punish);
//        btn.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            muli.leave();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        finish();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
        } catch (Exception e1) {
            //
        }
    }
    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
//        Log.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
//        int rate = (int) bitrate;
//        if (rate / 1000 > 0) {
//            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
//        } else {
//            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
//        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String msg) {

    }

    @Override
    public void onRecordFinished(String msg) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordIOException(IOException e) {

    }
}
