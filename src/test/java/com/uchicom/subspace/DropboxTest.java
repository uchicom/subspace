// (c) 2018 uchicom
package com.uchicom.subspace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.dropbox.core.DbxException;
import com.uchicom.subspace.service.DropboxService;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class DropboxTest {

	protected static Properties initProperties() {
		Properties properties = new Properties();
		if (Constants.configFile.exists() && Constants.configFile.isFile()) {
			try (FileInputStream fis = new FileInputStream(Constants.configFile);) {
				properties.load(fis);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return properties;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Create Dropbox client
		
//		DbxRequestConfig config = new DbxRequestConfig("subspace");
//		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

		try {
			// Get current account info
//			FullAccount account = client.users().getCurrentAccount();
//			System.out.println(account.getName().getDisplayName());

			// Get files and folder metadata from Dropbox root directory
			
			Properties config = initProperties();
			DropboxService service = new DropboxService(config.getProperty("dbx.key"), config.getProperty("dbx.app"), config);
			service.list();
			//
//			nest(client, "");
//
//			// Upload "test.txt" to Dropbox
//			File file = new File("test.txt");
//			if (file.exists()) {
//				try (InputStream in = new FileInputStream(file)) {
//					FileMetadata metadata = client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
//					System.out.println(metadata.getId());
//				}
//			}
		} catch (DbxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
