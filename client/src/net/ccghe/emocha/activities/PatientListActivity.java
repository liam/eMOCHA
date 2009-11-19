package net.ccghe.emocha.activities;

import java.io.File;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.R;
import net.ccghe.emocha.model.PatientDB;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PatientListActivity extends ListActivity {
    
    private static final int ROW_ID      = 0;
    private static final int NAME_COLUMN    = 1;
    private static final int STATUS_COLUMN  = 2;
    
    private static final int MENU_FILL_FORM = 0;
    private static final int MENU_CANCEL = 1;
    private static final int MENU_INFO = 2;
    
    private static final int RESULT_CODE = 1;
    
    final StringBuilder mBuilder = new StringBuilder();
    
    private Button newPatientBtn;
    private Cursor mPatientCursor = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.patient_list);
        
        TextView mH1 = (TextView) findViewById(R.id.header_title);
        mH1.setText("Patient listing");

        newPatientBtn = (Button) this.findViewById(R.id.ButtonNewPatientForm);        
        newPatientBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                Intent tIntent = new Intent(getApplicationContext(), PatientAddActivity.class);               
                startActivityForResult(tIntent , RESULT_CODE);
            }
            
            
        });
        
        registerForContextMenu(getListView());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      
        final Intent rData = data;
        
        if(resultCode == RESULT_OK ){
            mBuilder.setLength(0);
            mBuilder.append("New patient ");
            mBuilder.append(rData.getExtras().getString("name"));
            mBuilder.append(" has been added");
            
            Toast.makeText(getApplicationContext(), mBuilder.toString() , Toast.LENGTH_SHORT ).show();
           
        }
        if(resultCode == RESULT_CANCELED ){
        }
 
        super.onActivityResult(requestCode, resultCode, data);
   
    }

    @Override
    protected void onResume() {
        populateFields();
        super.onResume();
    }

    @Override
    protected void onStop() {
	if (mPatientCursor != null)
	    mPatientCursor.close();
	try {
	    PatientDB.destroy();
	} catch (IllegalStateException e) {

	}
	super.onStop();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case MENU_FILL_FORM:
	    String mainForm = Constants.PATH_ODK_FORMS + "eMOCHA.xml";
	    File mainFormFile = new File(mainForm);
	    if (mainFormFile.exists()) {
		Intent i = new Intent(Constants.ODK_INTENT_FILTER_SHOW_FORM);
		i.putExtra(Constants.ODK_FILEPATH_KEY, mainForm);
		startActivity(i);
	    } else {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Main form no found");
		alertDialog.setMessage("Please configure eMOCHA's network settings and wait until all data is downloaded.");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			return;
		    }
		});
		alertDialog.show();
	    }
	    break;
	case MENU_INFO:
	    Intent tIntent = new Intent(getApplicationContext(), PatientInfoActivity.class);
	    tIntent.putExtra("row_id_patient", mPatientCursor.getInt(ROW_ID));
	    startActivity(tIntent);

	    // tPatientCursor.close();
	    break;
	}
	return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

	Object o = this.getListAdapter().getItem(info.position);
	Cursor tPatientCursor = (Cursor) o;
	// we can also set ids to the menu for easier discrimination in the
	// handler. For
	// now i use the title and check if == cancel
	menu.setHeaderTitle("Patient: " + tPatientCursor.getString(NAME_COLUMN));
	
	menu.add(0, MENU_FILL_FORM, 0, "Fill out form");
	menu.add(0, MENU_INFO, 0, "Patient info");
	menu.add(0, MENU_CANCEL, 0, "Cancel");
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	Object o = this.getListAdapter().getItem(position);
	mPatientCursor = (Cursor) o;

	l.showContextMenuForChild(v);
	
	super.onListItemClick(l, v, position, id);
    }

    // You then create your handler method:
    /*
    private Boolean onLongListItemClick( int position, long id) { 
        Object o = this.getListAdapter().getItem(position);
        mPatientCursor = (Cursor) o;        
        Log.i("EMOCHA" , "patient selected for odk " + mPatientCursor.getString(NAME_COLUMN) );        
        //tPatientCursor.close();        
        return true;
    }
    */
    
    private void populateFields() {
        
     // This will be used by our SimpleCursorAdapter to bind fields in each row to
        // data from our cursor.  Note that we have an extra field in the cursor that
        // determines a display attribute for the field we display.
        class ShowViewBinder implements SimpleCursorAdapter.ViewBinder {

            // this cursor is built by calling a function that returns a few things.
            // right now I'm interested in the first (title) and the third
            //(downloaded status)


            boolean retval = false;

            //@Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                
              
                switch( columnIndex ){

                    case NAME_COLUMN:
                        TextView tv = (TextView) view;
                        tv.setText(cursor.getString(NAME_COLUMN)); 
                        retval = true;
                    break;
                
                    case STATUS_COLUMN:
                        String status = cursor.getString(STATUS_COLUMN);
                        ImageView imageView = (ImageView) view;
                        //Log.i("EMOCHA" , "status " + status );
                        if( status.equals("1") ){
                            imageView.setBackgroundResource(R.drawable.user_blue_32);
                        } else {
//                            imageView.setBackgroundResource(R.drawable.user_warning_32);
                            imageView.setBackgroundResource(R.drawable.user_blue_32);
                        }  
                        retval = true;
                    break;
                }

                return retval;
            }
        }
        
        
        PatientDB.init( this );
        
        mPatientCursor = PatientDB.getAllPatients();
        
        startManagingCursor(mPatientCursor);
        // Now create a new list adapter bound to the cursor. 
        // SimpleListAdapter is designed for binding to a Cursor.
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, 
                R.layout.patientlist_row,
                mPatientCursor,                                    
                new String[] {PatientDB.KEY_NAME, PatientDB.KEY_STATUS}, 
                new int[]{R.id.text1 , R.id.ImageView01}); // Parallel array of which template objects to bind to those columns.
        adapter.setViewBinder( new ShowViewBinder() );
        // Bind to our new adapter.
        setListAdapter(adapter);
        


        
        
    }
    
    
    
}
