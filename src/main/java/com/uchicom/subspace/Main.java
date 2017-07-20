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
	 * 起動方法をどうする
	 * @param args
	 */
	public static void main(String[] args) {
		Subspace subspace = new Subspace();
		String path = "";
		List<FileRecord> fileRecordList = subspace.listFiles(path);

		for (FileRecord record : fileRecordList) {
			if (".".equals(record.getName())) continue;
			if ("..".equals(record.getName())) continue;
			System.out.println(record.getName());
		}
		SwingUtilities.invokeLater(()-> {
			ScopeFrame frame = new ScopeFrame(path);
			frame.setFileRecordList(fileRecordList);
			frame.setVisible(true);
		});

	}

}
