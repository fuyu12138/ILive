package com.graduation.fuyu.ilive.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.graduation.fuyu.ilive.util.ScreenUtils;

/**
 * 拖拽悬浮按钮
 * Created by root on 18-4-14.
 */

public class DragFloatActionButton extends FloatingActionButton {
    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;
    private int virtualHeight;

    public DragFloatActionButton(Context context) {
        super(context);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtils.getScreenHeight(getContext());
        statusHeight = ScreenUtils.getStatusHeight(getContext());
        virtualHeight=ScreenUtils.getVirtualBarHeigh(getContext());
    }

    private int lastX;
    private int lastY;

    private boolean isDrag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                Log.e("down---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = true;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些手机无法触发点击事件的问题
                int distance= (int) Math.sqrt(dx*dx+dy*dy);
                Log.e("distance---->",distance+"");
                if(distance<3){//给个容错范围，不然有部分手机还是无法点击
                    isDrag=false;
                    break;
                }

                float x = getX() + dx;
                float y = getY() + dy;

                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
                // y = y < statusHeight ? statusHeight : (y + getHeight() >= screenHeight ? screenHeight - getHeight() : y);
                if (y<0){
                    y=0;
                }
                if (y>screenHeight-statusHeight-getHeight()){
                    y=screenHeight-statusHeight-getHeight();
                }
                setX(x);
                setY(y);

                lastX = rawX;
                lastY = rawY;
                Log.e("move---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf + " " + isDrag+"  statusHeight="+statusHeight+ " virtualHeight"+virtualHeight+ " screenHeight"+ screenHeight+"  getHeight="+getHeight()+" y"+y);
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    setPressed(false);
                    Log.e("ACTION_UP---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                    if (rawX >= screenWidthHalf) {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(500)
                                .xBy(screenWidth - getWidth() - getX())
                                .start();
                    } else {
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                        oa.setInterpolator(new DecelerateInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }
                }
                Log.e("up---->",isDrag+"");
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);

    }
//    private int parentHeight;
//    private int parentWidth;
//
//    public DragFloatActionButton(Context context) {
//        super(context);
//    }
//
//    public DragFloatActionButton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//    }
//
//    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//    }
//    private int lastX;
//    private int lastY;
//
//    private boolean isDrag;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int rawX = (int) event.getRawX();
//        int rawY = (int) event.getRawY();
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
////                this.bringToFront();
//                setPressed(true);
//                isDrag=false;
//                getParent().requestDisallowInterceptTouchEvent(true);
//                lastX=rawX;
//                lastY=rawY;
//                ViewGroup parent;
//                if(getParent()!=null){
//                    parent= (ViewGroup) getParent();
//                    parentHeight=parent.getHeight();
//                    parentWidth=parent.getWidth();
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
////                this.bringToFront();
//                if(parentHeight<=0||parentWidth==0){
//                    isDrag=false;
//                    break;
//                }else {
//                    isDrag=true;
//                }
//                int dx=rawX-lastX;
//                int dy=rawY-lastY;
//                //这里修复一些华为手机无法触发点击事件
//                int distance= (int) Math.sqrt(dx*dx+dy*dy);
//                if(distance<3){
//                    isDrag=false;
//                    break;
//                }
//                float x=getX()+dx;
//                float y=getY()+dy;
//                //检测是否到达边缘 左上右下
//                x=x<0?0:x>parentWidth-getWidth()?parentWidth-getWidth():x;
//                y=getY()<0?0:getY()+getHeight()>parentHeight?parentHeight-getHeight():y;
//                setX(x);
//                setY(y);
//                lastX=rawX;
//                lastY=rawY;
//                Log.i("aa","isDrag="+isDrag+"getX="+getX()+";getY="+getY()+";parentWidth="+parentWidth);
//                break;
//            case MotionEvent.ACTION_UP:
//                if(!isNotDrag()){
//                    //恢复按压效果
//                    setPressed(false);
////                    //Log.i("getX="+getX()+"；screenWidthHalf="+screenWidthHalf);
////                    /*
////                    ======================向左吸附的时候会导致控件不在最前被覆盖，同意向右吸附＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
////                     */
//////                    if(rawX>=parentWidth/2){
////                        //靠右吸附
////                        animate().setInterpolator(new DecelerateInterpolator())
////                                .setDuration(500)
////                                .xBy(parentWidth-getWidth()-getX())
////                                .start();
////                    this.bringToFront();
//////                    }else {
//////                        //靠左吸附
//////                        ObjectAnimator oa=ObjectAnimator.ofFloat(this,"x",getX(),0);
//////                        oa.setInterpolator(new DecelerateInterpolator());
//////                        oa.setDuration(500);
//////                        oa.start();
//////                    }
//                }
//                break;
//        }
//        //如果是拖拽则消s耗事件，否则正常传递即可。
//        return !isNotDrag() || super.onTouchEvent(event);
//    }
//
//    private boolean isNotDrag(){
//        return !isDrag&&(getX()==0
//                ||(getX()==parentWidth-getWidth()));
//    }
}
