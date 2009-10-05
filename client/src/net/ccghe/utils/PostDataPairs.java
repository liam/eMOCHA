package net.ccghe.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class PostDataPairs {
	List<NameValuePair> data;   	

	public PostDataPairs() {
		data = new ArrayList<NameValuePair>(2);   	
	}
	public void add(String key, String val) {
		data.add(new BasicNameValuePair(key, val));
	}
	public List<NameValuePair> get() {
		return data;
	}
}
