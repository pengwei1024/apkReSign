package com.apkfuns.apkresign;

/**
 * Created by pengwei on 16/4/15.
 */
public class DeviceUtil {

    /**
     * 是否为window系统,其他默认走linux
     *
     * @return
     */
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }
}
