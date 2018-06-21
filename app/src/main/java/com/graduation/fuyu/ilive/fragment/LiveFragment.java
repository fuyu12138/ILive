package com.graduation.fuyu.ilive.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.LabelCommonActivity;
import com.graduation.fuyu.ilive.LabelEntertainmentActivity;
import com.graduation.fuyu.ilive.LabelPcActivity;
import com.graduation.fuyu.ilive.LabelPhoneActivity;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.adapter.LiveRoomRecyclerAdapter;
import com.graduation.fuyu.ilive.adapter.RecyclerViewAdapter;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.pojo.Label;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.service.LabelPostService;
import com.graduation.fuyu.ilive.service.LiveRoomPostService;
import com.graduation.fuyu.ilive.widget.HomeRecommend;
import com.graduation.fuyu.ilive.widget.ViewPicture;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播列表
 */
public class LiveFragment extends Fragment implements View.OnClickListener {

//    private RecyclerView mRecyclerView;
//    private RecyclerViewAdapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
//
//    private AbstractXMPPConnection connection;
//    private List<ChatRoom> roomList=new ArrayList<>();
    private ViewPicture viewPicture;

//    private TextView roomNum;

    private LinearLayout entertainment;
    private LinearLayout pcGame;
    private LinearLayout phoneGame;
    private LinearLayout common;
    //是否在刷新中
    private Boolean isRefresh=false;
    private SwipeRefreshLayout mSwipeLayout;

    private HomeRecommend recommend1;
    private HomeRecommend recommend2;
    private HomeRecommend recommend3;
    private HomeRecommend recommend4;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_live,container, false);
//        mRecyclerView=view.findViewById(R.id.fragment_live_recycler_view);
//        roomNum=view.findViewById(R.id.fragment_live_tv_1);
        viewPicture=view.findViewById(R.id.fragment_live_viewPager);
        entertainment=view.findViewById(R.id.fragment_live_entertainment);
        pcGame=view.findViewById(R.id.fragment_live_pcGame);
        phoneGame=view.findViewById(R.id.fragment_live_phoneGame);
        common=view.findViewById(R.id.fragment_live_common);
        recommend1=view.findViewById(R.id.fragment_live_hr_1);
        recommend2=view.findViewById(R.id.fragment_live_hr_2);
        recommend3=view.findViewById(R.id.fragment_live_hr_3);
        recommend4=view.findViewById(R.id.fragment_live_hr_4);
        recommendBindView();

        entertainment.setOnClickListener(this);
        phoneGame.setOnClickListener(this);
        pcGame.setOnClickListener(this);
        common.setOnClickListener(this);
        /*
        ==============================SwipeRefreshLayout下拉刷新==========================
         */
        mSwipeLayout=view.findViewById(R.id.fragment_live_swipeLayout);
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


