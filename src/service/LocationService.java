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
	
	
	@Override
	public void onStartService() {
		Log.d("LocationService", "Location Service started....");
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		provider =
		        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		        10000,          // 10-second interval.
		        10,             // 10 meters.
		        listener);
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
	        // A new location update is received.  Do something useful with it.  In this case,
	        // we're sending the update to a handler which then updates the UI with the new
	        // location.
	    	
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

}
