package com.graduation.fuyu.ilive.connection;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * XMPP服务器连接管理
 * Created by root on 18-3-15.
 */

public class XMPPConnectionManager extends XMPPTCPConnection {

    public static AbstractXMPPConnection abstractXMPPConnection;

    public XMPPConnectionManager(CharSequence jid, String password) {
        super(jid, password);
    }

    /**
     * 获取xmpp服务器连接
     * @return AbstractXMPPConnection
     */
    public static AbstractXMPPConnection getConnection(){
        if (abstractXMPPConnection==null){
            OpenConnection();
        }
        return abstractXMPPConnection;
    }

    /**
     * 开启连接
     */
    private static void OpenConnection(){
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        String host = "139.196.124.195";
        int port = 5222;
        String serviceName = "izf813mgctz7h8z";
        configBuilder
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(host)
                .setPort(port)
                .setServiceName(serviceName);
        abstractXMPPConnection = new XMPPTCPConnection(configBuilder.build());
        try {
            abstractXMPPConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
