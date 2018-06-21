package com.graduation.fuyu.ilive.Manager;



import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.util.JidTransform;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 18-2-25.
 */

public class ChatRoomManager {

    public static String CONFERENCE="@conference.";

    /**
     * 创建房间（聊天室）
     * @param connection
     * @param name 群的名称
     * @param description 描述
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException
     */
    public static MultiUserChat createOrJoinLiveRoom(AbstractXMPPConnection connection,String name,String description) throws XMPPException.XMPPErrorException, SmackException {
        MultiUserChatManager manager=MultiUserChatManager.getInstanceFor(connection);
        String Jid= JidTransform.parseBareJid(name)+CONFERENCE+connection.getServiceName();
        MultiUserChat multiUserChat=manager.getMultiUserChat(Jid);
        multiUserChat.createOrJoin(name);

        Form configForm=multiUserChat.getConfigurationForm();
        Form submitForm=configForm.createAnswerForm();
        for (FormField  formField:configForm.getFields()){
            if (formField.getVariable()!=null){
                submitForm.setDefaultAnswer(formField.getVariable());
            }
        }
        /*
        ==============================================配置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
         */
        //房间所有者
        List<String> owners=new ArrayList<>();
        owners.add(connection.getUser());
        submitForm.setAnswer("muc#roomconfig_roomowners",owners);
        //房间名字
        submitForm.setAnswer("muc#roomconfig_roomname",JidTransform.parseBareJid(name)+"的房间");
        //房间描述
        submitForm.setAnswer("muc#roomconfig_roomdesc",description);
        //房间最大人数
        submitForm.setAnswer("muc#roomconfig_maxusers", Collections.singletonList("50"));
        //允许owner更改主题
        submitForm.setAnswer("muc#roomconfig_changesubject",true);
        //房间持久
        submitForm.setAnswer("muc#roomconfig_persistentroom", true);
        //在列出目录中列出房间
        submitForm.setAnswer("muc#roomconfig_publicroom", true);
        //关闭房间仅对成员开放
        submitForm.setAnswer("muc#roomconfig_membersonly", false);
        //允许占有者邀请其他人
        submitForm.setAnswer("muc#roomconfig_allowinvites", true);
        //不需要房间密码
        submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",false);
        //仅允许注册昵称登陆
        submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
        //记录房间对话
        submitForm.setAnswer("muc#roomconfig_enablelogging", false);
        //允许使用者更改nickname
        submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
        //允许用户注册房间
        submitForm.setAnswer("x-muc#roomconfig_registration", false);

        multiUserChat.sendConfigurationForm(submitForm);

        return multiUserChat;
    }

    /**
     * 获取所有的房间列表
     * @param connection
     * @return
     */
    public static List<ChatRoom> getAllRooms(AbstractXMPPConnection connection) throws Exception {
        String ServerName=connection.getServiceName();
        List<ChatRoom>list =new ArrayList<>();
        MultiUserChatManager manager=MultiUserChatManager.getInstanceFor(connection);
        List<HostedRoom> hostedRooms=manager.getHostedRooms(manager.getServiceNames().get(0));
        for (HostedRoom room:hostedRooms) {
                RoomInfo info=manager.getRoomInfo(room.getJid());
                ChatRoom chatRoom=new ChatRoom();
                chatRoom.setJid(info.getRoom());
                chatRoom.setName(info.getName());
                chatRoom.setDescription(info.getDescription());
                chatRoom.setOccupantsCount(info.getOccupantsCount());
                list.add(chatRoom);
        }

        return list;
    }
    public static RoomInfo getRoomByJid(AbstractXMPPConnection connection,String Jid) throws Exception{
        RoomInfo chatRoom;
        MultiUserChatManager manager=MultiUserChatManager.getInstanceFor(connection);
        chatRoom=manager.getRoomInfo(Jid);
        return chatRoom;
    }

    /**
     * 加入聊天室
     * @param connection
     * @param jid
     * @param nickname
     * @return
     * @throws Exception
     */
    public static MultiUserChat joinChatRoom(AbstractXMPPConnection connection,String jid,String nickname) throws Exception {
        MultiUserChat multiUserChat=MultiUserChatManager.getInstanceFor(connection).getMultiUserChat((jid));
        DiscussionHistory history=new DiscussionHistory();
        history.setSeconds(0);//不接受历史信息
        multiUserChat.join(nickname,null,history,5000);
        return multiUserChat;
    }
}
