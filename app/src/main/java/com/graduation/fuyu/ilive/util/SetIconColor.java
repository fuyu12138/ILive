package com.graduation.fuyu.ilive.util;

import android.graphics.ColorMatrixColorFilter;

/**
 * Created by root on 18-4-15.
 */

public class SetIconColor {
    public static ColorMatrixColorFilter setIconColor(int r, int g, int b, int a) {
        float[] colorMatrix = new float[]{
                0, 0, 0, 0, r,
                0, 0, 0, 0, g,
                0, 0, 0, 0, b,
                0, 0, 0, (float) a / 255, 0
        };
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
