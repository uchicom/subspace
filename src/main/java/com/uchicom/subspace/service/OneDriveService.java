package com.uchicom.subspace.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * 1時間後にrefleshTokenが実施できるかを確認する。
 * @author hex
 *
 */
public class OneDriveService {

	private Properties config;
	public OneDriveService(Properties config) {
		this.config = config;
	}
	/**
	 * WEB画面の起動後にログイン認証が必要
	 * @param clientId
	 * @param scope
	 * @param redirectUri
	 * @throws IOException
	 */
	public void getCode(String clientId, String scope, String redirectUri) throws IOException {
		StringBuffer strBuff = new StringBuffer(1024);
		strBuff.append("https://login.live.com/oauth20_authorize.srf?client_id=")
		.append(URLEncoder.encode(clientId, "utf-8"))
		.append("&scope=")
		.append(URLEncoder.encode(scope, "utf-8"))
		.append("&response_type=code")
		.append("&redirect_uri=")
		.append(URLEncoder.encode(redirectUri, "utf-8"));
		System.out.println(strBuff.toString());
		URL url = new URL(strBuff.toString());
		try (InputStream is = url.openStream()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			System.out.println(string);
		}
	}
	
	public void getToken(String clientId, String redirectUri, String code) throws IOException {
		URL url = new URL("https://login.live.com/oauth20_token.srf");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		OutputStream os = con.getOutputStream();

		StringBuffer strBuff = new StringBuffer();
		strBuff
		.append("client_id=")
		.append(URLEncoder.encode(clientId, "utf-8"))
		.append("&redirect_uri=")
		.append(URLEncoder.encode(redirectUri, "utf-8"))
		.append("&code=")
		.append(URLEncoder.encode(code, "utf-8"))
		.append("&grant_type=authorization_code");
		System.out.println(strBuff.toString());
		os.write(strBuff.toString().getBytes());
		if (200 ==con.getResponseCode()) {
			System.out.println("OK:" + con.getURL());
			InputStream is = con.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			int index = string.indexOf("\"access_token\":\"");
			String token = string.substring(index + 16, string.indexOf('"', index+16));
			config.setProperty("odv.access_token", token);
			System.out.println(token);
			index = string.indexOf("\"refresh_token\":\"");
			token = string.substring(index + 17, string.indexOf('"', index+17));
			config.setProperty("odv.reflesh_token", token);
			System.out.println(token);
			System.out.println(string);
		} else {
			System.out.println(con.getResponseCode());
			System.out.println(con.getResponseMessage());
			System.out.println("NG:" + con.getURL());
			InputStream is = con.getErrorStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			
			System.out.println(string);
		}
	}

	public void refleshToken(String clientId, String redirectUri, String refleshToken) throws IOException {
		URL url = new URL("https://login.live.com/oauth20_token.srf");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		OutputStream os = con.getOutputStream();

		StringBuffer strBuff = new StringBuffer();
		strBuff
		.append("client_id=")
		.append(URLEncoder.encode(clientId, "utf-8"))
		.append("&redirect_uri=")
		.append(URLEncoder.encode(redirectUri, "utf-8"))
		.append("&refresh_token=")
		.append(URLEncoder.encode(refleshToken, "utf-8"))
		.append("&grant_type=refresh_token");
		System.out.println(strBuff.toString());
		os.write(strBuff.toString().getBytes());
		if (200 ==con.getResponseCode()) {
			System.out.println("OK:" + con.getURL());
			InputStream is = con.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			int index = string.indexOf("\"access_token\":\"");
			String token = string.substring(index + 16, string.indexOf('"', index+16));
			config.setProperty("odv.access_token", token);
			System.out.println(token);
			index = string.indexOf("\"refresh_token\":\"");
			token = string.substring(index + 17, string.indexOf('"', index+17));
			config.setProperty("odv.reflesh_token", token);
			System.out.println(token);
			System.out.println(string);
		} else {
			System.out.println(con.getResponseCode());
			System.out.println(con.getResponseMessage());
			System.out.println("NG:" + con.getURL());
			InputStream is = con.getErrorStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			
			System.out.println(string);
		}
	}
	public void list(String token) throws IOException {
		URL url = new URL("https://graph.microsoft.com/v1.0/me/drive/root");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setDoInput(true);
		con.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, */*");
		con.setRequestProperty("Authorization", "bearer " + token);
		if (200 ==con.getResponseCode()) {
			System.out.println("OK:" + con.getURL());
			InputStream is = con.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			System.out.println(string);
		} else {
			System.out.println(con.getResponseCode());
			System.out.println(con.getResponseMessage());
			System.out.println("NG:" + con.getURL());
			InputStream is = con.getErrorStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			
			System.out.println(string);
		}
	}
	
	public void child(String token, String id) throws IOException {

		URL url = new URL("https://graph.microsoft.com/v1.0/me/drive/items/" + id + "/children");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setDoInput(true);
		con.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, */*");
		con.setRequestProperty("Authorization", "bearer " + token);
		if (200 ==con.getResponseCode()) {
			System.out.println("OK:" + con.getURL());
			InputStream is = con.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			System.out.println(string);
		} else {
			System.out.println(con.getResponseCode());
			System.out.println(con.getResponseMessage());
			System.out.println("NG:" + con.getURL());
			InputStream is = con.getErrorStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 4 * 10];
			int length = 0;
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			String string = new String(baos.toByteArray(), "utf-8");
			
			System.out.println(string);
		}
	}
}
