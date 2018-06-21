package com.graduation.fuyu.ilive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.fuyu.ilive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签列表适配器
 * label三种状态:
 *      1.未被选中，处于待选择状态：灰线 灰字 不显示删除
 *      2.被选中，处于待选择状态： 红线 红字 不显示删除
 *      3.处于待删除状态：       灰线 灰字 显示删除
 * Created by root on 18-5-8.
 */

public class LabelSelectorAdapter extends RecyclerView.Adapter <LabelSelectorAdapter.ViewHolder>{
    private Context context;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    private List<String> labels= new ArrayList<>();

    private List<String> userLabels;

    public LabelSelectorAdapter(Context context, List<String>labels,List<String>userLabels) {
        super();
        this.context=context;
        this.labels=labels;
        this.userLabels=userLabels;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_label,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String name= labels.get(position);
        holder.labelName.setText(name);
        if (judge(name)){
            holder.labelName.setSelected(true);
            holder.labelName.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.minus.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.labelName.isSelected()){
                    holder.labelName.setSelected(false);
                    holder.labelName.setTextColor(context.getResources().getColor(R.color.colorGrey666666));
                    holder.minus.setVisibility(View.INVISIBLE);
                    if (onClickSelectorLabel!=null){
                        onClickSelectorLabel.labelSelector(labels.get(position),false);
                    }
                }else {
                    holder.labelName.setSelected(true);
                    holder.labelName.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    holder. minus.setVisibility(View.VISIBLE);
                    if (onClickSelectorLabel!=null){
                        onClickSelectorLabel.labelSelector(labels.get(position),true);
                    }
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView labelName;
        public ImageView minus;
        ViewHolder(View itemView) {
            super(itemView);
            labelName=itemView.findViewById(R.id.item_label_tv);
            minus=itemView.findViewById(R.id.item_label_iv_minus);
            labelName.setSelected(false);
            minus.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 自定义接口回调
     */
    private onClickSelectorLabel onClickSelectorLabel;
    public interface onClickSelectorLabel{
        void labelSelector(String LabelName,Boolean state);
    }

    public void setOnClickSelectorLabel(onClickSelectorLabel onClickSelectorLabel) {
        this.onClickSelectorLabel = onClickSelectorLabel;
    }


    //  添加数据
    public void addData(String label) {
//      在list中添加数据，并通知条目加入一条
        labels.add(label);
        //添加动画
        notifyItemInserted(labels.size()-1);
        notifyDataSetChanged();
    }

    /**
     *
     * @param label
     * @return true:有 false: 无
     */
    private Boolean judge(String label){
        for (String temp:userLabels
             ) {
            if (temp.equals(label)){
                return true;
            }
        }
        return false;
    }
}
