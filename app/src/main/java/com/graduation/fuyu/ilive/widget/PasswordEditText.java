package com.graduation.fuyu.ilive.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.graduation.fuyu.ilive.R;

/**
 * 不知道为什么Activity里重写onTextChanged方法，当Text改变时，会调用init()初始化
 * Created by root on 18-3-15.
 */

public class PasswordEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener,TextWatcher {
    private Drawable mRightDrawable;
    private Drawable mLeftDrawable;

    public PasswordEditText(Context context) {
        this(context,null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs,android.R.attr.editTextStyle);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化editText控件
     * 获取左右两个Drawable文件（虽然一直获取不到）
     * 初始化左右两个Drawable
     * 设置文本输入和焦点监听
     */
    private void init() {
        Drawable[] drawables =getCompoundDrawablesRelative();
        mLeftDrawable=drawables[0];
        mRightDrawable=drawables[2];
        if(mRightDrawable==null){
            mRightDrawable= ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_pink_24dp);
        }
        if(mLeftDrawable==null){
            mLeftDrawable=ContextCompat.getDrawable(getContext(),R.drawable.ic_lock_outline_pink_24dp);
        }
        mLeftDrawable.setBounds(0,0,50,50);
        mRightDrawable.setBounds(0,0,50,50);
        this.addTextChangedListener(this);
        this.setOnFocusChangeListener(this);
        setClearVisible(false);
    }


    /**
     * 根据触摸坐标，设置ACTION_UP事件，清空输入
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(getCompoundDrawables()[2]!=null){
                boolean touchable=event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if(touchable){
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //当文本改变时，判断文本长度
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setClearVisible(charSequence.length()>0);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * 当焦点改变时，设置左边图标变化
     * 失去焦点时，设置x不可见
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            mLeftDrawable=ContextCompat.getDrawable(getContext(),R.drawable.ic_lock_outline_pink_24dp);
            mLeftDrawable.setBounds(0,0,50,50);
            setClearVisible(getText().length()>0);
        }else {
            mLeftDrawable=ContextCompat.getDrawable(getContext(),R.drawable.ic_lock_outline_black_24dp);
            mLeftDrawable.setBounds(0,0,50,50);
            setClearVisible(false);
        }
    }

    private void setClearVisible(Boolean isVisible){
        Drawable right= isVisible? mRightDrawable:null;
        setCompoundDrawables(mLeftDrawable,getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
    }
}
