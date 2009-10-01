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
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class PostData {
	public static String Send(List<NameValuePair> tPairs, String tURL) {
		String tResponseText = "";
		
        HttpClient tClient = new DefaultHttpClient();
        HttpPost   tPost   = new HttpPost(tURL);
                
        try {
        	tPost.setEntity(new UrlEncodedFormEntity(tPairs));
        	HttpResponse tResponse = tClient.execute(tPost);
        	
			HttpEntity tEntity   = tResponse.getEntity();
			InputStream tStream  = tEntity.getContent();
			tResponseText = convertStreamToString(tStream);
			
			tStream.close();
			if (tEntity != null) {
			    tEntity.consumeContent();
			}        			        	
        	return tResponseText;
        } catch(ClientProtocolException e) {
        	Log.e("EMOCHA", "ClientProtocolException ERR. " + e.getMessage());
        } catch(UnknownHostException e) {
        	Log.e("EMOCHA", "UnknownHostException ERR. " + e.getMessage());        	
        } catch (IOException e) {
        	Log.e("EMOCHA", "IOException ERR. " + e.getMessage());
            //e.printStackTrace(); 	
        } catch (Exception e) {
        	Log.e("EMOCHA", "Exception ERR. " + e.getMessage());        	
        }
        return null;
	}
	/*
	public void xPostData(byte[] tData, SharedPreferences tSettings) {
		     
        try {
        	tPost.setEntity(new UrlEncodedFormEntity(tPairs));

        	HttpResponse tResponse = tClient.execute(tPost);

			HttpEntity tEntity = tResponse.getEntity();
			//int tStatus = tResponse.getStatusLine().getStatusCode();
			InputStream tStream = tEntity.getContent();
			String tResponseText = convertStreamToString(tStream);
			tStream.close();
			if (tEntity != null) {
			    tEntity.consumeContent();
			}        			        	
        	Log.i("EMOCHA", "Post OK. " + tResponseText);
        } catch(ClientProtocolException e) {
        	Log.e("EMOCHA", "Post ERR. " + e.getMessage());
        } catch (IOException e) {
        	Log.e("EMOCHA", "Post ERR. " + e.getMessage());
            e.printStackTrace(); 		
        }	        
	}
*/
	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    } 	
}
