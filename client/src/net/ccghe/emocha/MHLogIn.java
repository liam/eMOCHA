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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MHLogIn extends Activity {

	private Button pLoginButton;
	private EditText pInputUser;
	private EditText pInputPassword;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mh_log_in);

		pLoginButton   = (Button) findViewById(R.id.ButtonLogIn);
		pInputUser     = (EditText) findViewById(R.id.InputUser);
		pInputPassword = (EditText) findViewById(R.id.InputPassword);
		
		pLoginButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {		
				String tUser     = pInputUser.getText().toString();
				String tPassword = pInputPassword.getText().toString();

				if (tUser.equals("user") && tPassword.equals("pass")) {
					Intent i = new Intent(getApplicationContext(), MHMainMenu.class);
	                startActivity(i);
				} else {
					Toast.makeText(MHLogIn.this, "Wrong password", Toast.LENGTH_SHORT).show();				
				}
			}

		});

	}
}
