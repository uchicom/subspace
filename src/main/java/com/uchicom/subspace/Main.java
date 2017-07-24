// (c) 2017 uchicom
package com.uchicom.subspace;

import java.io.File;

import javax.swing.JOptionPane;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	/**
	 * 画面起動か、同期サービス起動か、-serviceがある場合は
	 * 起動方法をどうする
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			//サービス起動
			Subspace subspace = new Subspace();
			subspace.start();
		} else {
			//拡張子によって起動するプログラムを分ける。
			Subspace subspace = new Subspace();
			for (String arg : args) {
				JOptionPane.showMessageDialog(null, "来てますね；" + arg);
				if (arg.endsWith(".sub")) {
					//サブスペースの場合は独自のプログラムで表示する
					String path = arg.substring(0, arg.length() - 4);
					subspace.start(path, new File(arg).isDirectory());
					//起動する仕組みは？ファイル→サブスペースを選択するとサブスペースのファイル一覧ダイアログが表示される
					//ダブルクリックで
				}
			}
			//画面起動（これは別プロジェクトscopeにしようあくまでsubspaceはファイル同期、
//			String path = "";
//			List<FileRecord> fileRecordList = subspace.listFiles(path);
//
//			for (FileRecord record : fileRecordList) {
//				if (".".equals(record.getName())) continue;
//				if ("..".equals(record.getName())) continue;
//				System.out.println(record.getName());
//			}
//			SwingUtilities.invokeLater(()-> {
//				ScopeFrame frame = new ScopeFrame(path);
//				frame.setFileRecordList(fileRecordList);
//				frame.setVisible(true);
//			});
		}
	}

}
