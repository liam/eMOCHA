package net.ccghe.emocha.async;

import java.net.URL;
import java.util.ArrayList;
/**
 * 
 * IMPORTANT!!
 * It seems that this whole thing is just slowing down the GUI. I think it has to do with 
 * while loop at line 80. I don't if a runnable is posted to a thread and that the while 
 * loops block until that runnable is done and then continues the while loop. Or that is 
 * just runs over the objects and all puts them in a separate thread created by the CachedThreadPool.
 * In the debug output is shows that three threads a created in the pool-2 
 * (which is this exec var) and that they are all running.
 *
 */
public class DownloadFileGroup implements Runnable {

    // TODO: I wonder if this is dangerous to do. When i used a newFixedThreadPool 
    // it was also working only it was creating a new thread until 10 was reached.
    // I guess this is more important when the thread is doing only small tasks.
    
    //private static ExecutorService exec = Executors.newSingleThreadExecutor(); 
	// .newCachedThreadPool();
	// .newFixedThreadPool(10);
    
    private ArrayList<DownloadOneFile> mDownloads;
    
    public DownloadFileGroup(String string){  
    }
    
    public void run() {    	
        try {
        	mDownloads = new ArrayList<DownloadOneFile>(); 
        	mDownloads.add(new DownloadOneFile(
            		"sdcard/emocha/training/lectures/twob.mp4", 
            		new URL("https://secure.ccghe.net/sdcard/emocha/training/lectures/two.mp4")
            ));
        } catch (Exception ignore) {        	
        }
        
        while(mDownloads.size() > 0){
            mDownloads.get(0).beginDownload();
            mDownloads.remove(0);
        }
    }
}



