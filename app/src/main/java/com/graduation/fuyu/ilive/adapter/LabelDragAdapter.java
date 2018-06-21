package com.graduation.fuyu.ilive.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.graduation.fuyu.ilive.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * LabelEdit上用户喜好的列表适配器。
 * 提供拖拽排序和单击删除功能
 *
 * 标签列表适配器
 * label三种状态:
 *      1.未被选中，处于待选择状态：灰线 灰字 不显示删除
 *      2.被选中，处于待选择状态： 红线 红字 不显示删除
 *      3.处于待删除状态：       灰线 灰字 显示删除
 * Created by root on 18-5-8.
 */

public class LabelDragAdapter extends RecyclerView.Adapter <LabelDragAdapter.ViewHolder>{
    private Context context;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    private List<String> labels;

    public LabelDragAdapter(Context context, List<String>labels) {
        super();
        this.context=context;
        this.labels=labels;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (labels.size() == 1) {
//                    Snackbar.make(view, "至少选择一个标签", Snackbar.LENGTH_SHORT).show();
//                } else {
                    if (onClickClearLabel!=null){
                        onClickClearLabel.labelName(labels.get(position));
                        removeData(position);
                    }
//                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView labelName;
        ImageView minus;
        ViewHolder(View itemView) {
            super(itemView);
            labelName=itemView.findViewById(R.id.item_label_tv);
            minus=itemView.findViewById(R.id.item_label_iv_minus);
            labelName.setSelected(false);
            minus.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public String toString() {
        StringBuilder common= new StringBuilder();
        for (String label: labels
             ) {
            common.append(label).append("#");
        }
        return common.toString();
    }

    /**
     * 自定义接口回调
     * 在activity中获取被删除的标签内容
     */
    private onClickClearLabel onClickClearLabel;
    public interface onClickClearLabel{
        void labelName(String clearLabel);
    }
    public void setOnClickClearLabel(onClickClearLabel onClickClearLabel) {
        this.onClickClearLabel = onClickClearLabel;
    }

    public void move(int fromPosition,int toPosition){
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(labels, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(labels, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }
    //  添加数据
    public void addData(String label) {
        labels.add(label);
        notifyItemInserted(labels.size()-1);
        notifyDataSetChanged();
    }
    //  删除数据
    public void removeData(int position) {
        labels.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
}
