package de.troido.resigner.utils;

import com.apkfuns.apkresign.Global;

import java.io.*;
import java.util.Properties;

/**
 * Created by pengwei08 on 2015/7/23.
 */
public class PropertiesUtil {

    private static String filePath = Global.LOCAL_HOST;
    private static String fileName = Global.LOCAL_CONFIG_FILE;
    private static Properties props = new Properties();

    private static boolean hasReset = false;

    static {
        reset();
    }

    /**
     * 重置环境
     */
    private static void reset() {
        try {
            File path = new File(filePath);
            File proFile = new File(fileName);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!proFile.exists()) {
                proFile.createNewFile();
            }
            props.load(new FileInputStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * д��ֵ
     *
     * @param key
     * @param value
     */
    public static void put(String key, String value) {
        try {
            final OutputStream fos = new FileOutputStream(fileName);
            props.setProperty(key, value);
            props.store(fos, "");
        } catch (IOException e) {
            if (!hasReset) {
                reset();
                hasReset = true;
            } else {
                e.printStackTrace();
            }
        }
    }

    public static String get(String key) {
        return get(key, "");
    }

    public static String get(String key, String defaultValue) {
        String value = props.getProperty(key);
        return value == null ? defaultValue : value;
    }
}
