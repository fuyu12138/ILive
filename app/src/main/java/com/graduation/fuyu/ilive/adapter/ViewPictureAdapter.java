package com.graduation.fuyu.ilive.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.graduation.fuyu.ilive.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by root on 18-2-26.
 */

public class ViewPictureAdapter extends PagerAdapter {

    private static List<ImageView> List;
//    private static List<String> banners_location=new ArrayList<>();
//    private Context context;
//    private String image="http://139.196.124.195";
    //获取滑动空间的数量，设为最大值后可无限向右滑动
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    //框架里用来判断view的id是不是这个object
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    //初始化
    /**
     * 当viewpager低于三个页面无限滑动时会八错
     * The specified child already has a parent. You must call removeView() on the child's parent first.
     * 为了不影响程序运行，加了个判断把parent移除了
     * 这个处理不影响三个或以上页面滑动
     * 但是，如果低于三个页面，当进行第二轮循环时，无法正常显示图片，直接变成白色
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position=position%List.size();
        if (List.get(position).getParent()!=null) {
            container.removeView(List.get(position));
        }
//        String image_location=image+banners_location.get(position);
//        Glide.with(context).load(image_location).into(List.get(position));
        container.addView(List.get(position));
        return List.get(position);
    }

    //因为PagerAdapter里只缓存三张图片，如果超出范围，则调用此方法销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
//        container.removeView((View) object);
        position=position%List.size();
        container.removeView(List.get(position));
    }

    public List<ImageView> getList() {
        return List;
    }

    public void setList(List<ImageView> List) {
        ViewPictureAdapter.List = List;
    }
}
