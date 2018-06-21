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
 * email输入邮箱
 * Created by root on 18-3-7.
 */

public class EmailEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener,TextWatcher{
    private Drawable mRightDrawable;
    private Drawable mLeftDrawable;

    public EmailEditText(Context context) {
        this(context,null);
    }

    public EmailEditText(Context context, AttributeSet attrs) {
        this(context, attrs,android.R.attr.editTextStyle);
    }

    public EmailEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Drawable[] drawables =getCompoundDrawablesRelative();
        mLeftDrawable=drawables[0];
        mRightDrawable=drawables[2];
        if(mRightDrawable==null){
            mRightDrawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_clear_pink_24dp);
        }
        if(mLeftDrawable==null){
            mLeftDrawable=ContextCompat.getDrawable(getContext(),R.drawable.ic_email_black_24dp);
        }
        mLeftDrawable.setBounds(0,0,50,50);
        mRightDrawable.setBounds(0,0,50,50);
        this.addTextChangedListener(this);
        this.setOnFocusChangeListener(this);
        setEmailVisible(false);
    }


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

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setEmailVisible(charSequence.length()>0);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            mLeftDrawable=ContextCompat.getDrawable(getContext(),R.drawable.ic_email_pink_24dp);
            mLeftDrawable.setBounds(0,0,50,50);
            setEmailVisible(getText().length()>0);
        }else {
            mLeftDrawable=ContextCompat.getDrawable(getContext(),R.drawable.ic_email_black_24dp);
            mLeftDrawable.setBounds(0,0,50,50);
            setEmailVisible(false);
        }
    }

    private void setEmailVisible(Boolean isVisible){
        Drawable right= isVisible? mRightDrawable:null;
        setCompoundDrawables(mLeftDrawable,getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
    }
}
