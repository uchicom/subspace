package com.uchicom.subspace.service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class DropboxService {
	private final DbxRequestConfig requestConfig;
	private final DbxClientV2 client;

	private Properties config;
	public DropboxService(String accessToken, String clientIdentifier, Properties config) {
		requestConfig = new DbxRequestConfig(clientIdentifier);
		client = new DbxClientV2(requestConfig, accessToken);
		this.config = config;
	}

	/**
	 * @param args
	 */
	public void list() throws DbxException {

		try {
			list(client, "");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * サブフォルダの内容も出力する。
	 * 
	 * @param client
	 * @param path
	 * @throws ListFolderErrorException
	 * @throws DbxException
	 * @throws IOException 
	 */
	private void list(DbxClientV2 client, String path) throws ListFolderErrorException, DbxException, IOException {
		ListFolderResult result = client.files().listFolder(path);
		File localDir = new File(config.getProperty("dropbox"));
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				System.out.println(metadata.getPathLower());
				System.out.println(metadata.getName());
				System.out.println(metadata.getPathDisplay());
				// System.out.println(metadata.getParentSharedFolderId()); //共有していなければnullかな
				if (metadata instanceof FolderMetadata) {
					FolderMetadata folderMetadata = (FolderMetadata) metadata;
					System.out.println("folder:" + folderMetadata.getName());
					File file = new File(localDir, metadata.getPathDisplay());
					if (!file.exists()) {
						file.mkdir();
					}
					list(client, folderMetadata.getPathLower());
				} else if (metadata instanceof FileMetadata) {
					FileMetadata fileMetadata = (FileMetadata) metadata;
					System.out.println("file:" + fileMetadata.getName());
					File file = new File(localDir, metadata.getPathDisplay() + ".dbx");
					if (!file.exists()) {
						file.createNewFile();
					}
				} else if (metadata instanceof DeletedMetadata) {
					DeletedMetadata deletedMetadata = (DeletedMetadata) metadata;
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
