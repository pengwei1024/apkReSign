package de.troido.resigner.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Created by pengwei08 on 2015/7/27.
 * 路径设置窗口
 */
public class PathSettingWindow extends JFrame implements ActionListener{

    private JTextField javaDir, androidDir, debugDir;
    private JButton button1,button2, button3;

    public PathSettingWindow() throws HeadlessException {
        setTitle("路径设置");
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
        Container container = getContentPane();
        button1 = new JButton("确认");
        button2 = new JButton("确认");
        button3 = new JButton("确认");
        javaDir = new JTextField(40);
        javaDir.setText(System.getenv("JAVA_HOME"));
        androidDir = new JTextField(40);
        androidDir.setText(System.getenv("ANDROID_HOME"));
        debugDir = new JTextField(38);
        debugDir.setText(System.getenv("ANDROID_HOME")+"\\.android\\debug.keystore");
        container.add(new JLabel("jdk路径:"));
        container.add(javaDir);
        container.add(button1);
        button1.addActionListener(this);
        container.add(new JLabel("sdk路径:"));
        container.add(androidDir);
        container.add(button2);
        button2.addActionListener(this);
        container.add(new JLabel("debug路径:"));
        container.add(debugDir);
        container.add(button3);
        button3.addActionListener(this);
        setSize(600, 400);
        setResizable(false);
        this.setBackground(Color.white);
        setLocationRelativeTo(getRootPane());
        setVisible(true);
        setAlwaysOnTop(true);
        setTitle(System.getProperty("user.dir"));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.pathSetting = null;
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getID()){

        }
    }
}
