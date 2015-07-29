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

import de.troido.resigner.utils.PropertiesUtil;

/**
 * Created by pengwei08 on 2015/7/27.
 * 路径设置窗口
 */
public class PathSettingWindow extends BaseJFrame implements ActionListener{

    private JTextField javaDir, androidDir, debugDir;
    private JButton button1,button2, button3;

    public PathSettingWindow() throws HeadlessException {
        setTitle("路径设置");
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
        Container container = getContentPane();
        button1 = new JButton("选择");
        button2 = new JButton("选择");
        button3 = new JButton("选择");
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        javaDir = new JTextField(40);
        javaDir.setText(System.getenv("JAVA_HOME"));
        androidDir = new JTextField(40);
        androidDir.setText(System.getenv("ANDROID_HOME"));
        debugDir = new JTextField(38);

        String userDir = System.getProperty("user.home");
        String userDebugKeyStore = userDir + "/.android/debug.keystore";
        String debugKeyStore = System.getenv("ANDROID_HOME")+"/.android/debug.keystore";
        if(new File(debugKeyStore).exists()){

        }else if(new File(userDebugKeyStore).exists()){
            debugKeyStore = userDebugKeyStore;
        }else if(new File(PropertiesUtil.get("debug.keystore")).exists()){
            debugKeyStore = PropertiesUtil.get("debug.keystore");
        }else{
            debugKeyStore = "";
        }
        debugDir.setText(debugKeyStore);
        PropertiesUtil.put("debug.keystore", debugKeyStore);
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
        JFileChooser jfc = new JFileChooser();
        if(e.getSource() == button1 || e.getSource() == button2){
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 设置选择文件夹
        }
        if(jfc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION ){
            String path = jfc.getSelectedFile().getAbsolutePath();
            if(e.getSource() == button1){
                if(new File(path+"/bin/javac.exe").exists()){
                    PropertiesUtil.put("jdk.dir", path);
                    javaDir.setText(path);
                    alert("jdk路径设置成功");
                }else{
                    alert("jdk路径错误");
                }
            }else if(e.getSource() == button2){
                if(new File(path+"/platform-tools").exists()){
                    PropertiesUtil.put("sdk.dir", path);
                    androidDir.setText(path);
                    String user = System.getProperty("user.home")+"/.android/debug.keystore";
                    String system = path + "/.android/debug.keystore";
                    if(new File(user).exists()){
                        PropertiesUtil.put("debug.keystore", user);
                        debugDir.setText(user);
                    }else if(new File(system).exists()){
                        PropertiesUtil.put("debug.keystore", system);
                        debugDir.setText(system);
                    }else{
                        PropertiesUtil.put("debug.keystore", "");
                        debugDir.setText("");
                    }
                    alert("sdk路径设置成功"+System.getProperty("user.home"));
                }else{
                    alert("sdk路径错误");
                }
            }else if(e.getSource() == button3){
                if(new File(path).getName().equals("debug.keystore")){
                    PropertiesUtil.put("debug.keystore", path);
                    debugDir.setText(path);
                    alert("debug.keystore路径设置成功");
                }else{
                    alert("debug.keystore路径错误");
                }
            }
        }
    }
}
