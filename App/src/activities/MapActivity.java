package activities;

import old.SettingsActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.Intent;
import android.os.Bundle;


public class MapActivity extends BaseActivity {
	
	private GoogleMap mMap;
	
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
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save")
        .setIcon(R.drawable.ic_navigation_refresh)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    menu.add("Switch User")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

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
            	if (sApp.OnlineMode) {
            		// Sync from internet
            	}
            	//showProgress("...");
            	return true;
        
            
            case R.id.menu_settings:
            	intent = new Intent(this, SettingsActivity.class);
            	startActivity(intent);
            	//finish();
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
	
	
	
	
	
}