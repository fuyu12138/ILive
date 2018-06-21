package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.adapter.LabelRecyclerAdapter;
import com.graduation.fuyu.ilive.adapter.LabelRecyclerAdapter2;
import com.graduation.fuyu.ilive.adapter.LiveRoomRecyclerAdapter;
import com.graduation.fuyu.ilive.adapter.MyOnScroll;
import com.graduation.fuyu.ilive.pojo.Label;
import com.graduation.fuyu.ilive.pojo.LabelBean;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.service.LabelPostService;
import com.graduation.fuyu.ilive.service.LiveRoomPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LabelCommonActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView labelRecyclerView;
    private RecyclerView.LayoutManager labelLayoutManager;
    private LabelRecyclerAdapter2 labelAdapter;
    private List<String> labels=new ArrayList<>();
    private List<LabelBean> labelBeans=new ArrayList<>();


    private ImageView arrow;
    private RecyclerView smallRecyclerView;
    private StaggeredGridLayoutManager smallLayoutManager;
    private LabelRecyclerAdapter smallAdapter;
    private TextView edit;
    private String username;

    private SwipeRefreshLayout mSwipeLayout;

    private int offset=0;
    private int number=6;
    private String LabelSelected="";

    private RecyclerView mRecyclerView;
    private LiveRoomRecyclerAdapter mAdapter;
    private RecyclerView.OnScrollListener onScrollListener;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<LiveRoom> liveRooms=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_label_common);
        toolbar=findViewById(R.id.activity_label_common_toolbar);
        labelRecyclerView=findViewById(R.id.activity_label_common_rv_already);
        arrow=findViewById(R.id.activity_label_common_iv_arrow);
        smallRecyclerView=findViewById(R.id.activity_label_common_rv_small);
        edit=findViewById(R.id.activity_label_common_edit);
        mRecyclerView=findViewById(R.id.activity_label_common_rv_card);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LabelCommonActivity.this,LabelEditActivity.class);
                startActivity(intent);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        arrow.setSelected(false);

        labelLayoutManager=new GridLayoutManager(LabelCommonActivity.this,3);
        labelRecyclerView.setLayoutManager(labelLayoutManager);
