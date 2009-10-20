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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import net.ccghe.emocha.ServerService.FileTransmitter;
import net.ccghe.emocha.async.DownloadOneFile;
import net.ccghe.emocha.async.UploadOneFile;
import net.ccghe.emocha.model.Preferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Server {
	private static String user;
	private static String password;
	private static String serverURL;
	
	public static final String CMD_HELLO 				= "hello";
	public static final String CMD_ACTIVATE_PHONE 		= "activatePhone";
	public static final String CMD_GET_FILE 			= "getFile";
	public static final String CMD_GET_SDCARD_FILE_LIST	= "getSdcardFileList";
	public static final String CMD_UPLOAD_FILE			= "uploadFile";
	
	// TODO: retrigger init after changing the settings
	public static void init(Context context) {
		serverURL 	= Preferences.getServerURL(context);
		user 		= Preferences.getUser(context);
		password 	= Preferences.getPassword(context);
	}
	
	public static JSONObject Send(PostDataPairs pairs) {		
		HttpClient client = new DefaultHttpClient();
        try {
        	HttpPost post = new HttpPost(serverURL); 
        	post.setEntity(new UrlEncodedFormEntity(pairs.get()));
        	HttpResponse response = client.execute(post);
        	
			HttpEntity entity   = response.getEntity();
			InputStream stream  = entity.getContent();
			String jsonResponse = convertStreamToString(stream);			
			stream.close();
			if (entity != null) {
			    entity.consumeContent();
			}        
			JSONObject jObject = new JSONObject(jsonResponse);
			
        	return jObject;
        } catch(ClientProtocolException e) {
        	Log.e("EMOCHA", "ClientProtocolException ERR. " + e.getMessage());
        } catch(UnknownHostException e) {
        	Log.e("EMOCHA", "UnknownHostException ERR. " + e.getMessage());        	
        } catch (IOException e) {
        	Log.e("EMOCHA", "IOException ERR. " + e.getMessage());
        } catch (Exception e) {
        	Log.e("EMOCHA", "Exception ERR. " + e.getMessage());        	
        }
        return null;
	}
	public static JSONObject SendAuthenticated(PostDataPairs pairs) {
		pairs.add("usr", user);
		pairs.add("pwd", password);
		return Send(pairs);
	}
	public static JSONObject GetSdcardFileList(String lastServerUpdate) {
		PostDataPairs data = new PostDataPairs();
        data.add("last_server_upd", lastServerUpdate);	        
        data.add("cmd", CMD_GET_SDCARD_FILE_LIST);
		return SendAuthenticated(data);
	}
	public static JSONObject activatePhone(String imei) {
		PostDataPairs data = new PostDataPairs();
        data.add("imei", imei);	        
        data.add("cmd", CMD_ACTIVATE_PHONE);
        return Send(data);		
	}

	public static void UploadFile(String path, FileTransmitter transmitter) {
		MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart("usr", new StringBody(user));
			entity.addPart("pwd", new StringBody(password));
			entity.addPart("cmd", new StringBody(CMD_UPLOAD_FILE));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		new UploadOneFile(path, serverURL, transmitter, entity);					
	}
	
	public static void DownloadFile(String path, FileTransmitter transmitter) {
		new DownloadOneFile(path, serverURL, transmitter);			
	}
	
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
	public static String convertStreamToString(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                result.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return result.toString();
    } 		
}
