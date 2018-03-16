package com.uchicom.subspace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.uchicom.subspace.service.OneDriveService;

public class OneDriveTest {

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
		Properties config = initProperties();
		OneDriveService service = new OneDriveService(config);
		try {
			if (!config.containsKey("odv.code")) {
				service.getCode(config.getProperty("odv.client_id"), config.getProperty("odv.scope"), config.getProperty("odv.redirect_uri"));
			} else if (!config.containsKey("odv.reflesh_token")) {
				service.getToken(config.getProperty("odv.client_id"), config.getProperty("odv.redirect_uri"), config.getProperty("odv.code"));

				config.store(new FileOutputStream(Constants.configFile), "getToken");
			} else if (!config.containsKey("odv.access_token")) {
				service.refleshToken(config.getProperty("odv.client_id"), config.getProperty("odv.redirect_uri"), config.getProperty("odv.reflesh_token"));
				config.store(new FileOutputStream(Constants.configFile), "refleshToken");
			} else {
//				service.list(config.getProperty("odv.access_token"));//token 1時間ぐらいしかもｔなさそう。
				service.child(config.getProperty("odv.access_token"), config.getProperty("odv.document.id"));//試しにDocument
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
