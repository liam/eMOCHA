package net.ccghe.emocha.async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.ccghe.emocha.Constants;
import net.ccghe.utils.FileUtils;
import android.util.Log;

public class DownloadOneFile {    
    private final static String  LOG_TAG = "EMOCHA";
    
    private URL mUrl;
    private String mDataBaseId;
    
    /**
     *  Constructor
     * @param dataBaseId    This is the id of the requested file as stored in the database.
     * @param url           The actuall URL that is pointing to the requested file to download.
     */
    public DownloadOneFile(String dataBaseId , URL url ) {
        mUrl = url;
        mDataBaseId = dataBaseId;
    }
    public Boolean beginDownload() {
        HttpURLConnection con = null;

        try {

           con = (HttpURLConnection) mUrl.openConnection();
           con.setReadTimeout(10 * Constants.ONE_SECOND);
           con.setConnectTimeout(2 * Constants.ONE_SECOND);
           con.setRequestMethod("GET");
           con.setDoOutput(true);
           con.connect();

           String folder 	= FileUtils.getFolder(mDataBaseId);
           String fileName	= FileUtils.getFilename(mDataBaseId);

           FileOutputStream f = new FileOutputStream(new File(folder, fileName));
           InputStream in = con.getInputStream();

           byte[] buffer = new byte[1024];
           int len = 0;
           while ( (len = in.read(buffer)) != -1 ) {
        	   f.write(buffer, 0, len);
           }
           f.close();

           Log.i(LOG_TAG , "FILE [ " + fileName + " ] HAS BEEN SAVED under [ " + folder +" ] ");
        } catch (IOException e) {
           Log.e(LOG_TAG, "IOException", e);
        }  finally {
           if (con != null) {
              con.disconnect();
           }
        }

       return true; 
    }

};
