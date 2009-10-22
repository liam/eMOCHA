package net.ccghe.emocha.activities;

import net.ccghe.emocha.R;
import net.ccghe.emocha.model.PatientDB;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PatientAddActivity extends Activity {
    
    private final Handler guiThread  = new Handler();
    
    private Button addBtn;
    private Button cancelBtn;
    private EditText nameField;
    private EditText statusField;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        PatientDB.init(this);
        
        this.setContentView(R.layout.patient_add);
        
        
        TextView mH1 = (TextView) findViewById(R.id.header_title);
        mH1.setText("Edit a patient data");
        
        
        addBtn = (Button) this.findViewById(R.id.ButtonAddPatient);
        cancelBtn = (Button) this.findViewById(R.id.ButtonCancelAddPatient);
        nameField = (EditText) this.findViewById(R.id.PatientInputName);
        statusField = (EditText) this.findViewById(R.id.PatientInputStatus);
        
        addBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                addNewPatient();
            }
            
        });
        
        cancelBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                cancel();
            }
            
        });
        
        
        
        Intent i = this.getIntent();
        int patientId = i.getIntExtra("row_id_patient", -1);

        if(patientId == -1){       
            Log.e("EMOCHA" , "ERROR GETTING USER BY ROW ID :: "+ Integer.toString(patientId) );  
        }else{
        
            final Cursor cursor;
            
            try {
                
                
                PatientDB.init(this);
                cursor = PatientDB.getPatient( patientId );
                nameField.setText( cursor.getString(1) );
                statusField.setText( cursor.getString(2) );
                cursor.close();
                PatientDB.destroy();
                
            } catch (SQLException ignore) {
                Log.e("EMOCHA" , getClass().getSimpleName() + " :: error :: row id="+ Integer.toString(patientId) );  
            }
            
        }  

     }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        try {
            PatientDB.destroy();
        } catch (IllegalStateException ignore) {
           
        }
        super.onStop();
    }
    private void addNewPatient() {
        String name = nameField.getText().toString();
        String status = statusField.getText().toString();
        Intent resultIntent = getIntent();
        
        if(!validateInputFields()) {
            guiThread.post( new Runnable() {
                public void run(){
                    Toast.makeText(getApplicationContext(), "please fill in both fields" , Toast.LENGTH_SHORT ).show();
                }
            });
            
        } else {
            
  
            try {
                Log.e("EMOCHA" , "adding " + name + ":" + status);
                 
                PatientDB.init(this);
                 
                 long rowid = PatientDB.insertPatient(name, status);
                 if(rowid == -1){
                     setResult(RESULT_CANCELED, resultIntent);
                     throw new SQLException("Insertion failed");
                 }else{
                     resultIntent.putExtra("name", name);
                 }
            } catch (SQLException e) {
                Log.e("EMOCHA" , getClass().getSimpleName() +" :: Error ::" + e.getMessage() );
            } finally {
                setResult(RESULT_OK, resultIntent);
                
                finish();
            }
        }
    }
    
    private void cancel(){
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }
    
    private boolean validateInputFields(){
        String name = nameField.getText().toString();
        String status = statusField.getText().toString();
        if(name.length() == 0  || status.length()==0) return false;
        return true;
    }
}
