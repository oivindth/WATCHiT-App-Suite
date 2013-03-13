package activities;


import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import dialogs.MapLayersDialog;

import android.content.Intent;
import android.os.Bundle;


public class MapActivity extends BaseActivity {
	
	private GoogleMap mMap;
	private MainApplication sApp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
		//mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		setUpMapIfNeeded();
		
	}
	
	@Override
	public void onResume () {
		super.onResume();
		setUpMapIfNeeded();
	
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// save state when in on pause or in ondestroy..
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
    	switch (item.getItemId()) {
    	
    		case android.R.id.home:
    			// iff up instead of back:
                //intent = new Intent(this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();
                return true;
    	
            case R.id.menu_sync:
            	showToast("Sync it up");
            	return true;
            	
            case R.id.menu_layers:
            	 new MapLayersDialog().show(getSupportFragmentManager(), "layersdialog");
            	 return true;
            	 
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	private void setUpMapIfNeeded() {
	     //Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	                          //  .getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	             //The Map is verified. It is now safe to manipulate the map.

	        }
	    }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	
}
