/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.troido.resigner.ui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.apkfuns.apkresign.BrowserUtil;
import com.apkfuns.apkresign.Global;
import de.troido.resigner.controll.ReSignerLogic;

public class MainWindow extends JPanel implements DropTargetListener, ActionListener {
    private BufferedImage img;
    public static PathSettingWindow pathSetting;
    public JLabel progressLabel;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, 0, 0, this);
    }

    public MainWindow() throws Exception {
        try {
            img = ImageIO.read(new File("res/resigner.png"));
        } catch (Exception e) {
            img = ImageIO.read(getClass().getResourceAsStream(
                    "/res/resigner.png"));
        }
        JFrame f = new JFrame();
        DropTarget dt = new DropTarget(f, this);
        this.setDropTarget(dt);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressLabel = new JLabel("正在重签名中,请稍等...");
        progressLabel.setFont(new Font("Dialog", 1, 16));
        setProgressLabel(false);
        add(progressLabel);
        f.getContentPane().add(this);
        f.setSize(318, 335);
        f.setResizable(false);
        f.setTitle("apk resigner");
        this.setBackground(Color.white);
        f.setLocationRelativeTo(getRootPane());
        JMenuBar menuBar = new JMenuBar();
        menuBar.setVisible(true);
        JMenuItem optionsItem = new JMenuItem("option");
        menuBar.add(optionsItem);
        JMenuItem optionsItem1 = new JMenuItem("about");
        menuBar.add(optionsItem1);
        menuBar.add(new JMenuItem("V" + Global.VERSION));
        optionsItem1.addActionListener(this);
        optionsItem.addActionListener(this);
        f.setJMenuBar(menuBar);
        f.setVisible(true);
        checkEnvironment();
    }

    /**
     * 设置进度提示是否显示
     *
     * @param show
     */
    public void setProgressLabel(boolean show) {
        progressLabel.setVisible(show);
    }

    private boolean checkEnvironment() {
        try {
            ReSignerLogic.checkEnvironment();
            return true;
        } catch (RuntimeException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage());
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new MainWindow();
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        try {
            java.util.List files = (java.util.List) dtde.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);
            if (files.size() != 1) {
                dtde.rejectDrag();
                return;
            }
            if (((File) files.get(0)).getName().endsWith(".apk")) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
            } else {
                dtde.rejectDrag();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        try {
            if (!checkEnvironment()) {
                return;
            }
            setProgressLabel(true);
            String inFileName = "";
            try {
                java.util.List files = (java.util.List) dtde.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                File inFile = ((File) files.get(0));
                inFileName = inFile.getAbsolutePath();
            } catch (UnsupportedFlavorException exc) {
                inFileName = (String) dtde.getTransferable().getTransferData(
                        DataFlavor.stringFlavor);
                System.out.println("re-signing: " + inFileName);
            }
            File outFile = new File(inFileName.replaceAll(".apk", "_debug.apk"));
            // Create a file chooser
            final JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(outFile);
            // In response to a button click:
            int returnVal = fc.showSaveDialog(this);
            if (returnVal != JFileChooser.APPROVE_OPTION)
                return;
            outFile = fc.getSelectedFile();
            String outFileName = outFile.getAbsolutePath();
            if (!outFileName.endsWith(".apk"))
                outFileName = outFileName + ".apk";
            String result[] = ReSignerLogic.resign(inFileName, outFileName);
            if (result != null) {
                JOptionPane.showMessageDialog(this,
                        "apk successfully re-signed\n\nPackage name: "
                                + result[0] + "\nMain activity: " + result[1]);
//                new ShowCodeWindow(result[0], result[1]);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ERROR: " + e.getMessage());
        } finally {
            setProgressLabel(false);
        }

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("option")) {
            if (pathSetting == null) {
                pathSetting = new PathSettingWindow();
            } else {
                pathSetting.toFront();
            }
        } else if (e.getActionCommand().equals("about")) {
            BrowserUtil.open(Global.ABOUT_UEL);
        }
    }
}
