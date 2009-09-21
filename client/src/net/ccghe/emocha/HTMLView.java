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
package net.ccghe.emocha;

import net.ccghe.utils.LocalFileContentProvider;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HTMLView extends Activity {
	private WebView webview;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.htmlview);

	    Bundle extras = getIntent().getExtras();
		String tDocID = extras.getString(Constants.DOC_ID);
	    
	    webview = (WebView) findViewById(R.id.webview);
	    //webview.getSettings().setJavaScriptEnabled(true);

	    
	    try {
		    webview.loadUrl(LocalFileContentProvider.makeUri(Constants.PATH_TRAINING_LIBRARY + tDocID));

		    //webview.loadUrl("file:///android_asset/full_" + tDocID + ".html" );

		    //String tFileContent = File.readFileAsString(Constants.PATH_TRAINING_LIBRARY + tDocID);		    
	    	//webview.loadData(tFileContent, "text/html", "utf-8");
	    } catch (Exception e) {
			// TODO: error loading html document
		}
    }
	
	
}
