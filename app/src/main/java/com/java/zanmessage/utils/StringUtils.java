package com.java.zanmessage.utils;

import android.text.TextUtils;

public class StringUtils {
    public static boolean isMatch(String str, String match) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            return str.matches(match);
        }
    }

    public static String getInitial(String str) {
        if (str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase();
    }
}
