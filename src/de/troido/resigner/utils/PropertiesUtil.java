package de.troido.resigner.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by pengwei08 on 2015/7/23.
 */
public final class PropertiesUtil {

    private static String fileName = System.getProperty("user.dir") +
            "/assets/config.properties";
    private static Properties props = new Properties();
    static {
        try {
            props.load(new FileInputStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * –¥»Î÷µ
     * @param key
     * @param value
     */
    public static void put(String key, String value){
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

    public static String get(String key, String defaultValue){
        String value = props.getProperty(key);
        return value == null?defaultValue:value;
    }
}
