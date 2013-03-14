package activities;


import interfaces.LayersChangeListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dialogs.MapLayersDialog;
import enums.SharedPreferencesNames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class MapActivity extends BaseActivity implements LayersChangeListener {
	
	private GoogleMap mMap;
	private MainApplication sApp;
	private SharedPreferences mapPreferences;
	
	private boolean moodLayerMode;
	private boolean personLayerMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
	    mapPreferences = getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , MODE_PRIVATE );
	    moodLayerMode = mapPreferences.getBoolean("mood_layer", false);
	    personLayerMode = mapPreferences.getBoolean("person_layer", false);	
	     
	}
	
	@Override
	public void onResume () {
		super.onResume();
		setUpMapIfNeeded();	
		
		mMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				mMap.addMarker(new MarkerOptions()
		        .position(latLng)
		        .title("Person found"))
		        .setSnippet("User: James" + "\n" + "Time: 14:30");
		        
			}
		});
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker arg0) {
				showToast("Here we go to new fragment that displays more info with quiz button etc....");
				
			}
		});
	
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Save the state of the user choice of layers since the activity may be destroyed.
		SharedPreferences.Editor ed = mapPreferences.edit();
        ed.putBoolean("mood_layer", moodLayerMode);
        ed.putBoolean("person_layer", personLayerMode);
        ed.commit();
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
	public void onMoodLayerChanged(boolean on) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPersonLayerChanged(boolean on) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
