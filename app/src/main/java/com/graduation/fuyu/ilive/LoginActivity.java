package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.User;
import com.graduation.fuyu.ilive.service.UserPostService;
import com.graduation.fuyu.ilive.widget.CheckBlankButton;
import com.graduation.fuyu.ilive.widget.PasswordEditText;
import com.graduation.fuyu.ilive.widget.TouchableButton;
import com.graduation.fuyu.ilive.widget.UsernameEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private UsernameEditText et_username;
    private PasswordEditText et_password;
    private ImageView image_eye;
    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private TouchableButton btn_register;
    private CheckBlankButton btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //禁用截图
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){getSupportActionBar().hide();}
        setContentView(R.layout.activity_login);
        sharedPreferences=getSharedPreferences("ilive", Context.MODE_PRIVATE);
        image_eye=findViewById(R.id.login_iv);
        et_username = findViewById(R.id.login_et_username);
        et_password=findViewById(R.id.login_et_password);
        btn_register = findViewById(R.id.login_btn_register);
        btn_login = findViewById(R.id.login_btn_login);
        image_eye.setSelected(true);
        btn_register.setSelected(false);

        ArrayList<EditText> editTexts=new ArrayList<>();
        editTexts.add(et_username);
        editTexts.add(et_password);
        btn_login.AddListeningEditTexts(editTexts);

        //=======================PasswordEditText初始化并绑定图片，并重写相应方法================
        mRightDrawable=ContextCompat.getDrawable(this,R.drawable.ic_clear_pink_24dp);
        mRightDrawable.setBounds(0,0,50,50);
        mLeftDrawable= ContextCompat.getDrawable(LoginActivity.this,R.drawable.ic_lock_outline_black_24dp);
        mLeftDrawable.setBounds(0,0,50,50);
        et_password.setCompoundDrawables(mLeftDrawable,et_password.getCompoundDrawables()[1],null,et_password.getCompoundDrawables()[3]);
        et_password.setOnFocusChangeListener(new ImageChange());


        btn_register.setOnTouchListener(new RegisterTouch());
        btn_login.setOnTouchListener(new LoginTouch());
    }

    private class RegisterTouch implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                btn_register.setSelected(true);
                btn_register.setTextColor(getResources().getColor(R.color.colorAccentPressed));
            }else if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                btn_register.setSelected(false);
                btn_register.setTextColor(getResources().getColor(R.color.colorAccent));
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            return false;
        }
    }

    private class LoginTouch implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                btn_login.setSelected(true);
            }else if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                btn_login.setSelected(false);
                new LoginTask().execute();
            }
            return false;
        }
    }

    /**
     * 登录XMPP服务器
     */
    @SuppressLint("StaticFieldLeak")
    class LoginTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            String username=et_username.getText().toString();
            String password=et_password.getText().toString();
            //登录时要用新的连接，否则会报错
            if (XMPPConnectionManager.abstractXMPPConnection!=null){
                XMPPConnectionManager.abstractXMPPConnection=null;
            }
            AbstractXMPPConnection connection= XMPPConnectionManager.getConnection();
            try {
                connection.login(username,password);
                Presence presence=new Presence(Presence.Type.available);
                connection.sendStanza(presence);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        //这里用commit()立即写入sharePreferences
        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("username",et_username.getText().toString());
                editor.putString("password",et_password.getText().toString());
                editor.commit();
                new getHeadByName().execute();
            }
            else{
                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class getHeadByName extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("username", et_username.getText().toString()));
            return UserPostService.getHeadByName(params);
        }

        @Override
        protected void onPostExecute(String s) {
            User.head=s;
            Intent intent;
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }



    private class ImageChange implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                mLeftDrawable= ContextCompat.getDrawable(LoginActivity.this,R.drawable.ic_lock_outline_pink_24dp);
                mLeftDrawable.setBounds(0,0,50,50);
                setClearVisible(!et_password.getText().toString().isEmpty());
                image_eye.setSelected(false);
            }else {
                mLeftDrawable= ContextCompat.getDrawable(LoginActivity.this,R.drawable.ic_lock_outline_black_24dp);
                mLeftDrawable.setBounds(0,0,50,50);
                setClearVisible(false);
                image_eye.setSelected(true);
            }
        }
    }
    private void setClearVisible(Boolean isVisible){
        Drawable right= isVisible? mRightDrawable:null;
        et_password.setCompoundDrawables(mLeftDrawable,et_password.getCompoundDrawables()[1],right,et_password.getCompoundDrawables()[3]);
    }
}
