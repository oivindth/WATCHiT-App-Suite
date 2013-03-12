package activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;

import parsing.GenericSensorData;
import parsing.Parser;

import service.WATCHiTService;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.R;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dialogs.ServerSettingsDialog;
import fragments.ApplicationsSettingsFragment;
import fragments.SpacesFragment;
import fragments.ApplicationsSettingsFragment.ApplicationsSettingsFfragmentListener;
import fragments.SpacesFragment.OnSpaceItemSelectedListener;
import fragments.StatusFragment;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;
import asynctasks.AuthenticateUserTask;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

/**
 * Gateway application.
 * @author oivindth
 *
 */
public class GatewayActivity extends BaseActivity implements OnSpaceItemSelectedListener, ApplicationsSettingsFfragmentListener  {

	private StatusFragment statusFragment;
	private SpacesFragment spaceFragment;
	private ApplicationsSettingsFragment settingsFragment;
	private BluetoothAdapter btAdapter;
	private List<String> arrayAdapter;
	private ListAdapter adapter;
	private List<BluetoothDevice> devices; 
	private String deviceAdress = "";


	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		devices = new ArrayList<BluetoothDevice>();
		statusFragment = new StatusFragment();
		spaceFragment = new SpacesFragment();
		settingsFragment = new ApplicationsSettingsFragment();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		arrayAdapter = new ArrayList<String>();

		ActionBar bar = getSupportActionBar();

		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tab1 = bar.newTab();
		ActionBar.Tab tab2 = bar.newTab();
		ActionBar.Tab tab3 = bar.newTab();
		tab1.setText("Status");
		tab2.setText("Events");
		tab3.setText("Config");
		tab1.setTabListener(new MyTabListener());
		tab2.setTabListener(new MyTabListener());
		tab3.setTabListener(new MyTabListener());
		bar.addTab(tab1);
		bar.addTab(tab2);
		bar.addTab(tab3);


	}

	private class MyTabListener implements ActionBar.TabListener
	{
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			int tabPosition = tab.getPosition();
			switch (tabPosition) {
			case 0:
				ft.replace(android.R.id.content, statusFragment);
				break;
			case 1:
				ft.replace(android.R.id.content, spaceFragment);
				break;
			case 2:
				ft.replace(android.R.id.content, settingsFragment);
				break;
			default:
				break;
			}

		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
	}



	@Override
	public void onResume() {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_gateway, menu);
		return super.onCreateOptionsMenu(menu); 
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
			//if (sApp.currentActiveSpace == null) {
			//showToast("Please register with an event first...");
			//return true;
			//}
			new GetDataFromSpaceTask(this , sApp.currentActiveSpace.getId()).execute();
			return true;
		case R.id.menu_mock_watchit_data:
			DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
					("Hello World", "44.84866", "10.30683"), sApp.connectionHandler.getCurrentUser().getBareJID());
			new PublishDataTask(this, dob, sApp.currentActiveSpace.getId()).execute();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy () {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onSpaceItemSelected(int position) {
		//Space space = app.spaceHandler.getAllSpaces().get(position); //TODO: Too heavy.
		Space space = sApp.spacesInHandler.get(position);
		//switch space
		sApp.currentActiveSpace = space;
		
		try {
			sApp.dataHandler.registerSpace(space.getId());
		} catch (UnknownEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//app.dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
		new GetDataFromSpaceTask(this, space.getId()).execute(); //TODO: To heavy?
		showToast("Noe registered to event: " + space.getName());
		//finish();
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
			// if (LocationManager.)
		}
	};


	@Override
	public void onlineModeClicked(boolean on) {
		if (on) {
			if ( UtilityClass.isConnectedToInternet(getBaseContext())) {
				//if (sApp.connectionHandler.getStatus() == ConnectionStatus.ONLINE) 

				new AuthenticateUserTask(this, sApp.getUserName(), sApp.getPassword()).execute();	
				sApp.OnlineMode = true;

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
			sApp.OnlineMode = false;
			sApp.setApplicationMode(Mode.OFFLINE);
		}
		settingsFragment.updateView(sApp.OnlineMode, sApp.isLocationOn, sApp.isWATChiTOn);
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
				//sApp.isWATChiTOn = true;
				Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

				arrayAdapter = new ArrayList<String>();
				devices = new ArrayList<BluetoothDevice>();

				// If there are paired devices
				if (pairedDevices.size() > 0) {
					// Loop through paired devices
					for (BluetoothDevice device : pairedDevices) {
						// Add the name and address to an array adapter to show in a ListView
						arrayAdapter.add(device.getName());
						devices.add(device);

					}
					sApp.bluetoothDevices = devices;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				Log.d("SETTINGSACTIVITY", "adapter size : " + arrayAdapter.size());
				adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);

				builder.setTitle("Choose WATCHiT device: ");
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//showProgress("WATCHiT", "Connecting to WATCHiT....");
						deviceAdress = devices.get(which).getAddress();
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
						settingsFragment.updateView(false);
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
		settingsFragment.updateView(sApp.OnlineMode, sApp.isLocationOn, sApp.isWATChiTOn);
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
				if (!sApp.locationService.isRunning()) sApp.locationService.start();
				sApp.isLocationOn = true;
				showToast("Location enabled.");
			}

		} else {
			//TODO: Do not want to use location. Stop adding location to dataobject or just add mocked location data?
			if (sApp.locationService.isRunning()) sApp.locationService.stop();
			showToast("Stopped location service...");
			sApp.isLocationOn= false;

		}
		settingsFragment.updateView(sApp.OnlineMode, sApp.isLocationOn, sApp.isWATChiTOn);
	}
}
