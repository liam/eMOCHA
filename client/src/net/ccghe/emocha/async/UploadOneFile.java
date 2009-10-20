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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.ServerService.FileTransmitter;
import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.utils.Server;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.util.Log;

// based on org.odk.collect.android.tasks.InstanceUploaderTask

public class UploadOneFile {
	public UploadOneFile(String path, String serverURL, FileTransmitter transmitter, MultipartEntity postData) {
		// configure connection
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20 * Constants.ONE_SECOND);
		HttpConnectionParams.setSoTimeout(params, 20 * Constants.ONE_SECOND);
		HttpClientParams.setRedirecting(params, false);

		// setup client
		DefaultHttpClient client = new DefaultHttpClient(params);
		
		HttpPost post = new HttpPost(serverURL);

		int id = 0;
		postData.addPart("file" + id, new FileBody(new File(path)));
		try {
			postData.addPart("path" + id, new StringBody(path));
		} catch (UnsupportedEncodingException e1) {
			Log.e(Constants.LOG_TAG, "Encoding error while uploading file.");
		}			

		// prepare response and return uploaded
		try {
			post.setEntity(postData);
			HttpResponse response = client.execute(post);

			HttpEntity entity   = response.getEntity();
			InputStream stream  = entity.getContent();
			String jsonResponse = Server.convertStreamToString(stream);			
			stream.close();			
			if (postData != null) {
			    postData.consumeContent();
			}        
			JSONObject jObject = new JSONObject(jsonResponse);

			Long ok = jObject.getLong("ok");
			
			if (ok > 0) {
				DBAdapter.markAsUploaded(path);
				Log.i(Constants.LOG_TAG, "Mark as uploaded: " + path);
			} else {
				Log.e(Constants.LOG_TAG, "Error uploading: " + path + " (json response not ok)");				
			}
		} catch (ClientProtocolException e) {
        	Log.e("EMOCHA", "ClientProtocolException ERR. " + e.getMessage());
		} catch (IOException e) {
        	Log.e("EMOCHA", "IOException ERR. " + e.getMessage());
        } catch (Exception e) {
        	Log.e("EMOCHA", "Exception ERR. " + e.getMessage());        	
        }

		/*
		// check response.
		// TODO: This isn't handled correctly.
		String serverLocation = null;
		Header[] h = response.getHeaders("Location");
		if (h != null && h.length > 0) {
			serverLocation = h[0].getValue();
		} else {
			// something should be done here...
			Log.e(Constants.LOG_TAG, "Location header was absent");
		}
		int responseCode = response.getStatusLine().getStatusCode();
		Log.e(Constants.LOG_TAG, "Response code:" + responseCode);

		// verify that your response came from a known server
		if (serverLocation != null && serverURL.contains(serverLocation)
				&& responseCode == 201) {
			DBAdapter.markAsUploaded(path);
		}
		*/
		transmitter.transmitComplete();
	}
}
