// (c) 2017 uchicom
package com.uchicom.subspace;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	/**
	 * ファイルをバイトデータで取得する
	 * @param path
	 * @return
	 */
	public byte[] getBytes(String path) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//これがディレクトリであるかを判定する。
		initProperties();
		Connection connection = null;
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
				return null;//TODO errorをスローする
			}

			// scp (アクション時にこれを実施する
			SCPClient scp = connection.createSCPClient();
			String fullPath = "~/" + config.getProperty(Constants.KEY_CURRENT) + "/" + path;
			System.out.println("getBytes:" + fullPath);
			scp.get(fullPath, baos);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return baos.toByteArray();
	}
	public List<FileRecord> listFiles(String path) {

		String dirPath = "~/" + config.getProperty(Constants.KEY_CURRENT) + "/" + path;
		List<FileRecord> recordList = new ArrayList<>();
		//これがディレクトリであるかを判定する。
		initProperties();
		watch(new File(config.getProperty(Constants.KEY_LOCAL)));
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

			// scp (アクション時にこれを実施する
			SCPClient scp = connection.createSCPClient();
			scp.put((Long.toHexString(System.currentTimeMillis())).getBytes(),
					".subspace." + config.getProperty(Constants.KEY_NAME),
					dirPath);
			System.out.println(dirPath);
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

			session.execCommand("ls -laA --full-time " + dirPath);
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

	Map<WatchKey, Path> pathMap = new HashMap<>();
	/**
	 *
	 * @param baseFile
	 */
	private void watch(File localFile) {
		Thread thread = new Thread(() -> {
			WatchKey key = null;
			try {
				WatchService service = FileSystems.getDefault().newWatchService();
				regist(service, localFile);
				int length = localFile.toPath().toString().length();
				while ((key = service.take()) != null) {

					// スレッドの割り込み = 終了要求を判定する. 必要なのか不明
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException();
					}
					if (!key.isValid())
						continue;
					for (WatchEvent<?> event : key.pollEvents()) {
						//eventではファイル名しかとれない
						Path file = (Path) event.context();
						//監視対象のフォルダを取得する必要がある
						Path real = pathMap.get(key).resolve(file);

						if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
							regist(service, real.toFile());
							// 追加
							System.out.println("create:" + real.toString().substring(length));
						} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
							// 削除
							System.out.println("delete:" + real.toString().substring(length));
						} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
							// 変更
							System.out.println("modify:" + real.toString().substring(length));
						}
					}
					key.reset();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				key.cancel();
			}
		});
		thread.setDaemon(false); //mainスレッドと運命を共に
		thread.start();
	}

	/**
	 *  監視サービスにフォルダを再起呼び出しして登録する
	 * @param service
	 * @param file
	 * @throws IOException
	 */
	public void regist(WatchService service, File file) throws IOException {
		if (file.isDirectory()) {
			Path path = file.toPath();
			System.out.println(path);
			pathMap.put(
					path.register(
							service, new Kind[] { StandardWatchEventKinds.ENTRY_CREATE,
									StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE },
							new Modifier[] {}),
					path);
			for (File child : file.listFiles()) {
				regist(service, child);
			}
		}
	}
}
