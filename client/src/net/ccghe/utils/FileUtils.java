/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.ccghe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.twmacinta.util.MD5;

/**
 * Static methods used for common file operations.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 * 
 */
public class FileUtils {
    private final static String t = "FileUtils";


    public static ArrayList<String> getFoldersAsArrayList(String path) {
        ArrayList<String> mFolderList = new ArrayList<String>();
        File root = new File(path);

        if (!storageReady()) {
            return null;
        }
        if (!root.exists()) {
            if (!createFolder(path)) {
                return null;
            }
        }
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (File child : children) {
                boolean directory = child.isDirectory();
                //String filename = child.getName();
                if (directory) {
                    mFolderList.add(child.getAbsolutePath());
                }
            }
        }
        return mFolderList;
    }


    public static ArrayList<String> getFilesAsArrayList(String path) {
        ArrayList<String> mFileList = new ArrayList<String>();
        File root = new File(path);

        if (!storageReady()) {
            return null;
        }
        if (!root.exists()) {
            if (!createFolder(path)) {
                return null;
            }
        }
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (File child : children) {
                String filename = child.getName();
                // no hidden files
                if (!filename.startsWith(".")) {
                    mFileList.add(child.getAbsolutePath());
                }
            }
        } else {
            String filename = root.getName();
            // no hidden files
            if (!filename.startsWith(".")) {
                mFileList.add(root.getAbsolutePath());
            }
        }
        return mFileList;
    }


    public static ArrayList<FileInfo> getFilesAsArrayListRecursive(String path) {
        ArrayList<FileInfo> mFileList = new ArrayList<FileInfo>();
        File root = new File(path);
        getFilesAsArrayListRecursiveHelper(root, mFileList);
        return mFileList;
    }

    private static void getFilesAsArrayListRecursiveHelper(File f, ArrayList<FileInfo> filelist) {
        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            for (File child : childs) {
                getFilesAsArrayListRecursiveHelper(child, filelist);
            }
            return;
        }
        filelist.add(new FileInfo(f.getAbsolutePath(), f.lastModified(), f.length(), ""));
    }

    public static boolean deleteFolder(String path) {
        // not recursive
        if (path != null && storageReady()) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (!file.delete()) {
                        Log.i(t, "Failed to delete " + file);
                    }
                }
            }
            return dir.delete();
        } else {
            return false;
        }
    }


    public static boolean createFolder(String path) {
        if (storageReady()) {
            boolean made = true;
            File dir = new File(path);
            if (!dir.exists()) {
                made = dir.mkdirs();
            }
            return made;
        } else {
            return false;
        }
    }


    public static boolean deleteFile(String path) {
        if (storageReady()) {
            File f = new File(path);
            return f.delete();
        } else {
            return false;
        }
    }


    public static byte[] getFileAsBytes(File file) {

        byte[] bytes = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                Log.e(t, "File " + file.getName() + "is too large");
                return null;
            }

            // Create the byte array to hold the data
            bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int read = 0;
            try {
                while (offset < bytes.length && read >= 0) {
                    read = is.read(bytes, offset, bytes.length - offset);
                    offset += read;
                }
            } catch (IOException e) {
                Log.e(t, "Cannot read " + file.getName());
                e.printStackTrace();
                return null;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                try {
                    throw new IOException("Could not completely read file " + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return bytes;

        } catch (FileNotFoundException e) {
            Log.e(t, "Cannot find " + file.getName());
            e.printStackTrace();
            return null;

        } finally {
            // Close the input stream
            try {
                is.close();
            } catch (IOException e) {
                Log.e(t, "Cannot close input stream for " + file.getName());
                e.printStackTrace();
                return null;
            }
        }



    }


    private static boolean storageReady() {
        String cardstatus = Environment.getExternalStorageState();
        if (cardstatus.equals(Environment.MEDIA_REMOVED)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
                || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getMd5Hash(String filename) {
    	return getMd5Hash(new File(filename));
    }

    public static String getMd5Hash(File file) {
    	try {
			return MD5.asHex(MD5.getHash(file));
		} catch (IOException e) {
            Log.e("MD5", e.getMessage());
            return null;
		}
    }



}
