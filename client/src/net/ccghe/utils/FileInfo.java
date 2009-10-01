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
