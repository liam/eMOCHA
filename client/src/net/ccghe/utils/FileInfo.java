package net.ccghe.utils;

public class FileInfo {
	private String mPath;
	private long mLastModified;
	private long mLength;
	
	public FileInfo(String path, long lastModified, long length) {
		this.mPath 			= path;
		this.mLastModified 	= lastModified;
		this.mLength 		= length;
	}
	
	public String path() {
		return mPath;
	}
	public long lastModified() {
		return mLastModified;
	}
	public long length() {
		return mLength;
	}}
