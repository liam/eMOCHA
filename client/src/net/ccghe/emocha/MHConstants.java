/*******************************************************************************
 * eMocha - Easy Mobile Open-Source Comprehensive Health Application
 * Copyright (c) 2009 Abe Pazos - abe@ccghe.net
 * 
 * This file is part of eMocha.
 * 
 * eMocha is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * eMocha is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.ccghe.emocha;

public class MHConstants {
	public static final String TRAINING_TYPE  		= "trainingType";
	public static final String DOC_ID				= "docID";

	public static final String BASE_PATH 			= "/sdcard/emocha/";
	public static final String VIDEO_COURSES_PATH	= BASE_PATH + "training/courses/";
	public static final String VIDEO_LECTURES_PATH	= BASE_PATH + "training/lectures/";
	
	public final static String VIDEO_FILE_PATTERN = "^\\w.*\\.mp4$";
}
