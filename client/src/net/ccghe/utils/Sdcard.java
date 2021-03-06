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
import java.util.ArrayList;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.model.DBAdapter;
import android.database.Cursor;
import android.util.Log;

public class Sdcard {
	public static void deleteUnwantedFiles(Cursor filesToKeepCursor) {
		ArrayList<String> filesToKeep = DBAdapter.getFilePaths();		
		ArrayList<FileInfo> sdcardFiles = FileUtils.getFilesAsArrayListRecursive(Constants.PATH_BASE);	

		for(FileInfo sdcardFile : sdcardFiles) {
			String fileToDelete = sdcardFile.path();
			if (!filesToKeep.contains(fileToDelete)) {
				Log.i(Constants.LOG_TAG, "Sdcard: delete " + fileToDelete);
				new File(fileToDelete).delete();
			}	
		}
		filesToKeepCursor.close();
	}
	
	
	
	// UNUSED, MAYBE FOR THE FUTURE
	public static void syncSD2DB() {
		FileInfo	dbFile;
		String 		md5;
		String		path;

		DBAdapter.markForDeletion(true);

		ArrayList<FileInfo> sdcardFiles = FileUtils.getFilesAsArrayListRecursive("/sdcard/emocha/");	

		// go through all files in sdcard
		for(FileInfo sdcardFile : sdcardFiles) {
			path = sdcardFile.path();
			dbFile = DBAdapter.getFile(path);
			if (dbFile.isInDB()) {
				if (sdcardFile.length() == dbFile.length() && sdcardFile.lastModified() == dbFile.lastModified()) {
					DBAdapter.markForDeletion(path, false);
				} else {
					md5 = FileUtils.getMd5Hash(path);
					DBAdapter.updateFile(path, sdcardFile.lastModified(), sdcardFile.length(), md5, false, false);										
				}
			} else {				
				md5 = FileUtils.getMd5Hash(path);
				DBAdapter.insertFile(path, sdcardFile.lastModified(), sdcardFile.length(), md5, false, false);
			}
		}
		
		DBAdapter.deleteMarked();		

	}
}
