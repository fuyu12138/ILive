
package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.graduation.fuyu.ilive.connection.HClient;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.pojo.User;
import com.graduation.fuyu.ilive.service.LiveRoomPostService;
import com.graduation.fuyu.ilive.service.UserPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎界面，自动登录
 */
public class WelcomeActivity extends AppCompatActivity {
    private String last_username;
    private String last_password;


    /**
     * 0:本地存在登录信息，登录
     * 1：本地不存在登录信息，跳转登录界面
     */
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    new AutoLoginTask().execute();
                    break;
                case 1:
                    Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏ActionBar
        if(getSupportActionBar()!=null){getSupportActionBar().hide();}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        SharedPreferences sharedPreferences = getSharedPreferences("ilive", Context.MODE_PRIVATE);
        last_username= sharedPreferences.getString("username","");
        last_password= sharedPreferences.getString("password","");
        if (!last_username.equals("")&&!last_password.equals("")){
            // TODO: 18-3-15 异步登录
            handler.sendEmptyMessageDelayed(0,2000);
        }else {
            handler.sendEmptyMessageDelayed(1,2000);
        }
    }

    /**
     * 自动登录的异步任务
     * 登录成功，跳转MainActivity
     * 登录失败,跳转LoginActivity
     */
    @SuppressLint("StaticFieldLeak")
    class AutoLoginTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            if (XMPPConnectionManager.abstractXMPPConnection!=null){
                XMPPConnectionManager.abstractXMPPConnection=null;
            }
            AbstractXMPPConnection connection= XMPPConnectionManager.getConnection();
            try {
                connection.login(last_username,last_password);
                Presence presence=new Presence(Presence.Type.available);
                connection.sendStanza(presence);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                new getHeadByName().execute();
            }else {
                Toast.makeText(WelcomeActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    class getHeadByName extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("username", last_username));
            return UserPostService.getHeadByName(params);
        }

        @Override
        protected void onPostExecute(String s) {
            User.head=s;
            Intent intent;
            Toast.makeText(WelcomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
