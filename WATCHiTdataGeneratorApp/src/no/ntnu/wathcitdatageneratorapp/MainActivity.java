package no.ntnu.wathcitdatageneratorapp;


import java.util.ArrayList;
import java.util.List;

import parsing.GenericSensorData;
import parsing.Parser;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dialogs.ChooseEventDialog;
import dialogs.NoteDialog;
import dialogs.SettingsDialog;
import dialogs.SettingsDialog.SettingsDialogListener;
import dialogs.ShareDialog;
import dialogs.ChooseEventDialog.ChooseEventDialogListener;
import dialogs.NoteDialog.NoteDialogListener;
import dialogs.ShareDialog.ShareDialogListener;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;


public class MainActivity extends SherlockFragmentActivity implements OnClickListener, ShareDialogListener, NoteDialogListener, SettingsDialogListener, ChooseEventDialogListener {

	private ImageView happyButton, neutralButton, sadButton, rescueButton, noteButton;
	private String currentText, notes;
	private int currentImageID;
	private Bundle b;
	private ShareDialog dialog;
	private NoteDialog noteDialog;

	private UserLoginTask mAuthTask = null;
	
	private Double latitude, longitude;
	
	private String host, domain, applicationId, username, password;
	private int port;
	
	private Space currentActiveSpace;
	
	private int checkedEvent = -1;
	
	private ProgressDialog mProgressDialog;
	private List<Space> spaces;
	private List<DataObject> dataObjects;
	private List<GenericSensorData> genericSensorDataObjects;
	
	private ArrayList<String> adapter;
	
	
	/**
	 * Handlers used for MIRROR Spaces.
	 */
	public ConnectionConfigurationBuilder connectionConfigurationBuilder; 
	public ConnectionConfiguration connectionConfig; 
	public ConnectionHandler connectionHandler; 
	public SpaceHandler spaceHandler; 
	public String dbName = "sdkcache_wdgapp"; 
	public DataHandler dataHandler;
	
	
	/**
	 * Location
	 */
	private LocationManager mLocationManager;
	private LocationProvider provider;
	private Location currentBestLocation;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
 
        SharedPreferences settings = getSharedPreferences("Settings", 0);
        
        host = settings.getString("host", getString(R.string.host));
    	domain = settings.getString("domain", getString(R.string.domain));
    	applicationId = getString(R.string.application_id);
    	port = settings.getInt("port", 5222); //5222 is standard port.
    	username = settings.getString("username", "");
    	password = settings.getString("password", "");
    	
    	
    	
    	      //Configure connection
    		connectionConfigurationBuilder = new ConnectionConfigurationBuilder(domain,applicationId);
            connectionConfigurationBuilder.setHost(host);
            connectionConfigurationBuilder.setPort(port);
            connectionConfig = connectionConfigurationBuilder.build();
            
