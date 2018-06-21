package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.graduation.fuyu.ilive.service.RegisterPostService;
import com.graduation.fuyu.ilive.widget.CheckBlankButton;
import com.graduation.fuyu.ilive.widget.EmailEditText;
import com.graduation.fuyu.ilive.widget.PasswordEditText;
import com.graduation.fuyu.ilive.widget.UsernameEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private UsernameEditText et_username;
    private PasswordEditText et_password;
    private EmailEditText et_email;
    private CheckBlankButton btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){getSupportActionBar().hide();}
        setContentView(R.layout.activity_register);
        et_username=findViewById(R.id.register_et_username);
        et_password=findViewById(R.id.register_et_password);
        et_email=findViewById(R.id.register_et_email);
        btn_register=findViewById(R.id.register_btn_register);
        Drawable mLeftDrawable = ContextCompat.getDrawable(RegisterActivity.this, R.drawable.ic_lock_outline_black_24dp);
        mLeftDrawable.setBounds(0,0,50,50);
        et_password.setCompoundDrawables(mLeftDrawable,et_password.getCompoundDrawables()[1],null,et_password.getCompoundDrawables()[3]);

        ArrayList<EditText> editTexts=new ArrayList<>();
        editTexts.add(et_username);
        editTexts.add(et_password);
        editTexts.add(et_email);
        btn_register.AddListeningEditTexts(editTexts);
        btn_register.setOnTouchListener(new RegisterTouch());
    }

    private class RegisterTouch implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            btn_register.setSelected(false);
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                btn_register.setSelected(true);
            }else if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                if(isEmail(et_email.getText().toString())){
                    new RegisterTask().execute();
                }else {
                    Toast.makeText(RegisterActivity.this,"邮箱格式错误",Toast.LENGTH_SHORT).show();
                }

            }
            return false;
        }
    }

    /**
     * 警告：
     *  如果不把用作内部类的AsyncTask声明为static会导致
     *  RegisterTask 比 top class:Activity的生存周期还要长
     *  造成内存泄露
     * 解决办法：
     *  注释掉警告，哈哈哈哈O(∩_∩)O
     *  repose： 0:失败
     *          1：成功
     */
    @SuppressLint("StaticFieldLeak")
    class RegisterTask extends AsyncTask<Void,Void,Boolean>{
        String responseMsg;
        @Override
        protected Boolean doInBackground(Void... voids) {
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();
            String email = et_email.getText().toString();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("email", email));
            responseMsg = RegisterPostService.send(params);
            return responseMsg.equals("SUCCEEDED");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //邮箱正则验证
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        return !TextUtils.isEmpty(strPattern) && strEmail.matches(strPattern);
    }
}
