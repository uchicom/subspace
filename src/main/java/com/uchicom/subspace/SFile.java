// (c) 2017 uchicom
package com.uchicom.subspace;

import java.io.File;
import java.net.URI;

/**
 * sub://
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SFile extends File {

	/**
	 * @param uri
	 */
	public SFile(URI uri) {
		super(uri);
	}

	@Override
	public File[] listFiles() {
		return null;
	}

}
