package net.ccghe.emocha.activities;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.R;
import net.ccghe.emocha.model.PatientDB;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
    
    private final Handler guiHandler = new Handler();
    
    private static final int ROW_ID      = 0;
    private static final int NAME_COLUMN    = 1;
    private static final int STATUS_COLUMN  = 2;
    
    
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
        Runnable tstMes;
      
        final Intent rData = data;
        
        if(resultCode == RESULT_OK ){
            mBuilder.setLength(0);
            mBuilder.append("New patient ");
            mBuilder.append(rData.getExtras().getString("name"));
            mBuilder.append(" has been added");
            
            tstMes = new Runnable() {
                public void run(){
                    //this will run on the mainGui Thread , because a service is running on the main GUI thread
                    Toast.makeText(getApplicationContext(), mBuilder.toString() , Toast.LENGTH_SHORT ).show();
                }
            };
            
            guiHandler.post(tstMes);
           
        }
        if(resultCode == RESULT_CANCELED ){
//            mBuilder.setLength(0);
//            mBuilder.append("Cancelled");
//            tstMes = new Runnable() {
//                public void run(){
//                    //this will run on the mainGui Thread , because a service is running on the main GUI thread
//                    Toast.makeText(getApplicationContext(), mBuilder.toString() , Toast.LENGTH_SHORT ).show();
//                }
//            };
//            
//            guiHandler.post(tstMes);
 
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
	// AdapterContextMenuInfo info =
	// (AdapterContextMenuInfo)item.getMenuInfo();
	if (!item.getTitle().equals("cancel")) {
	    Intent i = new Intent(Constants.ODK_INTENT_FILTER_SHOW_FORM);
	    i.putExtra(Constants.ODK_FILEPATH_KEY, Constants.PATH_ODK_FORMS + "eMOCHA.xml");
	    startActivity(i);

	}
	return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

	Object o = this.getListAdapter().getItem(info.position);
	Cursor tPatientCursor = (Cursor) o;
	// we can also set ids to the menu for easier discrimenation in the
	// handler. For
	// now i use the title and check if == cancel
	menu.add("Enter data for:\n" + tPatientCursor.getString(NAME_COLUMN));
	menu.add("cancel");
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	Object o = this.getListAdapter().getItem(position);
	mPatientCursor = (Cursor) o;

	Intent tIntent = new Intent(getApplicationContext(), PatientInfoActivity.class);
	tIntent.putExtra("row_id_patient", mPatientCursor.getInt(ROW_ID));
	startActivity(tIntent);

	// tPatientCursor.close();

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
