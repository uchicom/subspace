// (c) 2017 uchicom
package com.uchicom.subspace.window;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.uchicom.subspace.FileRecord;
import com.uchicom.subspace.Subspace;
import com.uchicom.subspace.table.ListTableModel;

/**
 * ファイルビューア
 * ディレクトリをダブルクリックで下層を表示する
 * ファイルをダブルクリックした場合は別のソフトを起動する。
 * txtの場合はsyo
 * csvの場合はcsv
 * pdfの場合はpdfv
 * pngの場合はimgv
 * emlの場合はeml
 * odsの場合は
 *
 * タイトルにはパスを表示する。
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ScopeFrame extends JFrame {

	private String path;
	private ListTableModel model;
	private JTable listTable = new JTable();
	public ScopeFrame(String path) {
		super("Scope " + path);
		this.path = path;
		initComponents();
	}

	/**
	 *
	 */
	private void initComponents() {
		//ツリーの表示する？大きくなりすぎる
		listTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		listTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//一覧をダブルクリックすると別ウィンドウでファイルを表示する。
				if (e.getClickCount() >= 2) {
					Subspace subspace = new Subspace();
					listTable.rowAtPoint(e.getPoint());
					//名前
					String name = (String)model.getValueAt(listTable.rowAtPoint(e.getPoint()), 0);
					//権限
					String access = (String)model.getValueAt(listTable.rowAtPoint(e.getPoint()), 3);
					System.out.println(path);
					System.out.println(name);
					String dirPath = null;
					//ファイルの一覧を
					if (path.length() > 0) {
						dirPath = path + "/" + name;
					} else {
						dirPath = name;
					}
					if (!access.startsWith("d")) {
						String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
						switch(ext) {
						case "png":
							//イメージビューアーを開く
							try {
								BufferedImage image = ImageIO.read(new ByteArrayInputStream(subspace.getBytes(dirPath)));
								ImageFrame frame = new ImageFrame(dirPath, image);
								frame.setVisible(true);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							break;
						case "txt":
							//テキストエディタを開く
							try {
								TextFrame frame = new TextFrame(dirPath, new String(subspace.getBytes(dirPath),"utf-8"));
								frame.setVisible(true);
							} catch (UnsupportedEncodingException e1) {
								// TODO 自動生成された catch ブロック
								e1.printStackTrace();
							}
							break;
						case "pdf":
							//PDFVを開く
							JOptionPane.showMessageDialog(ScopeFrame.this, "PDFファイル形式");
							break;
						case ".zip":
							//ZIPを開く
							JOptionPane.showMessageDialog(ScopeFrame.this, "ZIPファイル形式");
							break;
							default:
								JOptionPane.showMessageDialog(ScopeFrame.this, "未対応のファイル形式です");
						}
					} else {
						//ディレクトリを開く
						final String finalPath = dirPath;
						List<FileRecord> fileRecordList = subspace.listFiles(dirPath);
						SwingUtilities.invokeLater(()-> {
							ScopeFrame frame = new ScopeFrame(finalPath);
							frame.setFileRecordList(fileRecordList);
							frame.setVisible(true);
						});
					}
				}
			}
		});
		getContentPane().add(new JScrollPane(listTable));
		pack();
	}

	public void setFileRecordList(List<FileRecord> fileRecordList) {
		List<String[]> cellList = new ArrayList<>(fileRecordList.size());
		for (FileRecord record : fileRecordList) {
			if (".".equals(record.getName())) continue;
			if ("..".equals(record.getName())) continue;
			cellList.add(record.getStrings());
		}
		model = new ListTableModel(cellList, 7);
		listTable.setModel(model);
	}
}
