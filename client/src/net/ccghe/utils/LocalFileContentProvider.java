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

import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

//webview.loadUrl(“content://com.yourauthority/sdcard/foo/bar/anything.jpeg”):
//webview.loadUrl(LocalFileContentProvider.makeUri(“/sdcard/foo/bar/anything.jpeg”)):

public class LocalFileContentProvider extends ContentProvider {
   private static final String URI_PREFIX = "content://net.ccghe";

   public static String makeUri(String url) {
       Uri uri = Uri.parse(url);
       return uri.isAbsolute() ? url : URI_PREFIX + url;
   }

   @Override
   public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
       java.io.File file = new java.io.File(uri.getPath());
       ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
       return parcel;
   }   
   
   @Override
   public boolean onCreate() {
       return true;
   }

   @Override
   public int delete(Uri uri, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public String getType(Uri uri) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public Uri insert(Uri uri, ContentValues contentvalues) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

}
