package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.graduation.fuyu.ilive.service.RoomPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetRoomDescribeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static String RoomDescribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_set_room_describe);
        toolbar=findViewById(R.id.activity_room_describe_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (getFragmentManager().findFragmentById(R.id.activity_room_describe_content_frame) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_room_describe_content_frame, new RoomDescribeFragment())
                    .commit();
        }
    }

    public static class RoomDescribeFragment extends Fragment {
        private EditText editText;
        private int MAXLINES=8;
        private String username;
        private Button save;
        private String introduce;
        @SuppressLint("ClickableViewAccessibility")
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_room_describe, container, false);
            editText=view.findViewById(R.id.fragment_room_describe_et);
            save=view.findViewById(R.id.fragment_room_describe_save);

            editText.setText(RoomDescribe);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int lines = editText.getLineCount();
                    // 限制最大输入行数
                    if (lines > MAXLINES) {
                        String str = editable.toString();
                        int cursorStart = editText.getSelectionStart();
                        int cursorEnd = editText.getSelectionEnd();
                        if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                            str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                        } else {
                            str = str.substring(0, editable.length()-1);
                        }
                        // setText会触发afterTextChanged的递归
                        editText.setText(str);
                        // setSelection用的索引不能使用str.length()否则会越界
                        editText.setSelection(editText.getText().length());
                    }
                }
            });
            @SuppressLint("WrongConstant")
            SharedPreferences sharedPreferences=getActivity().getSharedPreferences("ilive", Context.MODE_APPEND);
            username=sharedPreferences.getString("username","error");
//            save.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    introduce=editText.getText().toString();
//                    new IntroduceTask().execute();
//                }
//            });
            save.setOnTouchListener(new SaveTouch());
            return view;
        }
        /**
         * 设置房间简介
         */
        @SuppressLint("StaticFieldLeak")
        class IntroduceTask extends AsyncTask<Void,Void,Boolean> {
            String responseMsg;
            @Override
            protected Boolean doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", username));
                params.add(new BasicNameValuePair("introduce", introduce));
                responseMsg = RoomPostService.setIntroduce(params);
                return Objects.equals(responseMsg, "SUCCEEDED");

            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean){
                    Toast.makeText(getActivity(),"描述设置成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),introduce,Toast.LENGTH_SHORT).show();
                }
            }
        }

        private class SaveTouch implements View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    save.setSelected(true);
                    save.setTextColor(getResources().getColor(R.color.colorAccentPressed));
                }else if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                    save.setSelected(false);
                    save.setTextColor(getResources().getColor(R.color.colorAccent));
                    introduce=editText.getText().toString();
                    new IntroduceTask().execute();
                }
                return false;
            }
        }
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
    }
}
