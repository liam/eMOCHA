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
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity {

    private VideoView pVideo;
    private MediaController pPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mh_video_player);

	    Bundle extras = getIntent().getExtras();
		String tDocID = extras.getString(Constants.DOC_ID);
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.mh_video_player);

        pVideo = (VideoView) findViewById(R.id.video);
        pVideo.setVideoPath(tDocID);
        pVideo.start();
        
        pPlayer = new MediaController(this);
        pPlayer.setMediaPlayer(pVideo);     
        pVideo.setMediaController(pPlayer);
        pVideo.requestFocus();
    }

} 
