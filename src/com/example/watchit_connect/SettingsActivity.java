package com.example.watchit_connect;

import com.example.watchit_connect.ApplicationsSettingsFragment.ApplicationsSettingsFfragmentListener;

import Utilities.UtilityClass1;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

//TODO: stop using the fracking editor and put values inn mainapplication as global variables?
public class SettingsActivity extends BaseActivity implements ApplicationsSettingsFfragmentListener {
	
	public static final String PREFS_SETTINGS = "settings_preferences";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings); 
		settings = getSharedPreferences(PREFS_SETTINGS, 0);
		editor = settings.edit(); 
		ApplicationsSettingsFragment settingsFRagment = new ApplicationsSettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, settingsFRagment  ,"settings").commit(); 
		
	}
	
    @Override
    public void onResume() {
    	
    	//TODO: Update status of buttons from settings
    	ApplicationsSettingsFragment  fragment = (ApplicationsSettingsFragment) getSupportFragmentManager().findFragmentByTag("settings");
    	boolean s1,s2,s3;
    	s1= settings.getBoolean("onlineMode", false);
    	s2 = settings.getBoolean("locationMode", false);
    	s3 = settings.getBoolean("WATCHiTMode", false);
    	fragment.updateView(s1, s2,s3 );
    	
    	
    	super.onResume();
    	 
    }

	@Override
	public void onlineModeClicked(boolean on) {
		if (on) {
			if ( UtilityClass1.isConnectedToInternet(getBaseContext())) {
				//TODO: Change spacehandler and datahandler to online mode.....
				
				//Set "hasLoggedIn" to true
				
			
				
				
			} else {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	// Add the buttons
		    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	               enableSettings(Settings.ACTION_DATA_ROAMING_SETTINGS);
		    	           }
		    	       });
		    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	               // User cancelled the dialog
		    	           }
		    	       });
		    	
		    	builder.setMessage("You need internet access to use online mode.")
		        .setTitle("Internet");
		    
		    	// Create the AlertDialog
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 
			}
			//TODO: Change handlers to online mode man.
			editor.putBoolean("onlineMode", true);
			
			
		} else {
			//TODO: Change handlers to offline mode maaaan.
			editor.putBoolean("onlineMode", false);
			
		}
		editor.commit();
	}

	@Override
	public void WATCHiTSwitchClicked(boolean on) {
		if (on) {
			
			// first bluetooth.
			// then popup to choose which device to pair?
			// when paired with wathcit: start service and wait for data continiously.
			editor.putBoolean("WATCHiTMode", true);
			
		}
			
		if (!on) {
			showToast("Stopped WATCHiT sync...");
			editor.putBoolean("WATCHiTMode", false);
		}
		editor.commit();
	}

	@Override
	public void locationMode(boolean on) { //TODO: MOVE LOCATIONMANAGER TO BASE?
		LocationManager locationManager =
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	    if (on) {
	    	
	        if (!gpsEnabled) {

		    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	// Add the buttons
		    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	               enableSettings(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    	          
		    	      
		    	           }
		    	       });
		    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	               editor.putBoolean("locationMode", false);
		    	           }
		    	       });
		    	
		    	builder.setMessage("You need to enable gps to use the location")
		        .setTitle("GPS");
		    
		    	// Create the AlertDialog
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 
		    	
		    	gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    	editor.putBoolean("locationMode", gpsEnabled);
		    } else {
		    	//gps er på..:
		        //TODO: Now use latitude and longitude in dataobjects.
		    	editor.putBoolean("locationMode", true);
		    }
	    	
			
	    	
	    } else {
	    	//TODO: Do not want to use location. Stop adding location to dataobject or just add mocked location data?
	    	editor.putBoolean("locationMode", false);
	    }
	
	  
	editor.commit();	
	}

	
}
