package net.ccghe.emocha.activities;

import net.ccghe.emocha.R;
import net.ccghe.emocha.model.PatientDB;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PatientInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.patient_info);
        
	/*
        Button cancelBtn = (Button) this.findViewById(R.id.ButtonCancelAddPatient);
        cancelBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
            
        });
	*/
        
        
        TextView mH1 = (TextView) findViewById(R.id.header_title);
        
        
        
        Intent i = this.getIntent();
        final int patientId = i.getIntExtra("row_id_patient", -1);

        if (patientId == -1) {
            //this can be used to define if we need to edit or view the patient data

        } else {
            final Cursor cursor;

            try {

                PatientDB.init(this);

                cursor = PatientDB.getPatient(patientId);

                String name = cursor.getString(1);
                String header   = "Info about patient: "+name;
                
                TextView info = (TextView) findViewById(R.id.TextView01);
                info.setText( "Suspendisse tempor molestie erat sed fermentum. Ut facilisis luctus adipiscing. Suspendisse purus mi, hendrerit eu sodales at, gravida sit amet est. " );
                
                mH1.setText(header);
                
                cursor.close();

                Button editPatientBtn = (Button) this
                        .findViewById(R.id.ButtonEditPatient);

                editPatientBtn.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                       /* 
                        Intent tIntent = new Intent(getApplicationContext(), PatientEditActivity.class);
                        tIntent.putExtra("row_id_patient", patientId);
                        startActivity(tIntent);
                        finish();
                        */
                    }
                });

            } catch (SQLException ignore) {
                Log.e("EMOCHA", "ERROR GETTING USER BY ROW ID :: "
                        + Integer.toString(patientId));
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            PatientDB.destroy();
        } catch (IllegalStateException ignore) {
            
        }
    }
}