//        initData(labels);
        labelAdapter=new LabelRecyclerAdapter2(LabelCommonActivity.this, labelBeans);
        labelRecyclerView.setAdapter(labelAdapter);
        labelAdapter.setOnClickSelectorLabel(new LabelRecyclerAdapter2.onClickSelectorLabel() {

            @Override
            public void labelSelector(int selected) {
                for (LabelBean l :
                        labelBeans) {
                    l.setSelected(false);
                }
                labelBeans.get(selected).setSelected(true);
                labelRecyclerView.scrollToPosition(selected);
                smallAdapter.setLabelBeans(labelBeans);
                smallAdapter.notifyDataSetChanged();
                smallRecyclerView.scrollToPosition(selected);
                offset=0;
                LabelSelected=labelBeans.get(selected).getLabel();
                new getLiveRoomByLabel().execute();
            }
        });

        smallLayoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL);
        smallRecyclerView.setLayoutManager(smallLayoutManager);
        smallAdapter=new LabelRecyclerAdapter(LabelCommonActivity.this,labelBeans);
        smallRecyclerView.setAdapter(smallAdapter);
        smallAdapter.setOnClickSelectorLabel(new LabelRecyclerAdapter.onClickSelectorLabel() {

            @Override
            public void labelSelector(int selected) {
                for (LabelBean l :
                        labelBeans) {
                    l.setSelected(false);
                }
                labelBeans.get(selected).setSelected(true);
                smallRecyclerView.scrollToPosition(selected);
                labelAdapter.setLabelBeans(labelBeans);
                labelAdapter.notifyDataSetChanged();
                labelRecyclerView.scrollToPosition(selected);
                LabelSelected=labelBeans.get(selected).getLabel();
                offset=0;
                new getLiveRoomByLabel().execute();
            }
        });

        labelRecyclerView.setVisibility(View.VISIBLE);
        smallRecyclerView.setVisibility(View.GONE);


        mSwipeLayout=findViewById(R.id.activity_label_common_swipe);
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

        mLayoutManager=new GridLayoutManager(LabelCommonActivity.this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter=new LiveRoomRecyclerAdapter(LabelCommonActivity.this, liveRooms);
        mRecyclerView.setAdapter(mAdapter);
        onScrollListener=new MyOnScroll() {
            @Override
            public void scrollBottom() {
                boolean isLoading=mSwipeLayout.isRefreshing();
                if (!isLoading){
                    new getLiveRoomByLabel().execute();
                }
            }
        };
        mRecyclerView.addOnScrollListener(onScrollListener);

        if (labelBeans.size()!=0){
            LabelSelected=labelBeans.get(0).getLabel();
            new getLiveRoomByLabel().execute();
        }


        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrow.isSelected()){
                    arrow.setSelected(false);
                    labelRecyclerView.setVisibility(View.VISIBLE);
                    smallRecyclerView.setVisibility(View.GONE);
                }else {
                    arrow.setSelected(true);
                    labelRecyclerView.setVisibility(View.GONE);
                    smallRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences=getSharedPreferences("ilive", Context.MODE_APPEND);
        username=sharedPreferences.getString("username","匿名");
    }

    /**
     * 下滑刷新的监听
     */
    Boolean isRefresh=false;
    class RefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            //检查是否处于刷新状态
            if (!isRefresh) {
                isRefresh = true;
                offset=0;
                new getLiveRoomByLabel().execute();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    class getLiveRoomByLabel extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("label", LabelSelected));
            params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
            params.add(new BasicNameValuePair("number", String.valueOf(number)));
            return LiveRoomPostService.getLiveRoomByLabel(params);
        }

        @Override
        protected void onPostExecute(String s) {
            // TODO: 18-5-22 判断
            JSONObject mJSONObject;
            List<LiveRoom> liveRooms=new ArrayList<>();
            try {
                mJSONObject = new JSONObject(s);
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
                    liveRooms.add(liveRoom);
                }
                if (offset==0){
                    mAdapter.setData(liveRooms);
                }else {
                    mAdapter.addData(liveRooms);
                }
                mAdapter.notifyDataSetChanged();
                offset+=number;
                mSwipeLayout.setRefreshing(false);
                isRefresh = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void initWindow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @SuppressLint("StaticFieldLeak")
    class getUserLabelTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            return LabelPostService.getUserLabel(params);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s==null){
                Toast.makeText(LabelCommonActivity.this,"error",Toast.LENGTH_SHORT).show();
            } else if (s.equals("null")) {
                Toast.makeText(LabelCommonActivity.this,"您还没有设置标签呢！",Toast.LENGTH_SHORT).show();
            }else {
                labels=ResolveLabel(s);
                Label.setCommon(labels);
                Label label=new Label();
                labelBeans=label.getCommonBean();
                labelAdapter.setLabelBeans(labelBeans);
                labelAdapter.notifyDataSetChanged();
                smallAdapter.setLabelBeans(labelBeans);
                smallAdapter.notifyDataSetChanged();
                if (labelBeans.size()!=0){
                    LabelSelected=labelBeans.get(0).getLabel();
                    new getLiveRoomByLabel().execute();
                }else {
                    LabelSelected="";
                    liveRooms=new ArrayList<>();
                    mAdapter.setData(liveRooms);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getUserLabelTask().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private List<String> ResolveLabel(String label){
        List<String> commonLabel=new ArrayList<>();
        List<LabelBean> labelBeans=new ArrayList<>();
        StringBuffer temp= new StringBuffer();
        Boolean flag=false;
        for (int i=0;i<label.length();i++){
            if (label.charAt(i)=='#'){
                flag=true;
            }else {
                temp.append(label.charAt(i));
            }
            if (flag){
                commonLabel.add(temp.toString());
                temp.setLength(0);
                flag=false;
            }

        }
//        for (String labels: commonLabel
//             ) {
//            LabelBean labelBean=new LabelBean(labels,false);
//            labelBeans.add(labelBean);
//        }
//        if (labelBeans.size()!=0){
//            labelBeans.get(0).setSelected(true);
//        }
        return commonLabel;
    }
}
