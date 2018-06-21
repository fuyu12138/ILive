package com.graduation.fuyu.ilive.service;

import com.graduation.fuyu.ilive.connection.HttpPost;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * 注册Service
 * 0:注册失败
 * 1：注册成功
 * Created by root on 18-3-1.
 */

public class RegisterPostService {

    public static String send(List<NameValuePair> params) {
        // 定位服务器的Servlet
        String servlet = "RegisterServlet";
        // 通过 POST 方式获取 HTTP 服务器数据
        String responseMsg;
        responseMsg = HttpPost.executeHttpPost(servlet, params);

        return responseMsg;
    }
}
