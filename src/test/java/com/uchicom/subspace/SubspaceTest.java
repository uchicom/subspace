// (c) 2017 uchicom
package com.uchicom.subspace;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SubspaceTest {

	/**
	 * {@link com.uchicom.subspace.Subspace#getBytes(java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testGetBytes() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.uchicom.subspace.Subspace#listFiles(java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testListFiles() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.uchicom.subspace.Subspace#regist(java.nio.file.WatchService, java.io.File)} のためのテスト・メソッド。
	 */
	@Test
	public void testRegist() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.uchicom.subspace.Subspace#sync()} のためのテスト・メソッド。
	 */
	@Test
	public void testSync() {
		Subspace subspace = new Subspace();
		List<FileRecord> recordList = subspace.sync();
		assertNotNull(recordList);
		for (FileRecord record : recordList) {
			System.out.println(record);
		}
	}
}
