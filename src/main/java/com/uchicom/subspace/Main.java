// (c) 2017 uchicom
package com.uchicom.subspace;

import java.util.List;

import javax.swing.SwingUtilities;

import com.uchicom.subspace.window.ScopeFrame;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Subspace subspace = new Subspace();
		List<FileRecord> fileRecordList = subspace.listFiles("/tmp");
		System.out.println("listFiles /tmp");
		if (fileRecordList == null) {
			System.out.println("ディレクトリではありません");
		} else {
			for (FileRecord record : fileRecordList) {
				if (".".equals(record.getName())) continue;
				if ("..".equals(record.getName())) continue;
				System.out.println(record.getName());
			}
			SwingUtilities.invokeLater(()-> {
				ScopeFrame frame = new ScopeFrame("/tmp");
				frame.setFileRecordList(fileRecordList);
				frame.setVisible(true);
			});
		}
	}

}
