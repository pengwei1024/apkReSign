package com.apkfuns.apkresign;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by pengwei on 16/4/15.
 */
public class BrowserUtil {

    /**
     * 调用系统浏览器打开url
     *
     * @param url
     */
    public static void open(String url) {
        if (DeviceUtil.isWindowsOS()) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                URI uri = new URI(url);
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                if (desktop != null)
                    desktop.browse(uri);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
