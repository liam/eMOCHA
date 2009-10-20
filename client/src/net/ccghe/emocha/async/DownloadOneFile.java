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
package net.ccghe.emocha.async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.ServerService.FileTransmitter;
import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.utils.FileInfo;
import net.ccghe.utils.FileUtils;
import android.util.Log;

public class DownloadOneFile {
	/**
	 * Constructor
	 * 
	 * @param path
	 * 				id of the requested file as stored in the database.
	 * @param serverURL
	 *           	The server API url
	 * @param transmitter
	 * 				Used for notifying when the download is complete
	 */
	public DownloadOneFile(String path, String serverURL, FileTransmitter transmitter) {
		String serverRootURL;
		try {
			serverRootURL = FileUtils.baseURL(new URL(serverURL));
		} catch (MalformedURLException e) {
			Log.e(Constants.LOG_TAG, "MalformedURLException: " + e);
			transmitter.transmitComplete();
			return;
		}

		HttpURLConnection con = null;
		byte[] buffer = new byte[1024];
		int len = 0;
		String folder = FileUtils.getFolder(path);
		String fileName = FileUtils.getFilename(path);
		
		FileUtils.createFolder(folder);

		try {
			URL fileURL = new URL(serverRootURL + path);

			con = (HttpURLConnection) fileURL.openConnection();
			con.setReadTimeout(20 * Constants.ONE_SECOND);
			con.setConnectTimeout(10 * Constants.ONE_SECOND);
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.connect();

			File newFile = new File(folder, fileName); 
			FileOutputStream f = new FileOutputStream(newFile);
			InputStream in = con.getInputStream();

			// TODO: what if connection closes in the middle? maybe we get a -1...
			while ((len = in.read(buffer)) != -1) {
				f.write(buffer, 0, len);
			}
			f.close();
					
			DBAdapter.markAsDownloaded(new FileInfo(newFile));
			
			Log.i(Constants.LOG_TAG, "END DOWNLOAD: " + newFile.getPath());
		} catch (SocketTimeoutException e) {
			Log.e(Constants.LOG_TAG, "Socket Timeout Exception", e);
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "IOException", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
			transmitter.transmitComplete();
		}
	}
}
