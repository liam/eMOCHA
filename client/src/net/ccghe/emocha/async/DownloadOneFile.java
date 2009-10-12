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
import net.ccghe.emocha.ServerService.Downloader;
import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.utils.FileInfo;
import net.ccghe.utils.FileUtils;
import android.util.Log;

public class DownloadOneFile {
	private String mServerRootURL;
	private String mPath;
	private Downloader mDownloader;

	/**
	 * Constructor
	 * 
	 * @param path
	 *            id of the requested file as stored in the
	 *            database.
	 * @param serverURL
	 *            The server API url
	 * @param downloader 
	 * 				Used for notifying when the download is complete
	 */
	public DownloadOneFile(String path, String serverURL, Downloader downloader) {
		try {
			mServerRootURL = FileUtils.baseURL(new URL(serverURL));
		} catch (MalformedURLException e) {
			Log.e(Constants.LOG_TAG, "MalformedURLException: " + e);
			destroy();
			return;
		}
		mPath = path;
		mDownloader = downloader;

		HttpURLConnection con = null;
		byte[] buffer = new byte[1024];
		int len = 0;
		String folder = FileUtils.getFolder(mPath);
		String fileName = FileUtils.getFilename(mPath);
		
		FileUtils.createFolder(folder);

		try {
			URL fileURL = new URL(mServerRootURL + mPath);

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
			destroy();
		}
	}
	private void destroy() {
		mDownloader.downloadComplete();
		mDownloader = null;		
	}
}
