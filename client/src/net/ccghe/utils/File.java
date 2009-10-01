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
