package com.graduation.fuyu.ilive.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.graduation.fuyu.ilive.R;

import java.util.ArrayList;

/**
 * 监听多个EditText是否为空，设置button enable;
 * Created by root on 18-3-9.
 */

public class CheckBlankButton extends android.support.v7.widget.AppCompatButton{
    private ArrayList<EditText> et_list;
    private CheckBlankButton mButtonCheckBlank;

    public CheckBlankButton(Context context) {
        super(context);
    }

    public CheckBlankButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBlankButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void AddListeningEditTexts(ArrayList<EditText>editTexts){
        CheckBlank_TextWatcher checkBlank_textWatcher=new CheckBlank_TextWatcher(this);
        et_list=editTexts;
        setEnabled(false);
        mButtonCheckBlank.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.btn_check_unenabled));
        for (int i=0;i<et_list.size();i++){
            et_list.get(i).addTextChangedListener(checkBlank_textWatcher);
        }
    }

    private class CheckBlank_TextWatcher implements TextWatcher {

        CheckBlank_TextWatcher(CheckBlankButton buttonCheckBlank){
            mButtonCheckBlank=buttonCheckBlank;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            for (int i=0;i<et_list.size();i++){
                if(et_list.get(i).getText().length()==0){
                    mButtonCheckBlank.setEnabled(false);
                    mButtonCheckBlank.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.btn_check_unenabled));
                    return;
                }
            }
            mButtonCheckBlank.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.btn_check_selector));
            mButtonCheckBlank.setEnabled(true);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
