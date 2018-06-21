package com.graduation.fuyu.ilive.util;

/**
 * Created by root on 18-3-1.
 */

public class JidTransform {
    public static String parseBareJid(String jid) {
        int slashIndex = jid.indexOf('@');
        if (slashIndex < 0) {
            return jid;
        } else if (slashIndex == 0) {
            return "";
        } else {
            return jid.substring(0, slashIndex);
        }
    }
}
