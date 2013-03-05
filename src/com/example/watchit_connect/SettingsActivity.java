package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.watchit_connect.ApplicationsSettingsFragment.ApplicationsSettingsFfragmentListener;

import Utilities.UtilityClass;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

//TODO: stop using the fracking editor and put values inn mainapplication as global variables?
public class SettingsActivity extends BaseActivity implements ApplicationsSettingsFfragmentListener {


	private ApplicationsSettingsFragment  fragment;
	private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
	private List<String> arrayAdapter;
	private ListAdapter adapter;
	private List<BluetoothDevice> devices; 
	String deviceAdress = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings); 
		ApplicationsSettingsFragment settingsFRagment = new ApplicationsSettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, settingsFRagment  ,"settings").commit(); 
        
        arrayAdapter = new ArrayList<String>();
        devices = new ArrayList<BluetoothDevice>();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
		
	}
	
    @Override
    public void onResume() {
    	
    	fragment = (ApplicationsSettingsFragment) getSupportFragmentManager().findFragmentByTag("settings");
    	boolean s1,s2,s3;
    	s1 = MainApplication.settingsSwitchOnline;
    	s2 = MainApplication.settingsSwitchLocation;
    	s3 = MainApplication.settingsSwitchWATChiT;
    	fragment.updateView(s1, s2,s3 );
    	
    	super.onResume();
    	
    	// Register the BroadcastReceiver
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    	IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
    	IntentFilter filter4 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    	registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    	registerReceiver(mReceiver, filter2); // Don't forget to unregister during onDestroy
    	registerReceiver(mReceiver, filter3); // Don't forget to unregister during onDestroy
    	registerReceiver(mReceiver, filter4); // Don't forget to unregister during onDestroy
    	 
    }
	//
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            Log.d("device found", "device name : " + device.getName());
	            if (!MainApplication.bluetoothDevices.contains(device)) {
	            	MainApplication.bluetoothDevices.add(device);
	            devices.add(device);
	            }
	            	
	            //Toast.makeText(getBaseContext(), "Device found!", Toast.LENGTH_SHORT).show();
	        }
	        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	        	int i = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0 );
	        	if (i == BluetoothAdapter.STATE_ON) {
	        		Log.d("BLUETOOTH", "bluetooth on");	
	        	}
	        }
	        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	Log.d("discovery", "started searching");
	        	showProgress("BlueTooth" , "Scanning for devices..");
	        }
	        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	btAdapter.cancelDiscovery();
	        	dismissProgress();
	        	for (BluetoothDevice device : devices) {
					arrayAdapter.add(device.getName());
				}
	      
	        	adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);
					       
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				// Set other dialog properties
			    builder.setTitle("Choose Device");
			    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deviceAdress = devices.get(which).getAddress();
						Log.d("shiiiiit", devices.get(which).getAddress());
						Message message = Message.obtain(null, LocalService.MSG_DEVICE_NAME);
						Bundle b = new Bundle();
						b.putString("btDevice", deviceAdress);
						b.putInt("btdevicepos", which);
						message.setData(b);
						sendMessageToService(message);
					}
				});
				// Create the AlertDialog
				AlertDialog dialog = builder.create();
				dialog.show();
	        }
	    }
	};

	@Override
	public void onlineModeClicked(boolean on) {
		if (on) {
			if ( UtilityClass.isConnectedToInternet(getBaseContext())) {
				//TODO: Change spacehandler and datahandler to online mode, log in user to spaces.
				//Set "hasLoggedIn" to true
				
				//TODO: Change handlers to online mode man.
				MainApplication.settingsSwitchOnline = true;
				
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
		    	               MainApplication.settingsSwitchOnline = false;
		    	             
		    	           }
		    	       });
		    	
		    	builder.setMessage("You need internet access to use online mode.")
		        .setTitle("Internet");
		    
		    	// Create the AlertDialog
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 
			}
		
		} else {
			//TODO: Change handlers to offline mode maaaan.
			MainApplication.settingsSwitchOnline = false;
			
		}
		fragment.updateView(MainApplication.settingsSwitchOnline, MainApplication.settingsSwitchLocation, MainApplication.settingsSwitchWATChiT);
	}

	@Override
	public void WATCHiTSwitchClicked(boolean on) {
		if (on) {
			
			if (!btAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivity(enableBtIntent);
				MainApplication.settingsSwitchWATChiT = false;
				return;
			} else {
				service.start();
				MainApplication.settingsSwitchWATChiT = true;
				Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
    			// If there are paired devices
    			if (pairedDevices.size() > 0) {
    			    // Loop through paired devices
    			    for (BluetoothDevice device : pairedDevices) {
    			        // Add the name and address to an array adapter to show in a ListView
    			       arrayAdapter.add(device.getName() + "\n" + device.getAddress());
    			       devices.add(device);
    			      
    			    }
    			    MainApplication.bluetoothDevices = devices;
    			}
    			
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		
        		Log.d("SETTINGSACTIVITY", "adapter size : " + arrayAdapter.size());
        		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);
        		
        		   builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
   					@Override
   					public void onClick(DialogInterface dialog, int which) {
   						
   						deviceAdress = devices.get(which).getAddress();
   						Log.d("shiiiiit", devices.get(which).getAddress());
   						Message message = Message.obtain(null, LocalService.MSG_DEVICE_NAME);
   						Bundle b = new Bundle();
   						b.putString("btDevice", deviceAdress);
   						b.putInt("btdevicepos", which);
   						message.setData(b);
   						sendMessageToService(message);
   					}
   				});
        		
        		   
		    	// Add the buttons
		    	builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   arrayAdapter.clear();
		    	        	   devices.clear();
		    	        	   MainApplication.bluetoothDevices.clear();
		   	        		btAdapter.startDiscovery();
		    	           }
		    	       });
		    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	               MainApplication.settingsSwitchWATChiT = false;
		    	               fragment.updateView(false);
		    	               devices.clear();
		    	               MainApplication.bluetoothDevices.clear();
		    	               arrayAdapter.clear();
		    	           }
		    	       });
		    	
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 		
        	}
		}
			
		if (!on) {
			if (service.isRunning()) service.stop();
			showToast("Stopped WATCHiT sync...");
			MainApplication.settingsSwitchWATChiT = false;
		}
		
		 fragment.updateView(MainApplication.settingsSwitchOnline, MainApplication.settingsSwitchLocation, MainApplication.settingsSwitchWATChiT);
		
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
		    	               MainApplication.settingsSwitchLocation = false;
		    	           }
		    	       });
		    	
		    	builder.setMessage("You need to enable gps to use the location")
		        .setTitle("GPS");
		    
		    	// Create the AlertDialog
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 
		    	
		    } else {
		    	//gps er på..:
		        //TODO: Now use latitude and longitude in dataobjects.
		    	MainApplication.settingsSwitchLocation = true;
		    }
	    	
	    } else {
	    	//TODO: Do not want to use location. Stop adding location to dataobject or just add mocked location data?
	    	MainApplication.settingsSwitchLocation = false;
	    	
	    }
	    fragment.updateView(MainApplication.settingsSwitchOnline, MainApplication.settingsSwitchLocation, MainApplication.settingsSwitchWATChiT);
	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	
}
