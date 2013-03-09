package activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.R;
import parsing.Parser;
import service.WATCHiTService;


import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;
import fragments.ApplicationsSettingsFragment;
import fragments.ApplicationsSettingsFragment.ApplicationsSettingsFfragmentListener;

import Utilities.UtilityClass;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import asynctasks.AuthenticateUserTask;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

/**
 * {@GATE Deprecated}
 * Activity containing a fragment where settings for online mode, location(gps) and watchit can be enabled
 * @author oivindth
 *
 */
public class SettingsActivity extends BaseActivity implements ApplicationsSettingsFfragmentListener {


	private ApplicationsSettingsFragment  fragment;
	private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
	private List<String> arrayAdapter;
	private ListAdapter adapter;
	private List<BluetoothDevice> devices; 
	String deviceAdress = "";
	private BluetoothAdapter btAdapter;
	private String bluetoothDeviceName;
	private BluetoothDevice device;
	private BluetoothSocket btSocket;
	
	
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
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	menu.add("Save")
        .setIcon(R.drawable.ic_navigation_refresh)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    menu.add("Switch User")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    	
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
    	switch (item.getItemId()) {
    	
    		case android.R.id.home:
    			// iff up instead of back:
              //  intent = new Intent(this, MainActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               // startActivity(intent);
    			
    			//just back use only finish():
                finish();
                return true;
    	
            case R.id.menu_sync:
            	new GetSpacesTask(this).execute();
            	sApp.dataHandler.setMode(Mode.ONLINE);
            	sApp.dataHandler.addDataObjectListener(myListener);
            	new GetDataFromSpaceTask(this ,"team#38").execute();
            	DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
            			("HelloWorld", "44.84866", "10.30683"), "admin" + "@" + sApp.connectionHandler.getConfiguration().getDomain());
            	Log.d("BASEACTIVITY", dob.toString());
            	new PublishDataTask(this, dob, "team#38").execute();
            	  
            	//showProgress("...", "zz");
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
    
    @Override
    public void onResume() {
    	
    	fragment = (ApplicationsSettingsFragment) getSupportFragmentManager().findFragmentByTag("settings");
    	boolean s1,s2,s3;
    	s1 = sApp.OnlineMode;
    	s2 = sApp.isLocationOn;
    	s3 = sApp.isWATChiTOn;
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
	            if (!sApp.bluetoothDevices.contains(device)) {
	            	sApp.bluetoothDevices.add(device);
	            devices.add(device);
	            }
	            	
	        }
	        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	        	int i = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0 );
	        	if (i == BluetoothAdapter.STATE_ON) {
	        		Log.d("BLUETOOTH", "bluetooth on");	
	        		showToast("Bluetooth is now enabled. You can now sync with WATCHiT");
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
						Message message = Message.obtain(null, WATCHiTService.MSG_DEVICE_NAME);
						Bundle b = new Bundle();
						b.putString("btDevice", deviceAdress);
						b.putInt("btdevicepos", which);
						message.setData(b);
						try {
							sApp.service.send(message);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
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
				new AuthenticateUserTask(this, sApp.getUserName(), sApp.getPassword()).execute();	
				
				
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
		    	               sApp.OnlineMode = false;
		    	             
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
			sApp.OnlineMode = false;
			sApp.setApplicationMode(Mode.OFFLINE);
			
			
		}
		fragment.updateView(sApp.OnlineMode, sApp.isLocationOn, sApp.isWATChiTOn);
	}

	@Override
	public void WATCHiTSwitchClicked(boolean on) {
		if (on) {
			
			if (!btAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivity(enableBtIntent);
				sApp.isWATChiTOn = false;
				return;
			} else {
				sApp.service.start();
				//service.start();
				sApp.isWATChiTOn = true;
				Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
    			// If there are paired devices
    			if (pairedDevices.size() > 0) {
    			    // Loop through paired devices
    			    for (BluetoothDevice device : pairedDevices) {
    			        // Add the name and address to an array adapter to show in a ListView
    			       arrayAdapter.add(device.getName() + "\n" + device.getAddress());
    			       devices.add(device);
    			      
    			    }
    			    sApp.bluetoothDevices = devices;
    			}
    			
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		
        		Log.d("SETTINGSACTIVITY", "adapter size : " + arrayAdapter.size());
        		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);
        		
        		   builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
   					@Override
   					public void onClick(DialogInterface dialog, int which) {
   						//showProgress("WATCHiT", "waiting for ack from WATCHiT");
   						deviceAdress = devices.get(which).getAddress();
   						Log.d("shiiiiit", devices.get(which).getAddress());
   						Message message = Message.obtain(null, WATCHiTService.MSG_DEVICE_NAME);
   						Bundle b = new Bundle();
   						b.putString("btDevice", deviceAdress);
   						b.putInt("btdevicepos", which);
   						message.setData(b);
   						sApp.sendMessageToService(message);
   					}
   				});
        		
        		   
		    	// Add the buttons
		    	builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   arrayAdapter.clear();
		    	        	   devices.clear();
		    	        	   sApp.bluetoothDevices.clear();
		   	        		btAdapter.startDiscovery();
		    	           }
		    	       });
		    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	               sApp.isWATChiTOn = false;
		    	               fragment.updateView(false);
		    	               devices.clear();
		    	               sApp.bluetoothDevices.clear();
		    	               arrayAdapter.clear();
		    	           }
		    	       });
		    	
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 		
        	}
		}
			
		if (!on) {
			if (sApp.service.isRunning()) sApp.service.stop();
			showToast("Stopped WATCHiT sync...");
			sApp.isWATChiTOn= false;
		}
		
		 fragment.updateView(sApp.OnlineMode, sApp.isLocationOn, sApp.isWATChiTOn);
		
	}

	@Override
	public void locationMode(boolean on) { 
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
		    	               sApp.isLocationOn = false;
		    	           }
		    	       });
		    	
		    	builder.setMessage("You need to enable gps to use the location")
		        .setTitle("GPS");
		    
		    	// Create the AlertDialog
		    	AlertDialog dialog = builder.create();
		    	dialog.show(); 
		    	
		    } else {
		    	//gps is on.
		        //TODO: Now use real latitude and longitude in dataobjects.
		    	sApp.isLocationOn = true;
		    }
	    	
	    } else {
	    	//TODO: Do not want to use location. Stop adding location to dataobject or just add mocked location data?
	    	sApp.isLocationOn = false;
	    	
	    }
	    fragment.updateView(sApp.OnlineMode, sApp.isLocationOn, sApp.isWATChiTOn);
	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	
}
