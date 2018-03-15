// (c) 2018 uchicom
package com.uchicom.subspace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.uchicom.subspace.service.GoogleDriveService;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class GoogleDriveTest {

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
		// Build a new authorized API client service.
		try {
			Properties config = initProperties();
			GoogleDriveService service = new GoogleDriveService(config.getProperty("gdv.key"), config.getProperty("gdv.app"));
			service.list();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
