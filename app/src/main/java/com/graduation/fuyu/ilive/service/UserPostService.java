package com.graduation.fuyu.ilive.service;

import com.graduation.fuyu.ilive.connection.HttpPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 * Created by root on 18-6-10.
 */

public class UserPostService {
    public static String getHeadByName(List<NameValuePair> params){
        params.add(new BasicNameValuePair("action", "getHead"));
        String servlet = "UserServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
}
