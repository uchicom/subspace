// (c) 2017 uchicom
package com.uchicom.subspace.window;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
		listTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//一覧をダブルクリックすると別ウィンドウでメールを表示する。
				if (e.getClickCount() >= 2) {
					Subspace subspace = new Subspace();
					listTable.rowAtPoint(e.getPoint());
					String name = (String)model.getValueAt(listTable.rowAtPoint(e.getPoint()), 0);
					String access = (String)model.getValueAt(listTable.rowAtPoint(e.getPoint()), 3);
					if (!access.startsWith("d")) {
						String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
						switch(ext) {
						case "png":
							//テキストエディタを開く
							JOptionPane.showMessageDialog(ScopeFrame.this, "PNGファイル形式");
							try {
								BufferedImage image = ImageIO.read(new ByteArrayInputStream(subspace.getBytes(path + "/" + name)));
								ImageFrame frame = new ImageFrame(image);
								frame.setVisible(true);
							} catch (IOException e1) {
								// TODO 自動生成された catch ブロック
								e1.printStackTrace();
							}
							break;
						case "txt":
							//テキストエディタを開く
							JOptionPane.showMessageDialog(ScopeFrame.this, "テキストファイル形式");
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
						//ファイルの一覧を
						List<FileRecord> fileRecordList = subspace.listFiles(path + "/" + name);
						SwingUtilities.invokeLater(()-> {
							ScopeFrame frame = new ScopeFrame("/tmp/" + name);
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
