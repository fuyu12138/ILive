package com.graduation.fuyu.ilive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.graduation.fuyu.ilive.adapter.LabelDragAdapter;
import com.graduation.fuyu.ilive.adapter.LabelFragmentAdapter;
import com.graduation.fuyu.ilive.adapter.LabelRecyclerAdapter;
import com.graduation.fuyu.ilive.adapter.LabelSelectorAdapter;
import com.graduation.fuyu.ilive.fragment.EntertainmentFragment;
import com.graduation.fuyu.ilive.fragment.PaintingFragment;
import com.graduation.fuyu.ilive.fragment.PcGameFragment;
import com.graduation.fuyu.ilive.fragment.PhoneGameFragment;
import com.graduation.fuyu.ilive.pojo.Label;
import com.graduation.fuyu.ilive.service.LabelPostService;
import com.graduation.fuyu.ilive.service.RoomPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LabelEditActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView edit;
    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private LabelDragAdapter mAdapter;
    private List<String> labels;

    private String cancleLabel;

    private TabLayout tabLayout;

    private ViewPager viewPager;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private LabelFragmentAdapter mFragmentAdapter;
    private EntertainmentFragment fragment1;
    private PcGameFragment fragment2;
    private PhoneGameFragment fragment3;
    private PaintingFragment fragment4;

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;
    public static final String action = "fuyu.broadcast.action";
    public static final String action1 = "fuyu.broadcast.actionFr";

    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_label_edit);
        toolbar=findViewById(R.id.activity_label_edit_toolbar);
        edit=findViewById(R.id.activity_label_edit_edit);
        recyclerView=findViewById(R.id.activity_label_edit_rv_already);
        viewPager=findViewById(R.id.activity_label_edit_vp);
        tabLayout=findViewById(R.id.activity_label_edit_tab_pager);
        constraintLayout=findViewById(R.id.activity_label_edit_constraint);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        IntentFilter filter = new IntentFilter(action);
        registerReceiver(broadcastReceiver, filter);
        /*
        =======================================recyclerView=========================================
         */
        mLayoutManager=new GridLayoutManager(LabelEditActivity.this,3);
        recyclerView.setLayoutManager(mLayoutManager);
        labels=new ArrayList<>();
        Label label=new Label();
        labels= label.getCommon();
        mAdapter=new LabelDragAdapter(LabelEditActivity.this, labels);

        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences=getSharedPreferences("ilive", Context.MODE_APPEND);
        username=sharedPreferences.getString("username","error");
        /**
         * recyclerView长按拖拽的实现
         */
        final ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags,swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags,swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                mAdapter.move(fromPosition,toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//                    viewHolder.itemView.setAlpha(0.5f);
                    viewHolder.itemView.setScaleX(1.2f);
                    viewHolder.itemView.setScaleY(1.2f);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
                mAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(mAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter.setOnClickClearLabel(new LabelDragAdapter.onClickClearLabel(){
            @Override
            public void labelName(String clearLabel) {
                Intent intent = new Intent(action);
                intent.putExtra("labelAc", clearLabel);
                LocalBroadcastManager.getInstance(LabelEditActivity.this).sendBroadcast(intent);
            }
        });

        /*
        ====================================fragment================================================
         */
        fragment1 = new EntertainmentFragment();
        fragment2 = new PcGameFragment();
        fragment3 = new PhoneGameFragment();
        fragment4 = new PaintingFragment();
        mFragmentList.add(fragment1);
        mFragmentList.add(fragment2);
        mFragmentList.add(fragment3);
        mFragmentList.add(fragment4);
        mFragmentAdapter = new LabelFragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);


        broadcastManager = LocalBroadcastManager.getInstance(LabelEditActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action1);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String labelName=intent.getExtras().getString("labelFa");
                Boolean state=intent.getExtras().getBoolean("labelFaState");
                if (state){
                    mAdapter.addData(labelName);
                }else {
                    int position=getPosition(labelName);
                    if (position!=-1){
                        mAdapter.removeData(position);
                    }
                }

            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
    private int getPosition(String lableName){
        for (int i=0;i<labels.size();i++){
            if (labels.get(i).equals(lableName)){
                return i;
            }
        }
        return -1;
    }


    private void initWindow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }


    class saveCommonLabelTask extends AsyncTask<Void,Void,Boolean>{
        String responseMsg;
        @Override
        protected Boolean doInBackground(Void... voids) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", username));
            params.add(new BasicNameValuePair("label", mAdapter.toString()));
            responseMsg = LabelPostService.setCommmonLabel(params);
            return Objects.equals(responseMsg, "SUCCEEDED");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.e("mAdatpter", mAdapter.toString());
            if (aBoolean){
                finish();
            }else {
                new MaterialDialog.Builder(LabelEditActivity.this)
                        .content("网络不大给力喲，是否放弃本次编辑直接返回？")
                        .theme(Theme.DARK)
                        .positiveText("继续尝试")
                        .positiveColor(getResources().getColor(R.color.colorAccent))
                        .negativeText("放弃编辑")
                        .negativeColor(getResources().getColor(R.color.colorAccent))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new saveCommonLabelTask().execute();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new saveCommonLabelTask().execute();
    }


}
