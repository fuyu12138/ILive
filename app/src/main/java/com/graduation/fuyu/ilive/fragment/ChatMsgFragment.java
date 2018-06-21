package com.graduation.fuyu.ilive.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.LiveRoomActivity;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.SoftInputActivity;
import com.graduation.fuyu.ilive.adapter.ChatMsgRecyclerViewAdapter;
import com.graduation.fuyu.ilive.media.IjkPlayerView;
import com.graduation.fuyu.ilive.pojo.ChatMessage;
import com.graduation.fuyu.ilive.util.SoftInputUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室
 */
public class ChatMsgFragment extends Fragment{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatMsgFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatMsgFragment newInstance(int columnCount) {
        ChatMsgFragment fragment = new ChatMsgFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private RecyclerView recyclerView;
    private MultiUserChat multiUserChat;
    private List<ChatMessage> chatMessages;
    private ChatMsgRecyclerViewAdapter chatListAdapter;
    private IjkPlayerView mPlayerView;
    private String getMsg;
    private String getName;
    private String username;
    private EditText et_content;

    public static final String action = "fuyu.broadcast.softInput";
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;
    private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_msg_list, container, false);
        Context context = view.getContext();
        ImageView iv_send = view.findViewById(R.id.fragment_chat_msg_iv_send);
        et_content=view.findViewById(R.id.fragment_chat_msg_et_content);
        linearLayout=view.findViewById(R.id.fragment_chat_msg_ll);
        recyclerView=view.findViewById(R.id.fragment_chat_msg_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        chatMessages=new ArrayList<>();
        chatListAdapter=new ChatMsgRecyclerViewAdapter(chatMessages);
        multiUserChat=((LiveRoomActivity)getActivity()).getMultiUserChat();
        username=((LiveRoomActivity)getActivity()).getUsername();
        mPlayerView=((LiveRoomActivity) getActivity()).getmPlayerView();
        recyclerView.setAdapter(chatListAdapter);
        new ChatMessageInit((LiveRoomActivity) getActivity()).execute();
//        iv_send.setOnClickListener(new sendMessage());
        multiUserChat.addMessageListener(new MessageListener() {
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
                }
                new ChatMassageLoad((LiveRoomActivity) getActivity()).execute();
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), SoftInputActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });
        et_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    Intent intent = new Intent(getContext(),SoftInputActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
//        et_content.setImeOptions(EditorInfo.IME_ACTION_SEND);
//        et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            String msg;
//            String msgJSON;
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i==EditorInfo.IME_ACTION_SEND){
//                    if (et_content.getText().toString().isEmpty()){
//                        Toast.makeText(getContext(),"弹幕内容不能为空哦！",Toast.LENGTH_SHORT).show();
//                    }else {
//                        try {
//                            msg=et_content.getText().toString();
//                            msgJSON="{\"nickname\":\""+username+"\",\"msg\":\""+msg+"\"}";
//                            multiUserChat.sendMessage(msgJSON);
//                        } catch (SmackException.NotConnectedException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getContext(),"发射失败",Toast.LENGTH_SHORT).show();
//                        }
//                        //弹幕统一由multiChatRoom监听发送,否则自己的弹幕会发送两遍
//                        et_content.setText("");
//                        et_content.clearFocus();
//                        SoftInputUtils.closeSoftInput(getContext());
//                        Toast.makeText(getContext(),"发射成功",Toast.LENGTH_SHORT).show();
//                    }
//                }
//                return false;
//            }
//        });
        /*
        ============================================广播注册===================================================================
         */
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String msgJSON;
                String chatMsg=intent.getExtras().getString("ChatMsg");
                try {
                    msgJSON="{\"nickname\":\""+username+"\",\"msg\":\""+chatMsg+"\"}";
                    multiUserChat.sendMessage(msgJSON);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),"发射失败",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(),"发射成功",Toast.LENGTH_SHORT).show();
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        return view;
    }
    @SuppressLint("StaticFieldLeak")
    class ChatMessageInit extends AsyncTask<Void,Void,Boolean> {
        private final WeakReference<LiveRoomActivity> weakReference;
        ChatMessageInit(LiveRoomActivity myActivity) {
            this.weakReference = new WeakReference<>(myActivity);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            LiveRoomActivity activity = weakReference.get();
            if (activity == null
                    || activity.isFinishing()
                    || activity.isDestroyed()) {
                // activity没了,就结束可以了
                return;
            }
            if (aBoolean){
                chatListAdapter.setData(chatMessages);
                chatListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加弹幕
     */
    @SuppressLint("StaticFieldLeak")
    class ChatMassageLoad extends AsyncTask<Void,Void,Boolean>{
        String name;
        private final WeakReference<LiveRoomActivity> weakReference;
        ChatMassageLoad(LiveRoomActivity myActivity) {
            this.weakReference = new WeakReference<>(myActivity);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            this.name=username;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            LiveRoomActivity activity = weakReference.get();
            if (activity == null
                    || activity.isFinishing()
                    || activity.isDestroyed()) {
                // activity没了,就结束可以了
                return;
            }
            if (aBoolean){
                chatListAdapter.setData(chatMessages);
                //判断是否是自己的弹幕，forself:true 框出弹幕
                if(getName.equals(name)){
                    mPlayerView.addDanmaku(getMsg,true,true);
                }else {
                    mPlayerView.addDanmaku(getMsg,true,false);
                }
                chatListAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatListAdapter.getItemCount()-1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    //    class sendMessage implements View.OnClickListener {
//         String msg;
//         String msgJSON;
//         @Override
//         public void onClick(View view) {
//             if (et_content.getText().toString().isEmpty()){
//                 Toast.makeText(getContext(),"弹幕内容不能为空哦！",Toast.LENGTH_SHORT).show();
//             }else {
//                 try {
//                     msg=et_content.getText().toString();
//                     msgJSON="{\"nickname\":\""+username+"\",\"msg\":\""+msg+"\"}";
//                     multiUserChat.sendMessage(msgJSON);
//                 } catch (SmackException.NotConnectedException e) {
//                     e.printStackTrace();
//                     Toast.makeText(getContext(),"发射失败",Toast.LENGTH_SHORT).show();
//                     return;
//                 }
//                 //弹幕统一由multiChatRoom监听发送,否则自己的弹幕会发送两遍
//                 et_content.setText("");
//                 et_content.clearFocus();
//                 SoftInputUtils.closeSoftInput(getContext());
//                 Toast.makeText(getContext(),"发射成功",Toast.LENGTH_SHORT).show();
//             }
//         }
//     }
}
