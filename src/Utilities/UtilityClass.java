package Utilities;

import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.ConnectivityManager;

public class UtilityClass {
	
	static BluetoothAdapter mBluetoothAdapter;
	public static boolean isBluetoothConnected() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter != null) {
			  if (mBluetoothAdapter.isEnabled()) 
			       return true;
		}
		return false;
	}
	
	/**
	 * Get the paired device with the given name
	 * @param deviceName
	 * @return
	 */
	public static BluetoothDevice getDevice(String deviceName) {
       	BluetoothDevice mDevice = null;
   		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
   		if(pairedDevices.size() > 0)
           {
               for(BluetoothDevice device : pairedDevices)
               {
                   if(device.getName().equals(deviceName)) mDevice = device;
               }
           }
   		return mDevice;
       }
	
	/**
	 * Get paired devices
	 * @return
	 */
	public static List<BluetoothDevice> getDevices() {
   		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
   		return (List<BluetoothDevice>) pairedDevices;
       }
	
	
	public static boolean isConnectedToInternet(Context context) {
		
		ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
	                    || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
	            return true;
	    return false;
	
	}
	
	
	 
	
}
