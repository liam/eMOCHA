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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MHTrainingItemInfo extends Activity {

	private String pVideoPath;
    private Button pPlayVideo;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mh_training_item_info);

        pPlayVideo = (Button) findViewById(R.id.ButtonPlayVideo);
        
        pPlayVideo.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
				  Intent tIntent = new Intent(getApplicationContext(), MHVideo.class);				
				  tIntent.putExtra(MHConstants.DOC_ID, pVideoPath);					  
				  startActivity(tIntent);        		
        	}
        });
	    
	    Bundle extras = getIntent().getExtras();

	    // get path to the requested video, replace file extension by .jpg (video thumbnail)
	    pVideoPath = extras.getString(MHConstants.DOC_ID);		
	    String tThumbPath = pVideoPath.replaceFirst(".mp4", ".jpg");
	    Uri tThumbUri = Uri.parse(tThumbPath);
	    
	    //TODO: check if the image exists, otherwise show some default thumb
	    	    
	    ImageView tThumb = (ImageView) findViewById(R.id.ImageVideoThumb);
	    
	    tThumb.setImageURI(tThumbUri);	    
	}

}
