package com.graduation.fuyu.ilive.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 绘制带圆角的图片
 * 方法：
 *  设置画布为圆角矩形，把图片放在该画布上即可
 * Created by root on 18-3-17.
 */

public class RoundBitmap {
    public static Bitmap getRoundBitmap(Bitmap mBitmap,Float index){
        Bitmap bitmap=Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);

        //设置矩形大小
        Rect rect=new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
        RectF rectF=new RectF(rect);

        //清屏
        canvas.drawARGB(0,0,0,0);

        //画圆角
        canvas.drawRoundRect(rectF,index,index,paint);

        //取两层绘制，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //把原生的图片放到这个画布上，使之带有画布的效果
        canvas.drawBitmap(mBitmap,rect,rect,paint);

        return bitmap;
    }
}
