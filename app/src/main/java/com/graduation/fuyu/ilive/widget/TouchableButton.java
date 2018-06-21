package com.graduation.fuyu.ilive.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 解决当setOnTouchListener的警告
 * android Button has setOnTouchListener called on it but does not override performClick
 * Created by root on 18-3-16.
 */

public class TouchableButton extends android.support.v7.widget.AppCompatButton {
    public TouchableButton(Context context) {
        super(context);
    }

    public TouchableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
