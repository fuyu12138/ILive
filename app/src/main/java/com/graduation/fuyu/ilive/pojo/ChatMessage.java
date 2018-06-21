package com.graduation.fuyu.ilive.pojo;

/**
 * 聊天内容
 * Created by root on 18-2-26.
 */

public class ChatMessage {
    private String Userid;
    private String nickname;
    private String msg;

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ChatMessage(){

    }




}
