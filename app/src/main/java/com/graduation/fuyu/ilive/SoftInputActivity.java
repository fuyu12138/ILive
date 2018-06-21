package com.graduation.fuyu.ilive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.graduation.fuyu.ilive.util.SoftInputUtils.showSoftInputFromWindow;

public class SoftInputActivity extends AppCompatActivity {

    private RelativeLayout rl;
    private ImageView send;
    private EditText et_content;
    public static final String action = "fuyu.broadcast.softInput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_soft_input);
        rl=findViewById(R.id.activity_soft_rl);
        send=findViewById(R.id.activity_soft_send);
        et_content=findViewById(R.id.activity_soft_et);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_content.getText().toString().isEmpty()) {
                    Toast.makeText(SoftInputActivity.this, "弹幕内容不能为空哦！", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(action);
                    intent.putExtra("ChatMsg", et_content.getText().toString());
                    LocalBroadcastManager.getInstance(SoftInputActivity.this).sendBroadcast(intent);
                    onBackPressed();
                }
            }
        });
        showSoftInputFromWindow(this,et_content);

        et_content.setImeOptions(EditorInfo.IME_ACTION_SEND);
        et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i==EditorInfo.IME_ACTION_SEND){
                    if (et_content.getText().toString().isEmpty()){
                        Toast.makeText(SoftInputActivity.this,"弹幕内容不能为空哦！",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent=new Intent(action);
                        intent.putExtra("ChatMsg",et_content.getText().toString());
                        LocalBroadcastManager.getInstance(SoftInputActivity.this).sendBroadcast(intent);
                        onBackPressed();
                    }
                }
                return true;
            }
        });
    }

    private void initWindow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
}
