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
package net.ccghe.emocha.activities;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.R;
import net.ccghe.utils.LocalFileContentProvider;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class HtmlBookViewActivity extends Activity {
    private final String LOG_TAG = HtmlBookViewActivity.class.getSimpleName();
    
	private WebView webview;
	private ProgressDialog myProgressDialog = null;
	private StringBuilder sBuilder = new StringBuilder("Loading ");
	private TextView mH1;
	private String mBookName;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.htmlview);

	    mH1 = (TextView) findViewById(R.id.header_title);
	 
	    Bundle extras = getIntent().getExtras();
	    mBookName = extras.getString(Constants.DOC_ID);
		
		mH1.setText("Loading book: " + mBookName);
		
	    myProgressDialog = ProgressDialog.show(this , "Please wait..." , sBuilder.append( mBookName ).toString()  , true ); 
	    myProgressDialog.setCancelable(true);
	    myProgressDialog.setOnCancelListener( new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface dialog) {
                finish();
            }      
	    });
	    
	    webview = (WebView) findViewById(R.id.webview);
	    webview.setVisibility(View.INVISIBLE);
	    webview.setWebViewClient( new ViewClient() );
	    
	    sBuilder.setLength(0);
	    sBuilder.append(Constants.PATH_TRAINING_LIBRARY);
	    sBuilder.append(mBookName);
	    sBuilder.append(".html");

	    webview.loadUrl(LocalFileContentProvider.makeUri( sBuilder.toString() ));  

    }
	

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onReady(){
	    webview.setVisibility(View.VISIBLE);
	    myProgressDialog.dismiss();
	    
	    mH1.setText("Book: " + mBookName);
	}
    
	private final class ViewClient extends WebViewClient{

        @Override
	    public void onPageFinished(WebView view, String url) {
            onReady();
	        super.onPageFinished(view, url);
	    }
	}
}

