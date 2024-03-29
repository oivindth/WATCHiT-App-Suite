package service;
/**
 * Copyrigth 2013 �ivind Thorvaldsen
 * This file is part of Reflection App

    Reflection App is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Message;

public class LocationService extends AbstractService {

	
	public static final int MSG_UPDATE_LOCATION = 9999;
	
	
	LocationManager mLocationManager;
	LocationProvider provider;
	Location currentBestLocation;
	
	@Override
	public void onStartService() {
		//Log.d("LocationService", "Location Service started....");
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		String gpsLocationProvider = LocationManager.GPS_PROVIDER;
		
		provider =
		        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
		
		if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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

	
	private static final int ONE_MINUTE = 1000 * 30 * 1;

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
	    boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
	    boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
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
