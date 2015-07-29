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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.troido.resigner.controll.ResignerLogic;
import de.troido.resigner.utils.PropertiesUtil;

public class MainWindow extends JPanel implements DropTargetListener {
	private BufferedImage img;
	public static PathSettingWindow pathSetting;

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
		f.getContentPane().add(this);
		f.setSize(318, 335);
		f.setResizable(false);
		f.setTitle("apk resigner");
		this.setBackground(Color.white);
		f.setLocationRelativeTo(getRootPane());
		f.setVisible(true);
		try {
			ResignerLogic.checkEnvironment();
		} catch (RuntimeException exc) {
			JOptionPane.showMessageDialog(this, exc.getMessage());
			System.exit(0);
		}
		JMenuBar menuBar = new JMenuBar();
		JMenuItem optionsItem = new JMenuItem("option");
		menuBar.add(optionsItem);
		optionsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pathSetting == null){
					pathSetting = new PathSettingWindow();
				}else{
					pathSetting.toFront();
				}
			}
		});
		f.setJMenuBar(menuBar);
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
			String result[] = ResignerLogic.resign(inFileName, outFileName);
			if (result != null) {
				JOptionPane.showMessageDialog(this,
						"apk successfully re-signed\n\nPackage name: "
								+ result[0] + "\nMain activity: " + result[1]);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "ERROR: " + e.getMessage());
		}

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}
}
