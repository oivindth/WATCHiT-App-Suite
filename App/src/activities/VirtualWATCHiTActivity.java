package activities;

import java.util.ArrayList;
import java.util.List;

import listeners.SpaceChangeListener;

import no.ntnu.emergencyreflect.R;

import parsing.GenericSensorData;
import parsing.Parser;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import asynctasks.AuthenticateUserTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import dialogs.ChooseEventDialog;
import dialogs.ChooseEventDialog2;
import dialogs.NoteDialog;
import dialogs.SettingsDialog;
import dialogs.ShareDialog;
import dialogs.ChooseEventDialog2.ChooseEventDialogListener;
import dialogs.NoteDialog.NoteDialogListener;
import dialogs.SettingsDialog.SettingsDialogListener;
import dialogs.ShareDialog.ShareDialogListener;



public class VirtualWATCHiTActivity extends BaseActivity implements OnClickListener, ShareDialogListener, NoteDialogListener, SpaceChangeListener {

	private ImageView happyButton, neutralButton, sadButton, rescueButton, noteButton;
	private String currentText, notes;
	private int currentImageID;
	private Bundle b;
	private ShareDialog dialog;
	private NoteDialog noteDialog;

	private Double latitude, longitude;
	private ProgressDialog mProgressDialog;

	/**
	 * Location
	 */
	private LocationManager mLocationManager;
	private LocationProvider provider;
	private Location currentBestLocation;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        happyButton = (ImageView) findViewById(R.id.imageViewHappy);
        
        neutralButton = (ImageView) findViewById(R.id.imageViewNeutral);
        sadButton = (ImageView) findViewById(R.id.imageViewSad);
        rescueButton = (ImageView) findViewById(R.id.imageViewRescue);
        noteButton = (ImageView) findViewById(R.id.imageViewNote);
        
        happyButton.setOnClickListener(this); 
        neutralButton.setOnClickListener(this);
        sadButton.setOnClickListener(this);
        rescueButton.setOnClickListener(this);
        noteButton.setOnClickListener(this);
 
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            
			mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			String gpsLocationProvider = LocationManager.GPS_PROVIDER;
			provider =
			        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
			
