package com.apkfuns.apkresign;

import de.troido.resigner.utils.PropertiesUtil;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by pengwei on 16/4/15.
 */
public class PathUtil {

    /**
     * 是否为jdk目录
     *
     * @param path
     * @return
     */
    public static boolean isJdkFile(String path) {
        if (DeviceUtil.isWindowsOS()) {
            if (new File(path + "/bin/javac.exe").exists()) {
                return true;
            }
        } else {
            if (new File(path + "/Contents/Home/bin/java").exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取jdk目录
     *
     * @return
     */
    public static String getJdkPath() {
        String path = PropertiesUtil.get("jdk.dir", "");
        if (isJdkFile(path)) {
            return path;
        }
        if (DeviceUtil.isWindowsOS()) {
            path = System.getenv("JAVA_HOME");
            if (TextUtils.notEmpty(path) && isJdkFile(path)) {
                PropertiesUtil.put("jdk.dir", path);
                return path;
            }
        } else {
            File jdkFile = new File("/Library/Java/JavaVirtualMachines");
            if (jdkFile.exists()) {
                String[] files = jdkFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return !name.endsWith("DS_Store");
                    }
                });
                if (files != null && files.length > 0) {
                    path = jdkFile.getAbsolutePath() + "/" + files[0];
                    if (isJdkFile(path)) {
                        PropertiesUtil.put("jdk.dir", path);
                        return path;
                    }
                }
            }
        }
        return "";
    }

    /**
     * 是否为sdk路径
     *
     * @param path
     * @return
     */
    public static boolean isSdkFile(String path) {
        if (isExist(path + "/tools") && isExist(path + "/platform-tools")) {
            return true;
        }
        return false;
    }

    /**
     * 获取sdk目录
     *
     * @return
     */
    public static String getSdkPath() {
        String path = PropertiesUtil.get("sdk.dir", "");
        if (isSdkFile(path)) {
            return path;
        }
        if (DeviceUtil.isWindowsOS()) {
            path = System.getenv("ANDROID_HOME");
            if (TextUtils.notEmpty(path) && isSdkFile(path)) {
                PropertiesUtil.put("sdk.dir", path);
                return path;
            }
        } else {
            String userHome = System.getProperty("user.home");
            if (TextUtils.notEmpty(userHome)) {
                path = userHome + "/Library/Android/sdk";
                if (TextUtils.notEmpty(path) && isSdkFile(path)) {
                    PropertiesUtil.put("sdk.dir", path);
                    return path;
                }
            }
        }
        return "";
    }

    /**
     * 是否为keystore文件
     *
     * @param path
     * @return
     */
    public static boolean isKeyStore(String path) {
        if (!isExist(path)) {
            return false;
        }
        if (path.endsWith(".keystore") || path.endsWith(".jks")) {
            return true;
        }
        return false;
    }

    /**
     * 获取keystore地址
     *
     * @return
     */
    public static String getKeyStore() {
        String path = PropertiesUtil.get("debug.keystore", "");
        if (isKeyStore(path)) {
            return path;
        }
        String userDir = System.getProperty("user.home");
        if (TextUtils.notEmpty(userDir)) {
            path = userDir + "/.android/debug.keystore";
            if (isKeyStore(path)) {
                PropertiesUtil.put("debug.keystore", path);
                return path;
            }
        }
        String androidHome = getSdkPath();
        if (TextUtils.notEmpty(androidHome)) {
            path = androidHome + "/.android/debug.keystore";
            if (isKeyStore(path)) {
                PropertiesUtil.put("debug.keystore", path);
                return path;
            }
        }
        return "";
    }

    public static boolean isExist(String path) {
        return new File(path).exists();
    }
}
