package service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class LocationService extends AbstractService {

	
	public static final int MSG_UPDATE_LOCATION = 9999;
	
	
	LocationManager mLocationManager;
	LocationProvider provider;
	Location currentBestLocation;
	
	@Override
	public void onStartService() {
		Log.d("LocationService", "Location Service started....");
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		String gpsLocationProvider = LocationManager.GPS_PROVIDER;
		
		provider =
		        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
		
		if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, listener);
		}
		
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			        10000,          // 10-second interval.
			        10,             // 10 meters.
			        listener);
		}
		
	
	}

	@Override
	public void onStopService() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}
	
	private final LocationListener listener = new LocationListener() {

	    @Override
	    public void onLocationChanged(Location location) {

	    	if (isBetterLocation(location, currentBestLocation)) {
	    		currentBestLocation = location;
	    	} else {
	    		location = currentBestLocation;
	    	}
	    	
	    	Log.d("LocationService", "Location: " + " latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
	    	
	    	Bundle b = new Bundle();
			b.putDouble("longitude",  location.getLongitude());
			b.putDouble("latitude", location.getLatitude());
			Message messageToActivity = Message.obtain(null, MSG_UPDATE_LOCATION);
			messageToActivity.setData(b);
			send(messageToActivity);
	            
	        }

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
			
		}
	    
	};

	
	private static final int TWO_MINUTES = 1000 * 60 * 2;

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
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
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
	
}