			if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				//Log.d("network", "network enabled");
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
			}
			
			if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				        2000,          // 2-second interval.
				        0,             // 0 meters.
				        listener);
			}
    }
	@Override
	public void onClick(View v) {
		
		dialog = new ShareDialog();
		
		switch (v.getId()) {
		case R.id.imageViewHappy:
			happyButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));			
			dialog.setArguments(setDataAndGetBundle("I'm happy", R.drawable.happy_tag));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewNeutral:
			neutralButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			dialog.setArguments(setDataAndGetBundle("I'm so and so", R.drawable.neutral_tag));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewSad:
			sadButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			dialog.setArguments(setDataAndGetBundle("I'm sad", R.drawable.sad_tag));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewRescue:
			rescueButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			dialog.setArguments(setDataAndGetBundle("I rescued someone", R.drawable.rescue_tag));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewNote:
			noteButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			noteDialog = new NoteDialog();
			noteDialog.setArguments(setDataAndGetBundle("note", R.drawable.note));
			noteDialog.show(getSupportFragmentManager(), "note_dialog");
			break;
		}	
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	private Bundle setDataAndGetBundle (String text, int imageID) {
		b = new Bundle();
		currentText = text;
		currentImageID = imageID;
		b.putString("text", currentText);
		b.putInt("imageID", currentImageID);
		return b;
	}
	

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.vwatchitmenu, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case android.R.id.home:
			return true;
		case R.id.menu_changeSpace:
			ChooseEventDialog dialog = new ChooseEventDialog();
			dialog.setArguments(b);
			dialog.show(getSupportFragmentManager(), "event");
			return true;
		case R.id.menu_location:
			updateLocationProviders();
			Toast.makeText(getBaseContext(), "Location providers updated.", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_refresh:
		    ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
    	                    || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {	
    	    	if (sApp.connectionHandler.getStatus() != ConnectionStatus.ONLINE) {
    	    		//Log.d("status", "status: " + connectionHandler.getStatus());
    	    		 new AuthenticateUserTask(this, sApp.getUserName(), sApp.getPassword());
    	    	} else {
    	    		new GetSpacesTask(this).execute();
    	    	}
    	    } else {
    	    	new GetSpacesTask(this).execute();
    	    }
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onShareDialogButtonClicked() {
		if (sApp.currentActiveSpace == null) {
			Toast.makeText(getBaseContext(), "Choose event first", Toast.LENGTH_SHORT).show();
			return;
		}
		if (currentBestLocation == null) {
			Toast.makeText(getBaseContext(), "Need a location fix first...", Toast.LENGTH_SHORT).show();
			return;
		}
		else {
			String stringLat = String.valueOf(latitude);
			String stringLong = String.valueOf(longitude);
			GenericSensorData gsd = Parser.buildSimpleXMLObject(currentText, stringLat, stringLong);
			DataObject md = Parser.buildDataObjectFromSimpleXMl(gsd, sApp.connectionHandler.getCurrentUser().getFullJID(), sApp.connectionHandler.getCurrentUser().getUsername());
			new PublishDataTask(md, sApp.currentActiveSpace.getId()).execute();
		}
	}

	@Override
	public void onShareNoteButtonClicked(String notes) {
		if (currentBestLocation == null) {
			Toast.makeText(getBaseContext(), "Need a location fix first...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (sApp.currentActiveSpace == null) {
			Toast.makeText(getBaseContext(), "Choose event first", Toast.LENGTH_SHORT).show();
			return;
		}
			String stringLat = String.valueOf(latitude);
			String stringLong = String.valueOf(longitude);
			GenericSensorData gsd = Parser.buildSimpleXMLObject(notes, stringLat, stringLong);
			DataObject md = Parser.buildDataObjectFromSimpleXMl(gsd, sApp.connectionHandler.getCurrentUser().getFullJID(), sApp.connectionHandler.getCurrentUser().getUsername());
			new PublishDataTask(md, sApp.currentActiveSpace.getId()).execute();
	}

	   public void showProgress(String title, String msg) {
	    	if (mProgressDialog != null && mProgressDialog.isShowing())
	    		            dismissProgress();
	    		        mProgressDialog =  new ProgressDialog(this);
	    		        mProgressDialog.setTitle(title);
	    		        mProgressDialog.setMessage(msg);
	    		        mProgressDialog.setCancelable(true);
	    		        mProgressDialog.show();
	    		        
	    		        //ProgressDialog.show(this, title, msg).setCancelable(true);
	    		    }
	    public void dismissProgress() {
	        if (mProgressDialog != null) {
	            mProgressDialog.dismiss();
	            mProgressDialog = null;
	        }
	    }
	   
		private final LocationListener listener = new LocationListener() {

		    @Override
		    public void onLocationChanged(Location location) {

		    	if (isBetterLocation(location, currentBestLocation)) {
		    		currentBestLocation = location;
		    	} else {
		    		//location = currentBestLocation;
		    	}
		    	
		    	//Log.d("LocationService", "Location: " + " latitude: " + currentBestLocation.getLatitude() + " longitude: " + currentBestLocation.getLongitude());
		    	longitude = currentBestLocation.getLongitude();
		    	latitude = currentBestLocation.getLatitude();
		        }

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
			}
		    
		};
		private static final int ONE_HALF_MINUTE = 1000 * 30 * 1;

		/** Determines whether one Location reading is better than the current Location fix
		  * @param location  The new Location that you want to evaluate
		  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
		  * @author Google
		  * url http://developer.android.com/training/basics/location/currentlocation.html
		  */
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		    if (currentBestLocation == null) {
		        // A new location is always better than no location
		        return true;
		    }

		    // Check whether the new location fix is newer or older
		    long timeDelta = location.getTime() - currentBestLocation.getTime();
		    boolean isSignificantlyNewer = timeDelta > ONE_HALF_MINUTE;
		    boolean isSignificantlyOlder = timeDelta < ONE_HALF_MINUTE;
		    boolean isNewer = timeDelta > 0;

		    // If it's been more than two minutes since the current location, use the new location
		    // because the user has likely moved
		    if (isSignificantlyNewer) {
		        return true;
		    // If the new location is more than two minutes older, it must be worse
		    } else if (isSignificantlyOlder) {
		        return false;
		    }

		    // Check whether the new location fix is more or less accurate
		    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		    boolean isLessAccurate = accuracyDelta > 0;
		    boolean isMoreAccurate = accuracyDelta < 0;
		    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		    // Check if the old and new location are from the same provider
		    boolean isFromSameProvider = isSameProvider(location.getProvider(),
		            currentBestLocation.getProvider());

		    // Determine location quality using a combination of timeliness and accuracy
		    if (isMoreAccurate) {
		        return true;
		    } else if (isNewer && !isLessAccurate) {
		        return true;
		    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
		        return true;
		    }
		    return false;
		}

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
		    if (provider1 == null) {
		      return provider2 == null;
		    }
		    return provider1.equals(provider2);
		}
		
		
		private void updateLocationProviders() {
			provider =
			        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
			
			if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
			}
			
			if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				        2000,          // 1-second interval.
				        0,             // 0 meters.
				        listener);
			}
       
		}

		@Override
		public void onSpaceChanged(int position) {
			if (position == -1) return;
			//if (sApp.dataHandler.getHandledSpaces().contains(sApp.currentActiveSpace)) {
			//sApp.dataHandler.removeSpace(sApp.currentActiveSpace.getId());
			//}
			Space space = sApp.spacesInHandler.get(position);
			sApp.currentActiveSpace = space;
			
		}

		@Override
		public void onDataFetchedFromSpace() {
			// TODO Auto-generated method stub
			
		}
	
		
		
}