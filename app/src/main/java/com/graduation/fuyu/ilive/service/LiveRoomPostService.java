package com.graduation.fuyu.ilive.service;

import com.graduation.fuyu.ilive.connection.HttpPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 获取直播间信息
 * Created by root on 18-5-21.
 */

public class LiveRoomPostService {

    public static String getLiveRoomByLabel(List<NameValuePair> params){
        params.add(new BasicNameValuePair("action", "getByLabel"));
        String servlet = "LiveRoomServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }

    public static String getHomeRecommend(List<NameValuePair> params){
        params.add(new BasicNameValuePair("action", "getHomeRecommend"));
        String servlet = "LiveRoomServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
    public static String searchLiveRoom(List<NameValuePair> params){
        params.add(new BasicNameValuePair("action", "searchLiveRoom"));
        String servlet = "LiveRoomServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
    public static String getLiveRoomByName(List<NameValuePair> params){
        params.add(new BasicNameValuePair("action", "getLiveRoomByName"));
        String servlet = "LiveRoomServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }

}
