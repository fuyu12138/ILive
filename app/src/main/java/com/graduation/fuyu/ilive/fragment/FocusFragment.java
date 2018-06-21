package com.graduation.fuyu.ilive.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.LiveRoomActivity;
import com.graduation.fuyu.ilive.MainActivity;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.adapter.FocusRecyclerAdapter;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.service.FocusPostService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的关注
 */
public class FocusFragment extends Fragment {

    private RecyclerView recyclerView;
    private FocusRecyclerAdapter focusRecyclerAdapter;
    private List<LiveRoom> focusLists=new ArrayList<>();
    private String username;
    private Boolean isRefresh;
    private SwipeRefreshLayout mSwipeLayout;
    private TextView noFocus;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_focus, container, false);
        Context context = view.getContext();
        recyclerView=view.findViewById(R.id.fragment_focus_recycler);
        mSwipeLayout=view.findViewById(R.id.fragment_focus_swipeLayout);
        noFocus=view.findViewById(R.id.fragment_focus_tv_no_focus);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        /*
        ==============================SwipeRefreshLayout下拉刷新==========================
         */
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSwipeLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorWhite));
        // 设置圆圈的大小
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        //设置下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(new RefreshLayoutListener());


        username= ((MainActivity)getActivity()).getUsername();
        focusRecyclerAdapter=new FocusRecyclerAdapter(context,focusLists,username);
        recyclerView.setAdapter(focusRecyclerAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new getFocusLists().execute();
    }

    class getFocusLists extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            return FocusPostService.getFocusLists("username="+username);
        }

        @Override
        protected void onPostExecute(String s) {
            focusLists.clear();
            Log.e("Focus", s );
            try {
                JSONObject mJSONObject = new JSONObject(s);;
                JSONArray name=mJSONObject.getJSONArray("name");
                JSONArray roomname=mJSONObject.getJSONArray("roomname");
                JSONArray label=mJSONObject.getJSONArray("label");
                JSONArray introduce=mJSONObject.getJSONArray("introduce");
                JSONArray cover=mJSONObject.getJSONArray("cover");
                JSONArray head=mJSONObject.getJSONArray("head");
                for (int i=0;i<name.length();i++){
                    LiveRoom liveRoom=new LiveRoom();
                    liveRoom.setName(name.getString(i));
                    liveRoom.setRoomname(roomname.getString(i));
                    liveRoom.setLabel(label.getString(i));
                    liveRoom.setIntroduce(introduce.getString(i));
                    liveRoom.setCover(cover.getString(i));
                    liveRoom.setHead(head.getString(i));
                    focusLists.add(liveRoom);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSwipeLayout.setRefreshing(false);
            isRefresh = false;
            if (focusLists.isEmpty()){
                noFocus.setVisibility(View.VISIBLE);
            }else {
                noFocus.setVisibility(View.GONE);
            }
            focusRecyclerAdapter.setData(focusLists);
            focusRecyclerAdapter.notifyDataSetChanged();
//            Toast.makeText(getContext(), focusLists.get(0).getJid(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 下滑刷新的监听
     */
    class RefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            //检查是否处于刷新状态
            if (!isRefresh) {
                isRefresh = true;

                new getFocusLists().execute();
            }
        }
    }
}
