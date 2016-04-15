package com.apkfuns.apkresign;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by pengwei on 16/4/15.
 */
public class FileUtil {

    /**
     * 复制文件
     *
     * @param source
     * @param target
     */
    public static void copyFile(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inStream);
            close(in);
            close(outStream);
            close(out);
        }
    }

    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
