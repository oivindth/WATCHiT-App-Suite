package Utilities;

import java.util.List;
import java.util.Set;

import enums.ValueType;

import parsing.GenericSensorData;

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
	
	
	public static String parseTimeStampToTimeAndDate (String timeStamp) {
		timeStamp = timeStamp.replace('T', '-');
		return timeStamp.substring(0, 19);
	}
	
	public static String parseTimeStampToTimeOnly(String timeStamp) {
		timeStamp = timeStamp.replace('T', '-');
		return timeStamp.substring(11, 16);
	}
	
	
	
	public static String getStringDataFromDataObjects (List<GenericSensorData> datas) {
		int moods= 0;
		int persons =0;
		int notes= 0;
		int me=0;
		for (GenericSensorData genericSensorData : datas) {
			ValueType valueType = ValueType.getValue(genericSensorData.getValue().getText());
			if (valueType == ValueType.MOOD_HAPPY || valueType == ValueType.MOOD_NEUTRAL || valueType == ValueType.MOOD_SAD) moods++;
			if (valueType == ValueType.PERSON) persons++;
			if (valueType == ValueType.NOTES) notes++;
		}
		
		String s = "Data size: " + datas.size() + "\n moods registered: " + moods + "\n persons found: " + persons + "\n Notes: " + notes;
		return s;
	}
	
}
