package com.graduation.fuyu.ilive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.pojo.LabelBean;

import java.util.List;

/**
 * 单选标签列表适配器
 * label状态:
 *      1.未被选中：灰线 灰字
 *      2.被选中： 红线 红字
 * Created by root on 18-5-8.
 */

public class LabelRecyclerAdapter2 extends RecyclerView.Adapter <LabelRecyclerAdapter2.ViewHolder>{
    private Context context;

    //默认第一个被选中
    private int mSelectedPos = -1;

    private List<LabelBean> labelBeans;

    public List<LabelBean> getLabelBeans() {
        return labelBeans;
    }

    public void setLabelBeans(List<LabelBean> labelBeans) {
        this.labelBeans = labelBeans;
        for (int i = 0; i < labelBeans.size(); i++) {
            if (labelBeans.get(i).isSelected()) {
                mSelectedPos = i;
            }
        }
    }

    public LabelRecyclerAdapter2(Context context, List<LabelBean>labelBeans) {
        super();
        this.context=context;
        this.labelBeans=labelBeans;
        for (int i = 0; i < labelBeans.size(); i++) {
            if (labelBeans.get(i).isSelected()) {
                mSelectedPos = i;
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_label2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String name= labelBeans.get(position).getLabel();
        holder.labelName.setSelected(labelBeans.get(position).isSelected());
        holder.labelName.setText(name);
        holder.minus.setVisibility(View.GONE);
        if (labelBeans.get(position).isSelected()){
            holder.labelName.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }else {
            holder.labelName.setTextColor(context.getResources().getColor(R.color.colorGrey666666));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedPos!=position){
                    labelBeans.get(mSelectedPos).setSelected(false);
                    notifyItemChanged(mSelectedPos);
                    mSelectedPos = position;
                    labelBeans.get(mSelectedPos).setSelected(true);
                    notifyItemChanged(mSelectedPos);
                    if (onClickSelectorLabel!=null){
                        onClickSelectorLabel.labelSelector(position);
                    }
                }
            }
        });
    }
    /**
     * 自定义接口回调
     */
    private onClickSelectorLabel onClickSelectorLabel;
    public interface onClickSelectorLabel{
        void labelSelector(int selected);
    }

    public void setOnClickSelectorLabel(onClickSelectorLabel onClickSelectorLabel) {
        this.onClickSelectorLabel = onClickSelectorLabel;
    }

    @Override
    public int getItemCount() {
        return labelBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView labelName;
        ImageView minus;
        ViewHolder(View itemView) {
            super(itemView);
            labelName=itemView.findViewById(R.id.item_label_tv);
            minus=itemView.findViewById(R.id.item_label_iv_minus);
//            labelName.setSelected(false);
//            minus.setVisibility(View.INVISIBLE);

        }
    }
}
