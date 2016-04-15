package de.troido.resigner.ui;

import com.apkfuns.apkresign.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by pengwei on 16/4/15.
 */
public class PathSettingRowView extends JPanel implements ActionListener {

    private JLabel title;
    private JTextField value;
    private JButton btn;

    private boolean isSelectFile = true;
    private OnSelectListener listener;

    public PathSettingRowView(String name) {
        this(name, true);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public PathSettingRowView(String name, boolean isSelectFile) {
        this.isSelectFile = isSelectFile;
        GridBagLayout layout = new GridBagLayout();
        title = new JLabel(name);
        value = new JTextField(30);
        btn = new JButton("选择");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 2;
        layout.setConstraints(title, constraints);
        constraints.weightx = 3;
        constraints.weighty = 0;
        constraints.gridwidth = 2;
        layout.setConstraints(value, constraints);
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 2;
        layout.setConstraints(btn, constraints);
        add(title);
        add(value);
        add(btn);
        btn.addActionListener(this);
    }

    public void isSelectFile(boolean flag) {
        this.isSelectFile = flag;
    }

    public void setText(String text) {
        value.setText(text);
    }

    public void setTextEnable(boolean enable) {
        value.setEnabled(enable);
    }

    public void setBtnEnable(boolean enable) {
        btn.setEnabled(enable);
    }

    public void setBtnText(String text) {
        btn.setText(text);
    }

    public void setLabel(String text) {
        title.setText(text);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (TextUtils.notEmpty(value.getText())) {
            if (listener != null) {
                listener.onSelect(PathSettingRowView.this, value.getText());
            }
        } else {
            JFileChooser jfc = new JFileChooser();
            if (!isSelectFile) {
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 设置选择文件夹
            }
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = jfc.getSelectedFile().getAbsolutePath();
                if (listener != null && TextUtils.notEmpty(path)) {
                    listener.onSelect(PathSettingRowView.this, path);
                }
            }
        }
    }

    interface OnSelectListener {
        void onSelect(PathSettingRowView view, String path);
    }
}
