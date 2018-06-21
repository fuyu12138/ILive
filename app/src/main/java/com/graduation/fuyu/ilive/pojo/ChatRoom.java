package com.graduation.fuyu.ilive.pojo;

/**
 * 聊天室
 * 05-23 00:27:43.526 2977-2977/com.graduation.fuyu.ilive E/Jid: jf@conference.izf813mgctz7h8z
    05-23 00:27:43.526 2977-2977/com.graduation.fuyu.ilive E/liveJid: jf
 * Created by root on 18-2-25.
 */

public class ChatRoom {
    private String Jid;
    private String name;
    private int occupantsCount;
    private String description;

    public ChatRoom(){

    }


    public String getJid() {
        return Jid;
    }

    public void setJid(String jid) {
        Jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOccupantsCount() {
        return occupantsCount;
    }

    public void setOccupantsCount(int occupantsCount) {
        this.occupantsCount = occupantsCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
