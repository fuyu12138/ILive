package com.graduation.fuyu.ilive.pojo;

/**
 * Created by root on 18-5-20.
 */

public class LabelBean extends SelectedBean {
    private String label;
    public LabelBean(String label,boolean isSelected) {
        this.label = label;
        setSelected(isSelected);
    }
    public String getLabel(){
        return label;
    }
}
