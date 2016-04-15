package de.troido.resigner.utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by pengwei08 on 2015/7/23.
 */
public class PropertiesUtil {

    private static String filePath = System.getProperty("user.home") + "/apkResign";
    private static String fileName = filePath + "/config.properties";
    private static Properties props = new Properties();

    static {
        try {
            File path = new File(filePath);
            File proFile = new File(fileName);
            if (!path.exists() || !proFile.exists()) {
                path.mkdirs();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
