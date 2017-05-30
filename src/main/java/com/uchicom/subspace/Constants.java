// (c) 2017 uchicom
package com.uchicom.subspace;

import java.io.File;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Constants {
	/** 設定ファイル */
	public static final File configFile = new File("conf/subspace.properties");

	/** ホスト名 */
	public static final String KEY_HOST = "host";
	/** ポート */
	public static final String KEY_PORT = "port";
	/** 公開鍵方式か否か */
	public static final String KEY_PUBLIC = "public";
	/** 公開鍵ファイル */
	public static final String KEY_FILE = "file";
	/** ユーザ */
	public static final String KEY_USER = "user";
	/** パスワードまたは公開鍵のパスフレーズ */
	public static final String KEY_PASSWORD = "password";
	/** ホスト上の相対カレントフォルダパス */
	public static final String KEY_CURRENT = "current";
	/** ローカルマシン上で同期するフォルダパス */
	public static final String KEY_LOCAL = "local";
	/** ローカルマシンを識別する名前 */
	public static final String KEY_NAME = "name";
}