//        mLayoutManager=new GridLayoutManager(getContext(),2);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter=new RecyclerViewAdapter(getContext(), roomList);
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setNestedScrollingEnabled(false);
//        new ChatRoomLoader().execute();
        new getHomeRecommend().execute();
        new getAllLabel().execute();
        return view;
    }

    private ImageView im_rc1;
    private TextView tv_rc1;
    private RecyclerView rv_rc1;
    private RelativeLayout rl_rc1;
    private RecyclerView.LayoutManager mLayoutManager1;
    private LiveRoomRecyclerAdapter mAdapter1;
    private List<LiveRoom> liveRooms1=new ArrayList<>();

    private ImageView im_rc2;
    private TextView tv_rc2;
    private RecyclerView rv_rc2;
    private RelativeLayout rl_rc2;
    private RecyclerView.LayoutManager mLayoutManager2;
    private LiveRoomRecyclerAdapter mAdapter2;
    private List<LiveRoom> liveRooms2=new ArrayList<>();

    private ImageView im_rc3;
    private TextView tv_rc3;
    private RecyclerView rv_rc3;
    private RelativeLayout rl_rc3;
    private RecyclerView.LayoutManager mLayoutManager3;
    private LiveRoomRecyclerAdapter mAdapter3;
    private List<LiveRoom> liveRooms3=new ArrayList<>();

    private ImageView im_rc4;
    private TextView tv_rc4;
    private RecyclerView rv_rc4;
    private RelativeLayout rl_rc4;
    private RecyclerView.LayoutManager mLayoutManager4;
    private LiveRoomRecyclerAdapter mAdapter4;
    private List<LiveRoom> liveRooms4=new ArrayList<>();

    private void recommendBindView(){
        im_rc1=recommend1.getIcon();
        tv_rc1=recommend1.getName();
        rv_rc1=recommend1.getRecyclerView();
        rl_rc1=recommend1.getRelativeLayout();

        im_rc2=recommend2.getIcon();
        tv_rc2=recommend2.getName();
        rv_rc2=recommend2.getRecyclerView();
        rl_rc2=recommend2.getRelativeLayout();

        im_rc3=recommend3.getIcon();
        tv_rc3=recommend3.getName();
        rv_rc3=recommend3.getRecyclerView();
        rl_rc3=recommend3.getRelativeLayout();

        im_rc4=recommend4.getIcon();
        tv_rc4=recommend4.getName();
        rv_rc4=recommend4.getRecyclerView();
        rl_rc4=recommend4.getRelativeLayout();

        im_rc1.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.video));
        im_rc2.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.entertainment));
        im_rc3.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.pcgame));
        im_rc4.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.phone));
        tv_rc1.setText("推荐直播");
        tv_rc2.setText("娱乐");
        tv_rc3.setText("游戏");
        tv_rc4.setText("手游");
        rl_rc1.setVisibility(View.GONE);
        rl_rc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getContext(), LabelEntertainmentActivity.class);
                startActivity(intent);
            }
        });
        rl_rc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getContext(), LabelPcActivity.class);
                startActivity(intent);
            }
        });
        rl_rc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getContext(), LabelPhoneActivity.class);
                startActivity(intent);
            }
        });

        mLayoutManager1=new GridLayoutManager(getContext(),2);
        rv_rc1.setLayoutManager(mLayoutManager1);
        mAdapter1=new LiveRoomRecyclerAdapter(getContext(), liveRooms1);
        rv_rc1.setAdapter(mAdapter1);
        rv_rc1.setNestedScrollingEnabled(false);

        mLayoutManager2=new GridLayoutManager(getContext(),2);
        rv_rc2.setLayoutManager(mLayoutManager2);
        mAdapter2=new LiveRoomRecyclerAdapter(getContext(), liveRooms2);
        rv_rc2.setAdapter(mAdapter2);
        rv_rc2.setNestedScrollingEnabled(false);

        mLayoutManager3=new GridLayoutManager(getContext(),2);
        rv_rc3.setLayoutManager(mLayoutManager3);
        mAdapter3=new LiveRoomRecyclerAdapter(getContext(), liveRooms3);
        rv_rc3.setAdapter(mAdapter3);
        rv_rc3.setNestedScrollingEnabled(false);

        mLayoutManager4=new GridLayoutManager(getContext(),2);
        rv_rc4.setLayoutManager(mLayoutManager4);
        mAdapter4=new LiveRoomRecyclerAdapter(getContext(), liveRooms4);
        rv_rc4.setAdapter(mAdapter4);
        rv_rc4.setNestedScrollingEnabled(false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    Intent intent;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_live_entertainment:
                intent=new Intent(getContext(), LabelEntertainmentActivity.class);
                break;
            case R.id.fragment_live_pcGame:
                intent=new Intent(getContext(), LabelPcActivity.class);
                break;
            case R.id.fragment_live_phoneGame:
                intent=new Intent(getContext(), LabelPhoneActivity.class);
                break;
            case R.id.fragment_live_common:
                intent=new Intent(getContext(), LabelCommonActivity.class);
                break;
        }
        startActivity(intent);
    }

