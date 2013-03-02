package Utilities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;

public class UtilityClass1 {
	
	
	public static boolean isBluetoothConnected() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		} else {
		    if (mBluetoothAdapter.isEnabled()) {
		       return true;
		    }
		    
		}
		return false;
	}
	
	
	public static boolean isConnectedToInternet(Context context) {
		
		ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		
	    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
	                    || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
	            return true;
	    return false;
	
	}
	
	
	 
	
}
