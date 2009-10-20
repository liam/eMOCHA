/*******************************************************************************
 * eMOCHA - electronic Mobile Open-Source Comprehensive Health Application
 * Copyright (c) 2009 Abe Pazos - abe@ccghe.net
 * 
 * This file is part of eMOCHA.
 * 
 * eMOCHA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * eMOCHA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.ccghe.utils;

import java.io.File;

import net.ccghe.emocha.model.DBAdapter;

import android.database.Cursor;

public class FileInfo {
	private String mPath;
	private long mLastModified;
	private long mLength;
	private String mMD5;
	private Boolean mValid = true;
	
	public FileInfo(File file) {
		mPath			= file.getPath();
		mLastModified	= file.lastModified();
		mLength			= file.length();
		mMD5			= FileUtils.getMd5Hash(file);
	}
	
	public FileInfo(String path, long lastModified, long length, String md5) {
		mPath 			= path;
		mLastModified 	= lastModified;
		mLength 		= length;
		mMD5			= md5;
	}
	
	public FileInfo(Cursor c) {
		if (c.getCount() > 0) {
			mPath 			= c.getString(DBAdapter.DL_COL_PATH);
			mLastModified 	= c.getLong(DBAdapter.DL_COL_TS);
			mLength			= c.getLong(DBAdapter.DL_COL_SIZE);
			mMD5			= c.getString(DBAdapter.DL_COL_MD5);
		} else {
			mValid			= false;
		}
	}
	
	public boolean isInDB() {
		return mValid;
	}
	public String path() {
		return mPath;
	}
	public long lastModified() {
		return mLastModified;
	}
	public long length() {
		return mLength;
	}
	public String md5() {
		return mMD5;
	}
}
