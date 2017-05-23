// (c) 2017 uchicom
package com.uchicom.subspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Subspace {

	private Properties config = new Properties();

	public void execute() {
		initProperties();
		Connection connection = null;
		Session session = null;
		long start = System.currentTimeMillis();
		try {
			// 接続処理
			connection = new Connection(config.getProperty(Constants.KEY_HOST), Integer.parseInt(config.getProperty(Constants.KEY_PORT)));
			connection.connect();

			// 認証
			boolean isAuthenticated = false;
			if (Boolean.valueOf(config.getProperty(Constants.KEY_PUBLIC))) {
				isAuthenticated = connection.authenticateWithPublicKey(config.getProperty(Constants.KEY_USER),
					new File(config.getProperty(Constants.KEY_FILE)),
					config.getProperty(Constants.KEY_PASSWORD));
			} else {
				isAuthenticated = connection.authenticateWithPassword(config.getProperty(Constants.KEY_USER),
						config.getProperty(Constants.KEY_PASSWORD));
			}
			if (!isAuthenticated) {
				return;
			}

			// コマンド実行
			session = connection.openSession();

			// scp
			SCPClient scp = connection.createSCPClient();
			scp.put("testdayo2".getBytes(), "test.txt", "~/" + config.getProperty(Constants.KEY_CURRENT));

			//カレントフォルダを取得し、ファイルやフォルダの一覧を取得する
			session.execCommand("pwd");//詳細表示、隠しファイル表示、.や..非表示 0.5[s]

			InputStream is = session.getStdout();
			int length = 0;
			byte[] bytes = new byte[1024 * 4];
			while ((length = is.read(bytes)) > 0) {
				System.out.print(length + ":" + new String(bytes, 0, length));
				System.out.print("end");
			}
			System.out.println(session.getExitStatus());

			is.close();
			session.close();

			List<FileRecord> recordList = new ArrayList<>();
			session = connection.openSession();

			session.execCommand("ls -laA --full-time");
			BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
			String line = null;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					first = false;
					continue;
				}
				FileRecord record = new FileRecord(line);
				recordList.add(record);
			}
			System.out.println(recordList);
			System.out.println(session.getExitStatus());
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
		System.out.println(((System.currentTimeMillis() - start) / 1000d) + "[s]");
	}

	public List<FileRecord> listFiles(String path) {

		List<FileRecord> recordList = new ArrayList<>();
		//これがディレクトリであるかを判定する。
		initProperties();
		Connection connection = null;
		Session session = null;
		long start = System.currentTimeMillis();
		try {
			// 接続処理
			connection = new Connection(config.getProperty(Constants.KEY_HOST), Integer.parseInt(config.getProperty(Constants.KEY_PORT)));
			connection.connect();

			// 認証
			boolean isAuthenticated = false;
			if (Boolean.valueOf(config.getProperty(Constants.KEY_PUBLIC))) {
				isAuthenticated = connection.authenticateWithPublicKey(config.getProperty(Constants.KEY_USER),
					new File(config.getProperty(Constants.KEY_FILE)),
					config.getProperty(Constants.KEY_PASSWORD));
			} else {
				isAuthenticated = connection.authenticateWithPassword(config.getProperty(Constants.KEY_USER),
					config.getProperty(Constants.KEY_PASSWORD));
			}
			if (!isAuthenticated) {
				return null;
			}

			// コマンド実行
			session = connection.openSession();

			// scp
//			SCPClient scp = connection.createSCPClient();
//			scp.put("testdayo2".getBytes(), "test.txt", "~/" + config.getProperty("current"));
			System.out.println("~/" + config.getProperty(Constants.KEY_CURRENT) + path);
			//カレントフォルダを取得し、ファイルやフォルダの一覧を取得する
//			session.execCommand("cd ~/" + config.getProperty(Constants.KEY_CURRENT) + "/" + path);//詳細表示、隠しファイル表示、.や..非表示 0.5[s]
////			session.execCommand("pwd");//詳細表示、隠しファイル表示、.や..非表示 0.5[s]
//
//			InputStream is = session.getStdout();
//			int length = 0;
//			byte[] bytes = new byte[1024 * 4];
//			while ((length = is.read(bytes)) > 0) {
//				System.out.print(length + ":" + new String(bytes, 0, length));
//				System.out.print("end");
//			}

//			is.close();
			session.close();

			session = connection.openSession();

			session.execCommand("ls -laA --full-time ~/" + config.getProperty(Constants.KEY_CURRENT) + path);
			BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
			String line = null;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					first = false;
					continue;
				}
				FileRecord record = new FileRecord(line);
				recordList.add(record);
			}
//			System.out.println(recordList);
//			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
		System.out.println(((System.currentTimeMillis() - start) / 1000d) + "[s]");
		return recordList;
	}

	private void initProperties() {
		if (Constants.configFile.exists() && Constants.configFile.isFile()) {
			try (FileInputStream fis = new FileInputStream(Constants.configFile);) {
				config.load(fis);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}


//	private void storeProperties() {
//		try {
//			if (!configFile.exists()) {
//				configFile.getParentFile().mkdirs();
//				configFile.createNewFile();
//			}
//			try (FileOutputStream fos = new FileOutputStream(configFile);) {
//				config.store(fos, "Launcher Ver1.0.0");
//			} catch (FileNotFoundException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			}
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
//	}

}
