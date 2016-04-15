package com.apkfuns.apkresign;

/**
 * Created by pengwei on 16/4/15.
 */
public class TextUtils {

    /**
     * 字符串是否为空
     *
     * @param text
     * @return
     */
    public static boolean notEmpty(String text) {
        return text != null && text.trim().length() > 0;
    }
}
