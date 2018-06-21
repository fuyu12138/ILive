package com.graduation.fuyu.ilive.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.graduation.fuyu.ilive.R;


/**
 *
 * Created by root on 18-4-26.
 */

public class BarArrow extends LinearLayout {
    private ImageView imageView;
    private TextView textView;
    private ImageView arrow;

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getArrow() {
        return arrow;
    }

    public void setArrow(ImageView arrow) {
        this.arrow = arrow;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public BarArrow(Context context) {
        super(context);
        InitView();
    }

    public BarArrow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        InitView();
    }

    public BarArrow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView();
    }


    private void InitView(){
        View.inflate(getContext(), R.layout.arrow_bar,this);
        imageView=findViewById(R.id.arrow_bar_image);
        textView=findViewById(R.id.arrow_bar_text);
        arrow=findViewById(R.id.arrow_bar_arrow);
    }

}
