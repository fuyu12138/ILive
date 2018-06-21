package com.graduation.fuyu.ilive.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.adapter.LabelSelectorAdapter;
import com.graduation.fuyu.ilive.pojo.Label;

import java.util.ArrayList;
import java.util.List;


public class PhoneGameFragment extends Fragment {

    private List<String> labels;
    private List<String> userLabel;
    private LabelSelectorAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;
    public static final String action = "fuyu.broadcast.action";
    public static final String action1 = "fuyu.broadcast.actionFr";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entertainment,null);
        recyclerView=view.findViewById(R.id.fragment_entertainment_recycler);
        mLayoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(mLayoutManager);
        labels=new ArrayList<>();
        Label temp=new Label();
        labels=temp.getphoneGame_sample();
        userLabel=temp.getCommon();
        mAdapter=new LabelSelectorAdapter(getActivity(),labels,userLabel);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickSelectorLabel(new LabelSelectorAdapter.onClickSelectorLabel() {


            @Override
            public void labelSelector(String LabelName, Boolean state) {
                Intent intent=new Intent(action1);
                intent.putExtra("labelFa",LabelName);
                intent.putExtra("labelFaState",state);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });
        return view;
    }

    /**
     * 通过广播获取被删除标签名字后
     * 消除该Fragment同名标签选中状态
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String label=intent.getExtras().getString("labelAc");
                int position=getPosition(label);
                if (position!=-1){
                    LabelSelectorAdapter.ViewHolder viewHolder = (LabelSelectorAdapter.ViewHolder)
                            recyclerView.findViewHolderForAdapterPosition(position);
                    viewHolder.labelName.setSelected(false);
                    viewHolder.labelName.setTextColor(context.getResources().getColor(R.color.colorGrey666666));
                    viewHolder.minus.setVisibility(View.INVISIBLE);
                }

            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private int getPosition(String lableName){
        for (int i=0;i<labels.size();i++){
            if (labels.get(i).equals(lableName)){
                return i;
            }
        }
        return -1;
    }
}
