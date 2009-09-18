package net.ccghe.emocha;

import net.ccghe.emocha.model.Preferences;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsReqPwd extends Activity {
	private TextView pResponseText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mh_settings_add_phone);
		
		pResponseText = (TextView) findViewById(R.id.SettingsAddPhoneResponse);
	}

	@Override
	protected void onResume() {
		super.onResume();
				
		if (Preferences.getServerURL().length() < Constants.SERVER_URL_MIN_LENGTH) {
			pResponseText.setText(R.string.settings_serverURL_undefined);			
		} else {
			// send POST to server
		}
	}
	
	

	
	
}
