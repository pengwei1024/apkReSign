package com.apkfuns.apkresign;

/**
 * Created by pengwei on 16/4/15.
 */
public interface Global {
    String VERSION = "1.2.0";

    String ABOUT_UEL = "https://github.com/pengwei1024/apkReSign";

    String LOCAL_HOST = System.getProperty("user.home") + "/apkResign";

    String LOCAL_CONFIG_FILE = LOCAL_HOST + "/config.properties";
}