            if (username.length() >1 && password.length() >1) {
            connectionHandler = new ConnectionHandler(username, password, connectionConfig);
            spaceHandler = new SpaceHandler(getBaseContext(), connectionHandler, dbName);
            dataHandler = new DataHandler(connectionHandler, spaceHandler);
            
            ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
    	                    || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
    	    	
    	    	 //new UserLoginTask().execute();
    	    	 if (mAuthTask != null) return;
    		        mAuthTask = new UserLoginTask();
    				mAuthTask.execute((Void) null);
    	    } else {
    	    	new GetSpacesTask().execute();
    	    }
            }
    	
			mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			String gpsLocationProvider = LocationManager.GPS_PROVIDER;
			
			provider =
			        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
			
			
			if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				Log.d("network", "network enabled");
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
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case android.R.id.home:
			return true;
	
		case R.id.menu_logout:
			new SettingsDialog().show(getSupportFragmentManager(), "settings");
			return true;
			
		case R.id.menu_changeSpace:
			Bundle b = new Bundle();
			
			if (spaces == null) {
				Toast.makeText(getBaseContext(), "Please refresh first.", Toast.LENGTH_SHORT).show();
				return true;
			}
		
			b.putStringArrayList("events", adapter);
			b.putInt("checkedEvent", checkedEvent);
			
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
    	    	if (connectionHandler.getStatus() != ConnectionStatus.ONLINE) {
    	    		Log.d("status", "status: " + connectionHandler.getStatus());
    	    		 new UserLoginTask().execute();
    	    	} else {
    	    		new GetSpacesTask().execute();
    	    	}
    	    } else {
    	    	new GetSpacesTask().execute();
    	    }
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onShareDialogButtonClicked() {
		if (currentActiveSpace == null) {
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
			DataObject md = Parser.buildDataObjectFromSimpleXMl(gsd, connectionHandler.getCurrentUser().getFullJID(), connectionHandler.getCurrentUser().getUsername());
			new PublishDataTask(md, currentActiveSpace.getId()).execute();
		}
	}

	@Override
	public void onShareNoteButtonClicked(String notes) {
		if (currentBestLocation == null) {
			Toast.makeText(getBaseContext(), "Need a location fix first...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (currentActiveSpace == null) {
			Toast.makeText(getBaseContext(), "Choose event first", Toast.LENGTH_SHORT).show();
			return;
		}
			String stringLat = String.valueOf(latitude);
			String stringLong = String.valueOf(longitude);
			GenericSensorData gsd = Parser.buildSimpleXMLObject(notes, stringLat, stringLong);
			DataObject md = Parser.buildDataObjectFromSimpleXMl(gsd, connectionHandler.getCurrentUser().getFullJID(), connectionHandler.getCurrentUser().getUsername());
			new PublishDataTask(md, currentActiveSpace.getId()).execute();
	}
	@Override
	public void saveSettingsButtonClick() {
		 SharedPreferences settings = getSharedPreferences("Settings", 0);
	        host = settings.getString("host", getString(R.string.host));
	    	domain = settings.getString("domain", getString(R.string.domain));
	    	applicationId = getString(R.string.application_id);
	    	port = settings.getInt("port", 5222); //5222 is standard port.
	    	username = settings.getString("username", "");
	    	password = settings.getString("password", "");
	    	
	        //Configure connection
			connectionConfigurationBuilder = new ConnectionConfigurationBuilder(domain,applicationId);
	        connectionConfigurationBuilder.setHost(host);
	        connectionConfigurationBuilder.setPort(port);
	        connectionConfig = connectionConfigurationBuilder.build();
	        connectionHandler = new ConnectionHandler(username, password, connectionConfig);
	        spaceHandler = new SpaceHandler(getBaseContext(), connectionHandler, dbName);
	        dataHandler = new DataHandler(connectionHandler, spaceHandler);
	        
	        
	        if (mAuthTask != null) return;
	        mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
	        
	        //new UserLoginTask().execute();
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress("Server", "Connecting to server...");
		}
		@Override
		protected Boolean doInBackground(Void... params) {
	      try {
	    	  connectionHandler.connect();
	      } catch (Exception e) {
	    	  e.printStackTrace();
	    	  return false;
	      }
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			dismissProgress();

			if (success) {
				Toast.makeText(getBaseContext(), "Logged in :)", Toast.LENGTH_SHORT).show();
				spaceHandler.setMode(Mode.ONLINE);
				dataHandler.setMode(Mode.ONLINE);
				new GetSpacesTask().execute();
			} else {
				Toast.makeText(getBaseContext(), "Failed to connect. Check server settings", Toast.LENGTH_SHORT).show();
				new GetSpacesTask().execute();
			}
		}
		

			@Override
			protected void onCancelled() {
				mAuthTask = null;
				dismissProgress();
				Toast.makeText(getBaseContext(), "Failed to connect. Check server settings", Toast.LENGTH_SHORT).show();
			}
		

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
	    private class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {    		
	    		@Override
	    		public void onPreExecute() {
	    			super.onPreExecute();
	    			showProgress("Syncing", "Syncing....");
	    		}
	    		protected Boolean doInBackground(Void...params) {
	    			try {
	    					spaces =  spaceHandler.getAllSpaces();
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    				return false;
	    			}
	    			return true;
	    		}

	    		protected void onPostExecute(final Boolean success) {
	    			dismissProgress();
	    			if (success) {
	    				Log.d("GETSPACESTASK", "successfully fetched spaces");
	    				adapter = new ArrayList<String>();
	    				for (Space space : spaces) {		
	    					adapter.add(space.getName());
	    				}

	    			} else {
	    				Toast.makeText(getBaseContext(), "Failed trying to sync events. Try refresh", Toast.LENGTH_SHORT).show();
	    				Log.d("GETSPACESTASK", "Something wentwrong when syncing events. Try Refresh." );
	    			}
	    		}
	    }

		@Override
		public void eventChosen(int which) {
				Log.d("which", "integer: " + which);
				if (which == -1) return;
				currentActiveSpace = spaces.get(which);
				checkedEvent = which;

			
		}
		
		private class PublishDataTask extends AsyncTask<Void, Void, Boolean> {

			private DataObject mDataObject;
			private String mSpaceId;
			

			public PublishDataTask (DataObject dataObject, String spaceId) {
				mDataObject = dataObject;
				mSpaceId = spaceId;
			}
		      @Override
		        protected void onPreExecute() {
		        	super.onPreExecute();
		        	showProgress("Sending data", "Sending data...");
		        }
			
			@Override
			protected Boolean doInBackground(Void... params) {
		    	 try {
		    		 dataHandler.registerSpace(mSpaceId);
					  dataHandler.publishDataObject(mDataObject, mSpaceId);
					  Log.d("publishWDGA", "dataobject: " + mDataObject.toString());
					  Log.d("publishWDGA", "just published dataobject with id: " + mDataObject.getId());

				} catch (Exception e) {
					e.printStackTrace();
					Log.d("ERROR:", "failed to publish dataobject with id: " + mDataObject.getId() );
					return false;
				}
		    	 return true;
			}

			protected void onPostExecute(final Boolean success) {
			dismissProgress();
				if (success) {
				Log.d("publish", "success");
				} else {
				Log.d("ERROR:", "Something went wrong");
				Toast.makeText(getBaseContext(), "Failed to send data. Try again..", Toast.LENGTH_SHORT).show();
				//mActivity.showToast("Failed to publish dataobject.....");
				//new PublishDataTask(mDataObject, mSpaceId).execute();
				}
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
		    	
		    	Log.d("LocationService", "Location: " + " latitude: " + currentBestLocation.getLatitude() + " longitude: " + currentBestLocation.getLongitude());
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
		public void onDestroy () {
			super.onDestroy();
			try {
				connectionHandler.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
}
