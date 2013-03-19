package activities;


import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import listeners.OnlineModeChangeListener;
import listeners.SpaceChangeListener;
import listeners.WATCHiTConnectionChangeListener;
import parsing.GenericSensorData;
import parsing.Parser;
import service.LocationService;
import service.ServiceManager;
import service.WATCHiTService;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import no.ntnu.emergencyreflect.R;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dialogs.ChooseBlueToothDeviceDialog.BlueToothSelectListener;
import dialogs.ChooseEventDialog;
import dialogs.LocationDialog;
import dialogs.LocationDialog.LocationDialogListener;
import dialogs.ChooseBlueToothDeviceDialog;
import dialogs.OnlineModeDialog;
import dialogs.OnlineModeDialog.OnlineModeDialogListener;
import enums.SharedPreferencesNames;
import fragments.AppsFragment.OnAppItemSelectedListener;
import fragments.ProfileFragment;
import fragments.AppsFragment;
import fragments.StatusFragment.StatusChangeListener;
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
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;
import asynctasks.AuthenticateUserTask;
import asynctasks.ConnectToBluetoothTask;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

/**
 * Gateway application.
 * @author oivindth
 *
 */
public class GatewayActivity extends BaseActivity implements OnAppItemSelectedListener, DataObjectListener, StatusChangeListener, OnlineModeDialogListener, LocationDialogListener, BlueToothSelectListener, SpaceChangeListener  {

	private StatusFragment statusFragment;
	private AppsFragment spaceFragment;
	private ProfileFragment profileFragment;
	private BluetoothAdapter btAdapter;
	private ArrayList<String> arrayAdapter;
	private ListAdapter adapter;
	private List<BluetoothDevice> devices; 
	private WATCHiTConnectionChangeListener wcListener;
	private OnlineModeChangeListener onlinemodeListener;
	private GatewayActivity mActivity;
	private Handler handler;
	private GenericSensorData gsdata;
	

	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		mActivity = this;
		devices = new ArrayList<BluetoothDevice>();
		statusFragment = new StatusFragment();
		spaceFragment = new AppsFragment();
		profileFragment = new ProfileFragment();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		arrayAdapter = new ArrayList<String>();
	
		ActionBar bar = getSupportActionBar();

		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tab1 = bar.newTab();
		ActionBar.Tab tab2 = bar.newTab();
		ActionBar.Tab tab3 = bar.newTab();
		tab1.setText("Dashboard");
		tab2.setText("Apps");
		tab3.setText("Profile");
		tab1.setTabListener(new MyTabListener());
		tab2.setTabListener(new MyTabListener());
		tab3.setTabListener(new MyTabListener());
		bar.addTab(tab1);
		bar.addTab(tab2);
		bar.addTab(tab3);
		
		
		handler = new Handler(Looper.getMainLooper());
		
		createAndHandleWATCHiTService();
		createAndHandleLocationService();
		sApp.dataHandler.addDataObjectListener(this);
		
		if (UtilityClass.isConnectedToInternet(getBaseContext())) {
			if (sApp.connectionHandler.getStatus() == ConnectionStatus.OFFLINE) {
				Log.d("Main",  " username: " + sApp.getUserName() + "  password: " + sApp.getPassword());
				new AuthenticateUserTask(this, sApp.getUserName(), sApp.getPassword()).execute();
			} else {
				new GetSpacesTask(this).execute();
			}
		} else {
			new GetSpacesTask(this).execute();
		}
		
		
		wcListener = new WATCHiTConnectionChangeListener() {
			@Override
			public void onWATCHiTConnectionChanged(boolean on) {
				statusFragment.updateView(sApp.OnlineMode, on, sApp.isLocationOn);
				dismissProgress();
			}
		};
		sApp.addListener(wcListener);
		
