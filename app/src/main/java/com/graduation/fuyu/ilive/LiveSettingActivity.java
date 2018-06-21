package com.graduation.fuyu.ilive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.service.FocusPostService;
import com.graduation.fuyu.ilive.service.LiveRoomPostService;
import com.graduation.fuyu.ilive.service.RoomPostService;
import com.graduation.fuyu.ilive.widget.BarArrow;
import com.graduation.fuyu.ilive.widget.CircleImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LiveSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private BarArrow my_picture;
    private BarArrow room_name;
    private BarArrow room_describe;
    private BarArrow room_label;
    private String username;
    private String label;
    private String roomName;

    private CircleImageView head;
    private TextView RoomOwner;
    private TextView RoomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_live_setting);
        toolbar=findViewById(R.id.activity_live_setting_toolbar);
        my_picture=findViewById(R.id.activity_live_setting_my_picture);
        room_name=findViewById(R.id.activity_live_setting_room_name);
        room_describe=findViewById(R.id.activity_live_setting_room_describe);
        room_label=findViewById(R.id.activity_live_setting_room_label);
        head=findViewById(R.id.activity_live_setting_iv_head);
        RoomOwner=findViewById(R.id.activity_live_setting_tv_room_owner);
        RoomName=findViewById(R.id.activity_live_setting_tv_room_name);
        my_picture.getImageView().setImageResource(R.mipmap.ic_picture);
        my_picture.getTextView().setText("我的封面");
        room_name.getImageView().setImageResource(R.mipmap.ic_rename);
        room_name.getTextView().setText("直播间名称");
        room_describe.getImageView().setImageResource(R.mipmap.ic_describe);
        room_describe.getTextView().setText("房间描述");
        room_label.getImageView().setImageResource(R.mipmap.ic_label);
        room_label.getTextView().setText("房间标签");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        my_picture.setOnClickListener(this);
        room_name.setOnClickListener(this);
        room_describe.setOnClickListener(this);
        room_label.setOnClickListener(this);

        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences=getSharedPreferences("ilive", Context.MODE_APPEND);
        username=sharedPreferences.getString("username","error");

        new getLiveRoomByName().execute();
    }

    private void requestCamera() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(LiveSettingActivity.this, "用户拒绝了访问摄像头", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
    }

    private void initWindow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_live_setting_my_picture:
                requestPermissions();
                break;
            case R.id.activity_live_setting_room_name:
                new MaterialDialog.Builder(this)
                        .title("请输入直播间名称")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .inputRange(2,16)
                        .positiveText("保存")
                        .input(mLiveRoom.getRoomname(), mLiveRoom.getRoomname(), false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                roomName=input.toString();
                                new RoomNameTask().execute();
                            }
                        }).show();
                break;
            case R.id.activity_live_setting_room_describe:
                SetRoomDescribeActivity.RoomDescribe=mLiveRoom.getIntroduce();
                startActivity(new Intent(getApplicationContext(), SetRoomDescribeActivity.class));
                break;
            case R.id.activity_live_setting_room_label:
                new MaterialDialog.Builder(this)
                        .title("请输入直播间标签")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .inputRange(2,16)
                        .positiveText("保存")
                        .input(mLiveRoom.getLabel(), mLiveRoom.getLabel(), false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                label= input.toString();
                                new LabelTask().execute();

                            }
                        }).show();

                break;
        }
    }

    String[] perms={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private void requestPermissions() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                SetFrontCoverActivity.FileName=mLiveRoom.getCover();
                Intent intent=new Intent(LiveSettingActivity.this, SetFrontCoverActivity.class);
                startActivity(intent);
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(LiveSettingActivity.this, "请求权限失败", Toast.LENGTH_LONG).show();
            }
        }, perms);
    }


    /**
     * 标签
     */
    @SuppressLint("StaticFieldLeak")
    class LabelTask extends AsyncTask<Void,Void,Boolean> {
        String responseMsg;
        @Override
        protected Boolean doInBackground(Void... voids) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", username));
            params.add(new BasicNameValuePair("label", label));
            responseMsg = RoomPostService.setLabel(params);
            return Objects.equals(responseMsg, "SUCCEEDED");

        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                new getLiveRoomByName().execute();
                Toast.makeText(LiveSettingActivity.this,"标签设置成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LiveSettingActivity.this,"标签设置失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 房间名
     */
    @SuppressLint("StaticFieldLeak")
    class RoomNameTask extends AsyncTask<Void,Void,Boolean> {
        String responseMsg;
        @Override
        protected Boolean doInBackground(Void... voids) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", username));
            params.add(new BasicNameValuePair("roomname", roomName));
            responseMsg = RoomPostService.setRoomName(params);
            return Objects.equals(responseMsg, "SUCCEEDED");

        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                new getLiveRoomByName().execute();
                Toast.makeText(LiveSettingActivity.this,"房间名设置成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LiveSettingActivity.this,"房间名设置失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private LiveRoom mLiveRoom=new LiveRoom();
    @SuppressLint("StaticFieldLeak")
    class getLiveRoomByName extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("liveName", username));
            return LiveRoomPostService.getLiveRoomByName(params);
        }

        @Override
        protected void onPostExecute(String s) {
//            Toast.makeText(LiveSettingActivity.this,s,Toast.LENGTH_SHORT).show();
            // TODO: 18-5-22 判断
            JSONObject mJSONObject;
            try {
                mJSONObject = new JSONObject(s);
                JSONArray name=mJSONObject.getJSONArray("name");
                JSONArray roomname=mJSONObject.getJSONArray("roomname");
                JSONArray label=mJSONObject.getJSONArray("label");
                JSONArray introduce=mJSONObject.getJSONArray("introduce");
                JSONArray cover=mJSONObject.getJSONArray("cover");
                JSONArray head=mJSONObject.getJSONArray("head");

                if (name.length()!=0){
                    mLiveRoom.setName(name.getString(0));
                    mLiveRoom.setRoomname(roomname.getString(0));
                    mLiveRoom.setLabel(label.getString(0));
                    mLiveRoom.setIntroduce(introduce.getString(0));
                    mLiveRoom.setCover(cover.getString(0));
                    mLiveRoom.setHead(head.getString(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Glide.with(LiveSettingActivity.this).load("http://139.196.124.195:8080/LoginServer/head/"+ mLiveRoom.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(head);
            RoomName.setText(mLiveRoom.getRoomname());
            RoomOwner.setText(mLiveRoom.getName());

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getLiveRoomByName().execute();
    }
}
