// (c) 2018 uchicom
package com.uchicom.subspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class DropboxTest {
	private static final String ACCESS_TOKEN = "MzW_5EucyH8AAAAAAAB2cE7GZ_UP3iOqpZpyuhfUhpgqRwWf_rWkOTMR-Zvx14bR";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create Dropbox client
		DbxRequestConfig config = new DbxRequestConfig("subspace");
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

		try {
			// Get current account info
			FullAccount account = client.users().getCurrentAccount();
			System.out.println(account.getName().getDisplayName());

			// Get files and folder metadata from Dropbox root directory

			nest(client, "");

			// Upload "test.txt" to Dropbox
			File file = new File("test.txt");
			if (file.exists()) {
				try (InputStream in = new FileInputStream(file)) {
					FileMetadata metadata = client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
					System.out.println(metadata.getId());
				}
			}
		} catch (DbxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * サブフォルダの内容も出力する。
	 * @param client
	 * @param path
	 * @throws ListFolderErrorException
	 * @throws DbxException
	 */
	private static void nest(DbxClientV2 client, String path) throws ListFolderErrorException, DbxException {
		ListFolderResult result = client.files().listFolder(path);
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				System.out.println(metadata.getPathLower());
				System.out.println(metadata.getName());
				System.out.println(metadata.getPathDisplay());
//				System.out.println(metadata.getParentSharedFolderId()); //共有していなければnullかな
				if (metadata instanceof FolderMetadata) {
					FolderMetadata folderMetadata = (FolderMetadata)metadata;
					System.out.println("folder:" + folderMetadata.getName());
					nest(client, folderMetadata.getPathLower());
				} else if (metadata instanceof FileMetadata) {
					FileMetadata fileMetadata = (FileMetadata)metadata;
					System.out.println("file:" + fileMetadata.getName());
				} else if (metadata instanceof DeletedMetadata) {
					DeletedMetadata deletedMetadata = (DeletedMetadata)metadata;
					System.out.println("deleted:" + deletedMetadata.getName());
				} else {
					//
					System.out.println("unknown:" + metadata.getName());
				}
			}

			if (!result.getHasMore()) {
				break;
			}

			result = client.files().listFolderContinue(result.getCursor());
		}
	}
}
