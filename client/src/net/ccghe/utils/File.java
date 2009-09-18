package net.ccghe.utils;

import java.io.BufferedReader;
import java.io.FileReader;

public class File {
    public static String readFileAsString(String tPath) throws java.io.IOException {
    	StringBuffer tData = new StringBuffer(1000);
    	BufferedReader tReader = new BufferedReader(new FileReader(tPath));
    	char[] tBuffer = new char[1024];
    	int tChars = 0;
    	while ((tChars = tReader.read(tBuffer)) != -1){
    		tData.append(tBuffer, 0, tChars);
    	}
    	tReader.close();
    	return tData.toString();
    }

}
