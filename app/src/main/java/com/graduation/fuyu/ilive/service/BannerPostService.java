package com.graduation.fuyu.ilive.service;

import com.graduation.fuyu.ilive.connection.HttpPost;

/**
 * Created by root on 18-3-18.
 */

public class BannerPostService {
    public static String getBanners() {


        String servlet = "BannerServlet";
        return HttpPost.bannerHttpPost(servlet);
    }
}