		onlinemodeListener = new OnlineModeChangeListener() {
			
			@Override
			public void onOnlineModeChanged(boolean on) {
				try {
					statusFragment.updateOnlineView(on);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		};
		sApp.addOnlineModeChangeListener(onlinemodeListener);
		
		
		if (sApp.currentActiveSpace != null) {
			try {
				sApp.dataHandler.registerSpace(sApp.currentActiveSpace.getId());
			} catch (UnknownEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
				ft.replace(android.R.id.content, profileFragment);
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
		// If phone doesen't have google play services installed user is prompted to install it.
				// Or else he/she can't use the Google Maps.
				int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
				if (resultCode == ConnectionResult.SUCCESS) {
					//proceed as normal
				} else {
					GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
				}
		
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
			//finish();
			return true;

		case R.id.menu_sync:
			if (sApp.currentActiveSpace == null) {
			showToast("Please register with an event first...");
			return true;
			}
			new GetDataFromSpaceTask(this , sApp.currentActiveSpace.getId()).execute();
			// new GetSpacesTask().
			return true;
			
		case R.id.menu_logout:
			SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			sApp.setOnlineMode(Mode.OFFLINE);
			sApp.connectionHandler.disconnect();
			editor.putBoolean("hasLoggedIn", false);
			editor.commit();
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.menu_mock_watchit_data:
			DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
					("Person found!", String.valueOf(sApp.getLatitude()) , String.valueOf(sApp.getLongitude()) ), 
					sApp.connectionHandler.getCurrentUser().getBareJID(), sApp.connectionHandler.getCurrentUser().getUsername());
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
		try {
			sApp.service.unbind();
			sApp.locationService.unbind();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	

			public void handleDataObject(de.imc.mirror.sdk.DataObject dataObject,
					String spaceId) {
				String objectId = dataObject.getId();
				//Looper.prepare();
				
				Log.d("dataObject: ", "Received object " + objectId + " from space " + spaceId);
				Log.d("dataobject: ", dataObject.toString());
				try {
					//sApp.dataObjects.add(dataObject);
					GenericSensorData data = Parser.buildSimpleXMLObject((DataObject) dataObject);
					Log.d("data 2k", data.toString());
					
					gsdata = data;
					
					sApp.genericSensorDataObjects.add(data);
					
					handler.post(new Runnable(){

						@Override
						public void run() {
							try {
								//TODO: Notification..
								 Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
								  v.vibrate(300); 
								  
								    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
							        r.play();
								
								Toast.makeText(getApplicationContext(), "Object receieved", Toast.LENGTH_LONG).show();
								sApp.latest = " " +gsdata.getCreationInfo().getPerson() + " :  " + gsdata.getValue().getText();
								statusFragment.updateTextViewLatesInfo(sApp.latest);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}  
					});
					
					
					
				} catch (Exception e) {
					e.printStackTrace();	
				}
				
			}
		
	

	@Override
	public void onlineModeClicked(boolean on) {
		if (on) {
			if ( UtilityClass.isConnectedToInternet(getBaseContext()) ) {
				if (sApp.connectionHandler.getStatus() == ConnectionStatus.OFFLINE)
				new AuthenticateUserTask(this, sApp.getUserName(), sApp.getPassword()).execute();	
			}
			 else new OnlineModeDialog().show(getSupportFragmentManager(), "onlineModeDialog");	
		} else {
			sApp.setOnlineMode(Mode.OFFLINE);
			sApp.connectionHandler.disconnect();
		}
		statusFragment.updateOnlineView(sApp.OnlineMode);
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
				//sApp.isWATChiTOn = true;
				Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
				arrayAdapter = new ArrayList<String>();
				devices = new ArrayList<BluetoothDevice>();
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice device : pairedDevices) {
						arrayAdapter.add("Name: " + device.getName() + "\n" + " Adress: " + device.getAddress());
						devices.add(device);
						//sApp.arrayAdapter.add()
					}
					sApp.bluetoothDevices = devices;
				}
			    Bundle b = new Bundle();
				b.putStringArrayList("adapter", arrayAdapter);
				ChooseBlueToothDeviceDialog dialog = new ChooseBlueToothDeviceDialog();
				dialog.setArguments(b);
				dialog.show(getSupportFragmentManager(), "pairedbluetoothdevices");
			}
		}
		if (!on) {
			if (sApp.service.isRunning()) sApp.service.stop();
			showToast("Stopped WATCHiT sync...");
			sApp.isWATChiTOn= false;
		}
		statusFragment.updateWATCHiTView(sApp.isWATChiTOn);
	}
	
	/**
	 * Set up the WATCHiT Service for communicating with WATCHiT
	 */
	private void createAndHandleWATCHiTService() {

		// Create a service and handle incoming messages
		//TODO: Make handler static to avoid (potential) leaks?
		sApp.service = new ServiceManager(getBaseContext(), WATCHiTService.class, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// Receive message from service
				switch (msg.what) {
				/*
				case WATCHiTService.MSG_CONNECTION_ESTABLISHED:
					sApp.isWATChiTOn = true;
					Log.d("MainActivity Handler ", "Connection established message receieved from service.");
					showToast("Connection to WATCHiT established..");
					sApp.broadcastConnectionChange(true);
					
					break;
				case WATCHiTService.MSG_CONNECTION_FAILED:
					//dismissProgress();
					sApp.isWATChiTOn = false;
					Log.d("MainDashBoardActivityHandler: ", " Connection failed");
					showToast(" Failed to connect to WATCHiT....");
					sApp.broadcastConnectionChange(false);
					break;
					*/
				case WATCHiTService.MSG_CONNECTION_LOST:
					Log.d("MainDashBoardActivity", "Lost connection with WATChiT...");
					showToast("Warning: Lost connection with WATCHiT!");
					sApp.isWATChiTOn = false;
					sApp.broadcastWATCHiTConnectionChange(false);
					break;
				case WATCHiTService.MSG_SET_STRING_VALUE_TO_ACTIVITY: 
					String data = msg.getData().getString("watchitdata");
					//textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
					Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
					String lat = String.valueOf(sApp.getLatitude());
					String lot = String.valueOf(sApp.getLongitude());
					GenericSensorData gsd = Parser.buildSimpleXMLObject(data, lat , lot);
					String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain(); 
					DataObject dataObject = Parser.buildDataObjectFromSimpleXMl(gsd, jid, sApp.connectionHandler.getCurrentUser().getUsername());
					//new CreateSpaceTask().execute();
					new PublishDataTask(dataObject, sApp.currentActiveSpace.getId()).execute();
					Toast.makeText(getBaseContext(), "WATCHiT Dat: " + data, Toast.LENGTH_SHORT).show();
					Log.d("watchitdata:  ", data);
					break;
				default:
					super.handleMessage(msg);
				} 
			}
		});
	}

	
	/**
	 * Sets up the Location Service for GPS updates.
	 */
	private void createAndHandleLocationService() {
		//LocationService.
		sApp.locationService = new ServiceManager(getBaseContext(), LocationService.class, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// Receive message from service
				switch (msg.what) {
				case LocationService.MSG_UPDATE_LOCATION:
					Log.d("MainDashBoardActivity", "receieved location update..");
					double longitude = msg.getData().getDouble("longitude");
					double latitude = msg.getData().getDouble("latitude");
					sApp.setLongitude(longitude);
					sApp.setLatitude(latitude);
					
					showToast("New location: " + " long:  " + longitude + " lat: " + latitude );
					
					//locationListener.onLocationChanged(latitude, longitude);
					
					break;

				} 
			}
		});
	}

	@Override
	public void locationMode(boolean on) { 
		LocationManager locationManager =
				(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (on) {
			if (!gpsEnabled || !networkEnabled) {
				new LocationDialog().show(getSupportFragmentManager(), "locationdialog");
			} else {
				if (!sApp.locationService.isRunning()) sApp.locationService.start();
				sApp.isLocationOn = true;
				showToast("Location enabled.");
			}
		} else {
			if (sApp.locationService.isRunning()) sApp.locationService.stop();
			showToast("Stopped location service...");
			sApp.isLocationOn= false;
		}
		statusFragment.updateLocationView(sApp.isLocationOn);
	}

	@Override
	public void onOKChooseOnlineSourceDialogButtonClick() {
		enableSettings(Settings.ACTION_DATA_ROAMING_SETTINGS);
	}

	@Override
	public void onOKChooseNetworkForLocationClick() {
		enableSettings(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	}

	@Override
	public void blueToothDeviceSelected(int which) {
		Log.d("frack", "bluetoothdevice frack frack frack");
		new ConnectToBluetoothTask(this, which).execute();
	}

	@Override
	public void startBlueToothDiscovery() {
		arrayAdapter.clear();
		devices.clear();
		sApp.bluetoothDevices.clear();
		btAdapter.startDiscovery();
	}
	
	@Override
	public void cancelBluetoothPairedDialog() {
		sApp.isWATChiTOn = false;
		statusFragment.updateWATCHiTView(false);
		devices.clear();
		sApp.bluetoothDevices.clear();
		arrayAdapter.clear();
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
				if (!sApp.bluetoothDevices.contains(device)) sApp.bluetoothDevices.add(device);
				if (!devices.contains(device)) devices.add(device);
					
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
					arrayAdapter.add("Name: " +device.getName() + "\n" + " Adress: " + device.getAddress());
				}

				adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				// Set other dialog properties
				builder.setTitle("Choose Device");
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ConnectToBluetoothTask(mActivity, which).execute();
					}
				});
				// Create the AlertDialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	};

	@Override
	public void eventChangeClick(boolean on) {
		if (on) {
			Bundle b = new Bundle();
			ArrayList<String> arrayadapter = new ArrayList<String>();
			for (Space space : sApp.spacesInHandler) {
				arrayadapter.add(" " + space.getName());
			}
			b.putStringArrayList("adapter", arrayadapter);
			ChooseEventDialog dialog = new ChooseEventDialog();
			dialog.setArguments(b);
			dialog.show(getSupportFragmentManager(), "choose_event_dialog");

			
		} else {
			sApp.eventConnected = false;
			sApp.currentActiveSpace = null;
		}
		statusFragment.updateEventView(sApp.eventConnected);
	}

	@Override
	public void onSpaceChanged(int position) {
		sApp.eventConnected = true;
		showProgress("Event", "Syncing...");
		Space space = sApp.spacesInHandler.get(position);
		//switch space
		sApp.currentActiveSpace = space;
		//app.dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
		new GetDataFromSpaceTask(this, space.getId()).execute(); //TODO: To heavy?
		showToast("Noe registered to event: " + space.getName());
		statusFragment.updateEventView(true);
	}

	@Override
	public void onAppItemSelected(int position) {
		//getSupportFragmentManager().beginTransaction().replace(android.R.id.content, statusFragment).commit();
		
		
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
		
	}

	@Override
	public void onCancelChangeSpaceClicked() {
		sApp.eventConnected = false;
		statusFragment.updateEventView(false);
		
	}
	
}
