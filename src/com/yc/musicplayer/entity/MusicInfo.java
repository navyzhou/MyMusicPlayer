package com.yc.musicplayer.entity;

/**
 * 音乐对象
 * @company 源辰
 * @author navy
 *
 */
public class MusicInfo {
	private String mname; // 名称
	private String size; // 大小
	private String path; // 路径
	private String author = "Navy"; // 歌手
	private int flag = 0; // 是否喜欢

	@Override
	public String toString() {
		return "MusicInfo [mname=" + mname + ", size=" + size + ", path=" + path + ", author=" + author + ", flag=" + flag + "]";
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public MusicInfo(String mname, String size, String path, String author) {
		super();
		this.mname = mname;
		this.size = size;
		this.path = path;
		this.author = author;
	}
	
	public MusicInfo(String mname, String size, String path, int flag) {
		super();
		this.mname = mname;
		this.size = size;
		this.path = path;
		this.flag = flag;
	}

	
	public MusicInfo(String mname, String size, String path) {
		super();
		this.mname = mname;
		this.size = size;
		this.path = path;
	}

	public MusicInfo() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((mname == null) ? 0 : mname.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + flag;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MusicInfo other = (MusicInfo) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (mname == null) {
			if (other.mname != null)
				return false;
		} else if (!mname.equals(other.mname))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (flag != other.flag)
			return false;
		return true;
	}
}
