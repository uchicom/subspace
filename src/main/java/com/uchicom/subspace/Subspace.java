// (c) 2017 uchicom
package com.uchicom.subspace;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.uchicom.subspace.window.ImageFrame;
import com.uchicom.subspace.window.ScopeFrame;
import com.uchicom.subspace.window.TextFrame;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Subspace {

	private Properties config = new Properties();

	public Subspace() {
		initProperties();
	}

	/**
	 * @param jsch
	 * @throws JSchException
	 * @throws NumberFormatException
	 */
	private Session createSession(JSch jsch) throws NumberFormatException, JSchException {
		Session session = null;
		if (Boolean.valueOf(config.getProperty(Constants.KEY_PUBLIC))) {
			jsch.addIdentity(config.getProperty(Constants.KEY_FILE));
			jsch.setKnownHosts("~/.ssh/known_hosts");
			UserInfo userInfo = new UserInfo() {

				@Override
				public String getPassphrase() {
					// TODO 自動生成されたメソッド・スタブ
					return config.getProperty(Constants.KEY_PASSWORD);
				}

				@Override
				public String getPassword() {
					// TODO 自動生成されたメソッド・スタブ
					return config.getProperty(Constants.KEY_PASSWORD);
				}

				@Override
				public boolean promptPassphrase(String arg0) {
					// TODO 自動生成されたメソッド・スタブ
					return true;
				}

				@Override
				public boolean promptPassword(String arg0) {
					// TODO 自動生成されたメソッド・スタブ
					return true;
				}

				@Override
				public boolean promptYesNo(String arg0) {
					// TODO 自動生成されたメソッド・スタブ
					return true;
				}

				@Override
				public void showMessage(String arg0) {
					// TODO 自動生成されたメソッド・スタブ

				}

			};
			session = jsch.getSession(config.getProperty(Constants.KEY_USER), config.getProperty(Constants.KEY_HOST), Integer.parseInt(config.getProperty(Constants.KEY_PORT)));
//			session.setConfig("StrictHostKeyChecking", "no");

			session.setUserInfo(userInfo);
//            session.setPassword(config.getProperty(Constants.KEY_PASSWORD));

		} else {
			session = jsch.getSession(config.getProperty(Constants.KEY_USER), config.getProperty(Constants.KEY_HOST), Integer.parseInt(config.getProperty(Constants.KEY_PORT)));
			session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(config.getProperty(Constants.KEY_PASSWORD));
		}
		session.connect();

		return session;
	}
	/**
	 * ファイルをバイトデータで取得する
	 * @param path
	 * @return
	 */
	public byte[] getBytes(String path) {
		path = path.replace("$", "\\$");//エスケープ
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp channel = null;
		try {
			// 接続処理
			session = createSession(jsch);
			channel = (ChannelSftp)session.openChannel("sftp");

			String fullPath =  config.getProperty(Constants.KEY_CURRENT) + "/" + path +"";
			System.out.println("getBytes:" + fullPath);
			channel.connect();
			//channel.setFilenameEncoding("UTF-8");
			channel.get(fullPath, baos);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

		return baos.toByteArray();
	}
	public List<FileRecord> listFiles(String path) {

		String dirPath = "~/" + config.getProperty(Constants.KEY_CURRENT) + "/" + path;
		List<FileRecord> recordList = new ArrayList<>();
		long start = System.currentTimeMillis();
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp channel = null;
		ChannelExec channel2 = null;
		try {
			// 接続処理
			session = createSession(jsch);
			channel = (ChannelSftp)session.openChannel("sftp");
			channel.connect();
			channel.put(new ByteArrayInputStream((Long.toHexString(System.currentTimeMillis())).getBytes()),
					dirPath + ".subspace." + config.getProperty(Constants.KEY_NAME));
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
			channel.disconnect();
			channel2 = (ChannelExec)session.openChannel("exec");

			channel2.setCommand("ls -lA --full-time " + dirPath);
			channel2.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(channel2.getInputStream()));
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
		} catch (JSchException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
		System.out.println(((System.currentTimeMillis() - start) / 1000d) + "[s]");
		return recordList;
	}

	protected void initProperties() {
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
				List<WatchEvent<?>> events = key.pollEvents();
				System.out.println(events.size());
				for (WatchEvent<?> event : events) {
					//eventではファイル名しかとれない
					Path file = (Path) event.context();
					//監視対象のフォルダを取得する必要がある
					Path real = pathMap.get(key).resolve(file);
					System.out.println(event.count());
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
						regist(service, real.toFile());
						// 追加
						System.out.println("create:" + real.toString());
					} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
						// 削除
						System.out.println("delete:" + real.toString());
					} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
						// 変更
						System.out.println("modify:" + real.toString());
						// ファイル変更時に2回呼ばれる
					} else if (StandardWatchEventKinds.OVERFLOW.equals(event.kind())) {

						System.out.println("overflow:" + real.toString());
					}
				}
				key.reset();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (key != null) {
				key.cancel();
			}
		}
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

	/**
	 *
	 */
	public void start() {
		//サービス起動
		//同期
		sync();
		//監視
		watch(new File(config.getProperty(Constants.KEY_LOCAL)));
	}
	/**
	 * ls * でファイル階層で一気に取得する、その配下にディレクトリ2つ以上があれば同じように取得する1つの場合はフォルダを限定する/a/ * /b/*のように
	 */
	public List<FileRecord> sync() {
		String relativePath = config.getProperty(Constants.KEY_CURRENT) + "/";

		long start = System.currentTimeMillis();
		List<FileRecord> recordList = new ArrayList<>();
		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		try {
			// 接続処理
			session = createSession(jsch);
			channel = (ChannelExec)session.openChannel("exec");

			// コマンド実行

			channel.setCommand("ls -lAR --full-time " + relativePath);
			channel.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream(), config.getProperty("charset")));
			String line = null;
			boolean dir = false;
			String parent = null;
			File parentFile = null;
			boolean first = false;
			int parentLength = relativePath.length();
			File localDir = new File(config.getProperty(Constants.KEY_LOCAL));
			while ((line = br.readLine()) != null) {
				System.out.println("line" + line);
				if (dir) {
					if (first) {//余計な行を削除
						first = false;
						continue;
					}
					if ("".equals(line)) {
						dir = false;
						continue;
					}
					FileRecord record = new FileRecord(parent, line);
//						file.delete();
					if (record.isDirectory()) {
						File file = new File(parentFile, record.getName());
						if (!file.exists()) {
							file.mkdir();
						}
					} else {
						File file = new File(parentFile, record.getName() + ".sub");
						if (!file.exists()) {
							file.createNewFile();
						}
					}

					recordList.add(record);
 				} else if (line.endsWith(":")) {
 					dir = true;
 					first = true;
 					parent = line.substring(parentLength, line.length() - 1);
 					//フォルダ構成追加TODO本当は追加と削除が必要フォルダリストを作って、ローカルと比較して追加するか削除するかを同期する必要がある
 					parentFile = new File(localDir, parent);
 				}
			}



		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		System.out.println(((System.currentTimeMillis() - start) / 1000d) + "[s]");
		return recordList;
	}
	/**
	 * ファイルをオープンする仕組み
	 * @param path
	 * @param dir
	 */
	public void start(String path, boolean dir) {
		File file = new File(path);
		File local = new File(config.getProperty(Constants.KEY_LOCAL));
		System.out.println(file.toPath().toAbsolutePath());
		System.out.println(local.toPath().toAbsolutePath());
		//subspacefile subspace dir .sbf .sbdにする
		final String dirPath = file.toPath().toAbsolutePath().toString().substring(local.toPath().toAbsolutePath().toString().length() + 1).replace('\\', '/');

		if (dir) {
			//ディレクトリを開く
			List<FileRecord> fileRecordList = listFiles(dirPath);
			SwingUtilities.invokeLater(()-> {
				ScopeFrame frame = new ScopeFrame(dirPath);
				frame.setFileRecordList(fileRecordList);
				frame.setVisible(true);
			});
		} else {
			//検索して
			int lastIndex = path.lastIndexOf('.');
			System.out.println(path + ":" + lastIndex);
			String ext = path.substring(lastIndex + 1).toLowerCase();
			switch(ext) {
			case "png":
				//イメージビューアーを開く
				try {
					BufferedImage image = ImageIO.read(new ByteArrayInputStream(getBytes(dirPath)));
					ImageFrame frame = new ImageFrame(dirPath, image);
					frame.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			case "txt":
				//テキストエディタを開く
				try {
					TextFrame frame = new TextFrame(dirPath, new String(getBytes(dirPath),"utf-8"));
					frame.setVisible(true);
				} catch (UnsupportedEncodingException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				break;
			case "pdf":
				//PDFVを開く
				JOptionPane.showMessageDialog(null, "PDFファイル形式");
				break;
			case ".zip":
				//ZIPを開く
				JOptionPane.showMessageDialog(null, "ZIPファイル形式");
				break;
				default:
					JOptionPane.showMessageDialog(null, "未対応のファイル形式です");
			}

		}
	}
}
