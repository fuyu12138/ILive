package com.graduation.fuyu.ilive.util;

import android.content.Context;
import android.net.Uri;

/**
 * 把资源id转化成Uri
 * Created by root on 18-4-6.
 */

public class ToUri {
    private static final String ANDROID_RESOURCE = "android.resource://";
    private static final String FOREWARD_SLASH = "/";

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }
}
