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

public class MHCallMenu extends Activity {
	private Button pCallConsult;
	private Button pCallClinic;
	private Button pCallOther;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mh_call_menu);
		
		
		pCallConsult = (Button) findViewById(R.id.ButtonCallConsult);
		pCallClinic  = (Button) findViewById(R.id.ButtonCallClinic);
		pCallOther   = (Button) findViewById(R.id.ButtonCallOther);
		
		pCallConsult.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				callHospital();
			}
		});
		pCallClinic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				callHospital();
			}
		});
		pCallOther.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent OpenDialer = new Intent(Intent.ACTION_DIAL);
	            startActivity(OpenDialer);
            }
		});
	}
	private void callHospital() {
		try {
	        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:+256777667444")));
	      } catch (Exception e) {
	        e.printStackTrace();
	    }								
	}
}
