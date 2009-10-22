package net.ccghe.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogUtil {
    
    private static String LOCATION = "/sdcard/log/";
    
    public static void appendToLog(String file , String logText) throws IOException{
        
        FileUtils.createFolder( LOCATION );
        
        try {
            FileWriter fstream = new FileWriter(LOCATION+file, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("\n"+generateTimestamp() + " | " + logText);
            out.close();
        } catch (Exception e) {
               throw new IOException("error writing to log ["+file+"] reason: " + e.getMessage());
        }
    }
    
    private static String generateTimestamp() {
        Date d = new Date( new Date().getTime() );
        return new SimpleDateFormat("EEE, MMM dd ':' HH:mm:ss").format(d);
    }
}
