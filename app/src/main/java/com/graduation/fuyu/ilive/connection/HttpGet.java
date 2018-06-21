package com.graduation.fuyu.ilive.connection;

import com.graduation.fuyu.ilive.util.InputStreamToString;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by root on 18-3-25.
 */

public class HttpGet {
    /**
     * Get方式获取banners图片的相对地址
     * @param servlet
     * @return
     */
    public static String HttpGet(String servlet,String params) {

        String SERVER = "http://139.196.124.195:8080";
        String PROJECT = "/LoginServer/";
        String baseURL = SERVER + PROJECT + servlet+"?"+params;

        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            conn = (HttpURLConnection) new URL(baseURL).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                return InputStreamToString.parseInfo(is);
            }
            return "error";

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return "error";
    }
}
