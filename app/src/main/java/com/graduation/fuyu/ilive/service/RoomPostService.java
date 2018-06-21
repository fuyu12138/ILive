package com.graduation.fuyu.ilive.service;

import com.graduation.fuyu.ilive.connection.HttpGet;
import com.graduation.fuyu.ilive.connection.HttpPost;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by root on 18-3-25.
 */

public class RoomPostService {

    public static String setcover(List<NameValuePair> params) {
        String servlet = "CoverServlet";
        String responseMsg;
        responseMsg = HttpPost.executeHttpPost(servlet, params);
        return responseMsg;
    }
    public static String setIntroduce(List<NameValuePair> params) {
        String servlet = "IntroduceServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
    public static String setLabel(List<NameValuePair> params) {
        String servlet = "LabelServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
    public static String setRoomName(List<NameValuePair> params) {
        String servlet = "LiveNameServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
//    public static String send(List<NameValuePair> params) {
//        // 定位服务器的Servlet
//        String servlet = "FocusServlet";
//        // 通过 POST 方式获取 HTTP 服务器数据
//        String responseMsg;
//        responseMsg = HttpPost.executeHttpPost(servlet, params);
//
//        return responseMsg;
//    }
//    public static String getFocusLists(String params) {
//        String servlet = "getFocusServlet";
//        return HttpGet.HttpGet(servlet,params);
//    }
//    public static String cancleFocus(String params){
//        String servlet="CancleFocusServlet";
//        return HttpGet.HttpGet(servlet,params);
//    }
//    public static String inquire(String params){
//        String servlet="InquireFocusServlet";
//        return HttpGet.HttpGet(servlet,params);
//    }
}
