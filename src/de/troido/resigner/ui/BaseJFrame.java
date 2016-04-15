package de.troido.resigner.ui;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Created by pengwei08 on 2015/7/30.
 */
public class BaseJFrame extends JFrame {

    public BaseJFrame() throws HeadlessException {
        setVisible(true);
        // 设置窗体居中
        setLocationRelativeTo(null);
    }

    /**
     * 弹出提示框
     * @param msg
     */
    protected void alert(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }
}
