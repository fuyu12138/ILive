package com.graduation.fuyu.ilive.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graduation.fuyu.ilive.R;

/**
 * 主页推荐栏
 * Created by root on 18-5-23.
 */

public class HomeRecommend extends LinearLayout {
    private ImageView icon;
    private TextView name;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;

    public HomeRecommend(Context context) {
        super(context);
        InitView();
    }

    public HomeRecommend(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        InitView();
    }

    public HomeRecommend(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView();
    }

    private void InitView(){
        View.inflate(getContext(), R.layout.recommend_home,this);
        icon=findViewById(R.id.recommend_iv_icon);
        name=findViewById(R.id.recommend_tv_name);
        recyclerView=findViewById(R.id.recommend_rv);
        relativeLayout=findViewById(R.id.recommend_rl_more);
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }
}
