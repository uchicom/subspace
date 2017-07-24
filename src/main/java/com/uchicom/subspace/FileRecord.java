// (c) 2017 uchicom
package com.uchicom.subspace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class FileRecord {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S Z");
	private String parent;
	private String access;
	private int count;
	private String owner;
	private String group;
	private long size;
	private Date updated;
	private String name;
	public String[] getStrings() {
		return new String[]{
				name,
				group,
				owner,
				access,
				String.valueOf(count),
				String.valueOf(size),
				String.valueOf(updated)
		};
	}

	public FileRecord(String parent, String line) {
		this(line);
		this.parent = parent;
	}
	public FileRecord(String line) {
		String[] splits = line.split(" +", 9);
		access = splits[0];
		count = Integer.parseInt(splits[1]);
		owner = splits[2];
		group = splits[3];
		size = Integer.parseInt(splits[4]);
		try {
			updated = format.parse(splits[5] + " " + splits[6] + " " + splits[7]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		name = splits[8];
	}
	/**
	 * accessを取得します.
	 *
	 * @return access
	 */
	public String getAccess() {
		return access;
	}
	/**
	 * accessを設定します.
	 *
	 * @param access access
	 */
	public void setAccess(String access) {
		this.access = access;
	}
	/**
	 * countを取得します.
	 *
	 * @return count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * countを設定します.
	 *
	 * @param count count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * ownerを取得します.
	 *
	 * @return owner
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * ownerを設定します.
	 *
	 * @param owner owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	/**
	 * groupを取得します.
	 *
	 * @return group
	 */
	public String getGroup() {
		return group;
	}
	/**
	 * groupを設定します.
	 *
	 * @param group group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	/**
	 * sizeを取得します.
	 *
	 * @return size
	 */
	public long getSize() {
		return size;
	}
	/**
	 * sizeを設定します.
	 *
	 * @param size size
	 */
	public void setSize(long size) {
		this.size = size;
	}
	/**
	 * updatedを取得します.
	 *
	 * @return updated
	 */
	public Date getUpdated() {
		return updated;
	}
	/**
	 * updatedを設定します.
	 *
	 * @param updated updated
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	/**
	 * nameを取得します.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * nameを設定します.
	 *
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileRecord [parent=" + parent + ",access=" + access + ", count=" + count + ", owner=" + owner + ", group=" + group + ", size="
				+ size + ", updated=" + updated + ", name=" + name + "]";
	}

	/**
	 * @return
	 */
	public boolean isDirectory() {
		return access != null && access.startsWith("d");
	}
}
