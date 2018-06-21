package com.graduation.fuyu.ilive.connection;

import com.graduation.fuyu.ilive.util.InputStreamToString;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过Post方式与服务器通信
 * Created by root on 18-3-16.
 */

public class HttpPost {
    // 请求超时
    private static final int REQUEST_TIMEOUT = 0;
    // 读取超时
    private static final int SO_TIMEOUT = 0;
    // 通过 POST 方式获取HTTP服务器数据
    public static String executeHttpPost(String servlet, List<NameValuePair> params) {
        String SERVER = "http://139.196.124.195:8080";
        String PROJECT = "/LoginServer/";
        String baseURL = SERVER + PROJECT + servlet;
        String responseMsg = "FAILED";
        try {
            //连接到服务器端相应的Servlet
            org.apache.http.client.methods.HttpPost request = new org.apache.http.client.methods.HttpPost(baseURL);
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()==200)//是否成功收取信息
                responseMsg = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMsg;
    }


    /**
     * Get方式获取banners图片的相对地址
     * @param servlet
     * @return
     */
    public static String bannerHttpPost(String servlet) {

        String SERVER = "http://139.196.124.195:8080";
        String PROJECT = "/LoginServer/";
        String baseURL = SERVER + PROJECT + servlet;

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
            return null;

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
