package com.graduation.fuyu.ilive.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * recyclerView下滑到底部
 * Created by root on 18-5-22.
 */

public abstract class MyOnScroll extends RecyclerView.OnScrollListener {

    private Boolean isBottom;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        isBottom = !recyclerView.canScrollVertically(1);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState==RecyclerView.SCROLL_STATE_IDLE&&isBottom){
            scrollBottom();
        }
    }

    public void scrollBottom(){

    }
}