//    @SuppressLint("StaticFieldLeak")
//    class ChatRoomLoader extends AsyncTask<Void,Void,Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            connection= XMPPConnectionManager.getConnection();
//            try {
//                roomList= ChatRoomManager.getAllRooms(connection);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//            return true;
//        }
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            if(aBoolean){
//                mAdapter.setData(roomList);
//                mAdapter.notifyDataSetChanged();
//                roomNum.setText(String.valueOf(roomList.size()));
//                mSwipeLayout.setRefreshing(false);
//                isRefresh = false;
//
//                Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(getContext(),"刷新失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    @SuppressLint("StaticFieldLeak")
    class getAllLabel extends AsyncTask<Void,Void,String>{

        List<String> labels;
        List<String> classifications;
        @Override
        protected String doInBackground(Void... voids) {
            return LabelPostService.getAllLabel();
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject mJSONObject;
            labels=new ArrayList<>();
            classifications=new ArrayList<>();
            try {
                mJSONObject = new JSONObject(s);
                JSONArray label=mJSONObject.getJSONArray("labelname");
                for (int i=0;i<label.length();i++){
                    labels.add(label.getString(i));
                }
                JSONArray classification=mJSONObject.getJSONArray("classification");
                for (int i=0;i<classification.length();i++){
                    classifications.add(classification.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Label.label=labels;
            Label.classification=classifications;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class getHomeRecommend extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            return LiveRoomPostService.getHomeRecommend(params);
        }

        @Override
        protected void onPostExecute(String s) {
            // TODO: 18-5-22 判断
            Log.e("liveFragment", s );
            JSONObject mJSONObject;
            liveRooms1=new ArrayList<>();
            liveRooms2=new ArrayList<>();
            liveRooms3=new ArrayList<>();
            liveRooms4=new ArrayList<>();
            try {
                mJSONObject = new JSONObject(s);
                JSONArray namecls=mJSONObject.getJSONArray("namecls");
                JSONArray classification=mJSONObject.getJSONArray("classification");
                JSONArray name=mJSONObject.getJSONArray("name");
                JSONArray roomname=mJSONObject.getJSONArray("roomname");
                JSONArray label=mJSONObject.getJSONArray("label");
                JSONArray introduce=mJSONObject.getJSONArray("introduce");
                JSONArray cover=mJSONObject.getJSONArray("cover");
                JSONArray head=mJSONObject.getJSONArray("head");
                for (int i=0;i<namecls.length();i++){
                    LiveRoom liveRoom=new LiveRoom();
                    for (int j=0;j<name.length();j++){
                        if (namecls.getString(i).equals(name.getString(j))){
                            liveRoom.setName(name.getString(i));
                            liveRoom.setRoomname(roomname.getString(i));
                            liveRoom.setLabel(label.getString(i));
                            liveRoom.setIntroduce(introduce.getString(i));
                            liveRoom.setCover(cover.getString(i));
                            liveRoom.setHead(head.getString(i));
                            break;
                        }
                    }
                    switch (classification.getString(i)){
                        case "recommend":
                            liveRooms1.add(liveRoom);
                            break;
                        case "entertainment":
                            liveRooms2.add(liveRoom);
                            break;
                        case "pcgame" :
                            liveRooms3.add(liveRoom);
                            break;
                        case "phonegame":
                            liveRooms4.add(liveRoom);
                            break;
                    }
                }
                mAdapter1.setData(liveRooms1);
                mAdapter2.setData(liveRooms2);
                mAdapter3.setData(liveRooms3);
                mAdapter4.setData(liveRooms4);
                mAdapter1.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
                mAdapter3.notifyDataSetChanged();
                mAdapter4.notifyDataSetChanged();
                mSwipeLayout.setRefreshing(false);
                isRefresh = false;
                if (recommend1.getVisibility()==View.GONE){
                    recommend1.setVisibility(View.VISIBLE);
                    recommend2.setVisibility(View.VISIBLE);
                    recommend3.setVisibility(View.VISIBLE);
                    recommend4.setVisibility(View.VISIBLE);
                }
//                Toast.makeText(getContext(),cover.toString(),Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                new getHomeRecommend().execute();
            }
        }
    }
}
