package de.troido.resigner.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.apkfuns.apkresign.PathUtil;
import de.troido.resigner.utils.PropertiesUtil;

/**
 * Created by pengwei08 on 2015/7/27.
 * 路径设置窗口
 */
public class PathSettingWindow extends BaseJFrame implements PathSettingRowView.OnSelectListener {

    private PathSettingRowView jdkPath, sdkPath, keyStore;

    public PathSettingWindow() throws HeadlessException {
        setTitle("路径设置");
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 30));
        initView();
        setSize(600, 400);
        setResizable(false);
        this.setBackground(Color.white);
        setLocationRelativeTo(getRootPane());
        setAlwaysOnTop(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.pathSetting = null;
            }
        });
    }

    private void initView() {
        jdkPath = new PathSettingRowView("jdk路径:", false);
        sdkPath = new PathSettingRowView("android sdk路径:", false);
        keyStore = new PathSettingRowView("debug.keystore路径:");
        jdkPath.setOnSelectListener(this);
        sdkPath.setOnSelectListener(this);
        keyStore.setOnSelectListener(this);
        jdkPath.setText(PathUtil.getJdkPath());
        sdkPath.setText(PathUtil.getSdkPath());
        keyStore.setText(PathUtil.getKeyStore());
        add(jdkPath);
        add(sdkPath);
        add(keyStore);
    }

    @Override
    public void onSelect(PathSettingRowView view, String path) {
        if (view.equals(jdkPath)) {
            if (PathUtil.isJdkFile(path)) {
                jdkPath.setText(path);
                PropertiesUtil.put("jdk.dir", path);
                alert("jdk路径设置成功");
            } else {
                alert("您选择的不是有效的JDK路径");
            }
        } else if (view.equals(sdkPath)) {
            if (PathUtil.isSdkFile(path)) {
                sdkPath.setText(path);
                PropertiesUtil.put("sdk.dir", path);
                String ks = path + "/.android/debug.keystore";
                if (PathUtil.isKeyStore(ks)) {
                    keyStore.setText(path);
                    PropertiesUtil.put("debug.keystore", path);
                }
                alert("android sdk路径设置成功");
            } else {
                alert("您选择的不是有效的android sdk路径");
            }
        } else if (view.equals(keyStore)) {
            if (PathUtil.isKeyStore(path)) {
                keyStore.setText(path);
                PropertiesUtil.put("debug.keystore", path);
                alert("debug.keystore路径设置成功");
            } else {
                alert("您选择的不是debug.keystore路径");
            }
        }
    }
}
