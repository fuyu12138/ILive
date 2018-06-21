package com.graduation.fuyu.ilive.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.graduation.fuyu.ilive.LoginActivity;
import com.graduation.fuyu.ilive.MainActivity;
import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.adapter.ViewPictureAdapter;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.service.BannerPostService;
import com.graduation.fuyu.ilive.util.RoundBitmap;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * viewpager的圆角实现有点瑕疵，哈哈哈
 * 本代码直接把图片画成圆角，但是在onScroll的时候圆角会消失
 * 有空再尝试怎么蒙上一层圆角矩形来完美实现
 *
 * Created by root on 18-2-26.
 */

public class ViewPicture extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private LinearLayout dotGroup;
    private ViewPager viewPager;

    float beforeX;
    List<ImageView> mList;
//    private String image1="http://139.196.124.195/banner/banner1.png";
    private String image="http://139.196.124.195";

    private ViewPictureAdapter viewPictureAdapter;
    Handler mHandler=new Handler();
    public ViewPicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.viewpicture, this);
        viewPager=findViewById(R.id.view_pager);
        dotGroup= view.findViewById(R.id.dotGroup);
        mList = new ArrayList<>();
//        viewPager.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        //获取起始坐标值
//                        beforeX = motionEvent.getX();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if(motionEvent.getX() - beforeX < 0){ //禁止左滑
//                            return true;
//                        }
//                        //重新对起始坐标赋值
//                        beforeX = motionEvent.getX();
//                        break;
//                }
//                return false;
//            }
//        });
//        viewPager.setOffscreenPageLimit(3);
        new BannerTask().execute();
//        int[] mImages = {R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4};
//        for(int i = 0; i< 4; i++){
//            ImageView imageView=new ImageView(context);
//
//            /*
//            ==========================glide加载图片到imageView并放入viewPager===================
//             */
//            Glide.with(getContext()).load(image1).into(imageView);
//            /*
//             * ===========================把drawable资源转化为圆角bitmap资源========================
//             * 这个方法本地资源加载可以用，但是不知道为什么Glide加载无法用getDrawable()再设置
//             */
////            BitmapDrawable bitmapDrawable= (BitmapDrawable) imageView.getDrawable();
////            Bitmap bitmap=bitmapDrawable.getBitmap();
////            bitmap=RoundBitmap.getRoundBitmap(bitmap,30f);
////
////            imageView.setImageBitmap(bitmap);
//            mList.add(imageView);
//            ImageView dot=new ImageView(context);
//            dot.setImageResource(R.drawable.dot_selector);
//            int dot_size=getResources().getDimensionPixelSize(R.dimen.dot_size);
//            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(dot_size,dot_size);
//            if (i>0){
//                params.leftMargin=getResources().getDimensionPixelSize(R.dimen.dot_margin);
//                dot.setSelected(false);
//            }else {
//                dot.setSelected(true);
//            }
//            dot.setLayoutParams(params);
//            dotGroup.addView(dot);
//        }
//        viewPictureAdapter=new ViewPictureAdapter();
//        viewPictureAdapter.setList(mList);
//        viewPager.setAdapter(viewPictureAdapter);
//        viewPager.addOnPageChangeListener(this);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int currentPosition=viewPager.getCurrentItem();
//                if(currentPosition==viewPager.getAdapter().getCount()-1){
//                    viewPager.setCurrentItem(0);
//                }else {
//                    viewPager.setCurrentItem(currentPosition+1);
//                }
//                mHandler.postDelayed(this,3000);
//            }
//        },3000);
    }


    private List<String> banners_location=new ArrayList<>();
//    private String banners;
    @SuppressLint("StaticFieldLeak")
    class BannerTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return BannerPostService.getBanners();
        }
        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(String banners) {
//            if (banners!=null&&!banners.equals("error")){
            if (false){
                try {
                    JSONObject bannersJson=new JSONObject(banners);
                    JSONArray bannersList=bannersJson.getJSONArray("banners");
                    for (int i=0;i<bannersList.length();i++){
                        banners_location.add(bannersList.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i< banners_location.size(); i++){
                    ImageView imageView=new ImageView(getContext());
                    String image_location=image+banners_location.get(i);
                    Glide.with(getContext()).load(image_location).dontAnimate().placeholder(ContextCompat.getDrawable(getContext(),R.mipmap.banner1)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            /*
             * ===========================把drawable资源转化为圆角bitmap资源========================
             * 这个方法本地资源加载可以用，但是不知道为什么Glide加载无法用getDrawable()再设置
             */
            BitmapDrawable bitmapDrawable= (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap=bitmapDrawable.getBitmap();
            bitmap=RoundBitmap.getRoundBitmap(bitmap,30f);
            imageView.setImageBitmap(bitmap);
                    mList.add(imageView);
                    ImageView dot=new ImageView(getContext());
                    dot.setImageResource(R.drawable.dot_selector);
                    int dot_size=getResources().getDimensionPixelSize(R.dimen.dot_size);
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(dot_size,dot_size);
                    if (i>0){
                        params.leftMargin=getResources().getDimensionPixelSize(R.dimen.dot_margin);
                        dot.setSelected(false);
                    }else {
                        dot.setSelected(true);
                    }
                    dot.setLayoutParams(params);
                    dotGroup.addView(dot);
                }
            }
            else{
                int[] mImages = {R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4};
                for(int i = 0; i< mImages.length; i++){
                    ImageView imageView=new ImageView(getContext());
                    imageView.setImageResource(mImages[i]);
                    mList.add(imageView);
                    ImageView dot=new ImageView(getContext());
                    dot.setImageResource(R.drawable.dot_selector);
                    int dot_size=getResources().getDimensionPixelSize(R.dimen.dot_size);
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(dot_size,dot_size);
                    if (i>0){
                        params.leftMargin=getResources().getDimensionPixelSize(R.dimen.dot_margin);
                        dot.setSelected(false);
                    }else {
                        dot.setSelected(true);
                    }
                    dot.setLayoutParams(params);
                    dotGroup.addView(dot);
                }
            }
            viewPictureAdapter=new ViewPictureAdapter();
            viewPictureAdapter.setList(mList);
            viewPager.setAdapter(viewPictureAdapter);
            viewPager.addOnPageChangeListener(ViewPicture.this);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int currentPosition=viewPager.getCurrentItem();
                    if(currentPosition==viewPager.getAdapter().getCount()-1){
                        viewPager.setCurrentItem(0);
                    }else {
                        viewPager.setCurrentItem(currentPosition+1);
                    }
                    mHandler.postDelayed(this,3000);
                }
            },3000);
        }
    }


    int lastPosition;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        position=position%viewPictureAdapter.getList().size();
//        viewPager.setCurrentItem(position);
        dotGroup.getChildAt(position).setSelected(true);
        dotGroup.getChildAt(lastPosition).setSelected(false);
        lastPosition=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
