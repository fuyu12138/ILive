package com.graduation.fuyu.ilive.service;

import com.graduation.fuyu.ilive.connection.HttpPost;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by root on 18-5-17.
 */

public class LabelPostService {

    public static String getAllLabel() {
        List<NameValuePair> params=new ArrayList<>();
        String servlet = "GetAllLabelServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }

    public static String getUserLabel(List<NameValuePair> params){
        String servlet = "GetUserLabelServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }

    public static String setCommmonLabel(List<NameValuePair> params) {
        String servlet = "UpdateCommonServlet";
        return HttpPost.executeHttpPost(servlet, params);
    }
}
