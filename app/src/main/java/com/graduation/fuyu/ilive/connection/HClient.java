package com.graduation.fuyu.ilive.connection;

import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import java.io.File;

/**
 * Created by root on 18-6-6.
 */

public class HClient {
    /*
     * private Context mContext ;
     *
     * public Hclient ( Context c ) { this.mContext = c ; }
     */

    public Boolean UpLoadFile(String str,String username) {

        String targetURL = null;// TODO 指定URL
        File targetFile = null;// TODO 指定上传文件
        targetFile = new File(str);
        targetURL = "http://139.196.124.195:8080/LoginServer/UpLoadImageServlet?action=xxx&username="+username;
        PostMethod filePost = new PostMethod(targetURL);
        try {
            // 通过以下方法可以模拟页面参数提交
//            filePost.setParameter("username", username);
            Part[] parts =
                    {new FilePart(targetFile.getName(), targetFile)};
            filePost.setRequestEntity(new MultipartRequestEntity(
                    parts, filePost.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams()
                    .setConnectionTimeout(5000);
            int status = client.executeMethod(filePost);
            Log.e("Hclient", String.valueOf(status) );
            if (status == HttpStatus.SC_OK) {
                // 上传成功
                return true;
            } else {
                return false;
                // 上传失败
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            filePost.releaseConnection();

        }
        return false;
    }

    public Boolean UpLoadHead(String str,String username) {

        String targetURL = null;// TODO 指定URL
        File targetFile = null;// TODO 指定上传文件
        targetFile = new File(str);
        targetURL = "http://139.196.124.195:8080/LoginServer/UpLoadHeadServlet?username="+username;
        PostMethod filePost = new PostMethod(targetURL);
        try {
            // 通过以下方法可以模拟页面参数提交
//            filePost.setParameter("username", username);
            Part[] parts =
                    {new FilePart(targetFile.getName(), targetFile)};
            filePost.setRequestEntity(new MultipartRequestEntity(
                    parts, filePost.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams()
                    .setConnectionTimeout(5000);
            int status = client.executeMethod(filePost);
            Log.e("Hclient", String.valueOf(status) );
            if (status == HttpStatus.SC_OK) {
                // 上传成功
                return true;
            } else {
                return false;
                // 上传失败
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            filePost.releaseConnection();

        }
        return false;
    }
}