package activities;
/**
 * Copyrigth 2013 ¯ivind Thorvaldsen
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import listeners.OnlineModeChangeListener;
import listeners.SpaceChangeListener;
import listeners.WATCHiTConnectionChangeListener;
import parsing.GenericSensorData;
import parsing.Parser;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import no.ntnu.emergencyreflect.R;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dialogs.ChooseBlueToothDeviceDialog.BlueToothSelectListener;
import dialogs.LocationDialog;
import dialogs.LocationDialog.LocationDialogListener;
import dialogs.ChooseBlueToothDeviceDialog;
import dialogs.ChooseEventDialog;
import dialogs.OnlineModeDialog;
import dialogs.OnlineModeDialog.OnlineModeDialogListener;
import fragments.ConfigFragment.StatusChangeListener;
import fragments.ProfileFragment;
import fragments.ConfigFragment;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import asynctasks.AuthenticateUserTask;
import asynctasks.ConnectToBluetoothTask;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;

/**
 * Gateway application.
 *
 */
public class GatewayActivity extends BaseActivity implements DataObjectListener, StatusChangeListener, OnlineModeDialogListener, LocationDialogListener, BlueToothSelectListener, SpaceChangeListener  {

	private StatusFragment statusFragment;
	private ConfigFragment configFragment;
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

	private de.imc.mirror.sdk.DataObject lastDataObject;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mActivity = this;
		devices = new ArrayList<BluetoothDevice>();
		statusFragment = new StatusFragment();
		configFragment = new ConfigFragment();
		profileFragment = new ProfileFragment();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		arrayAdapter = new ArrayList<String>();

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tab1 = bar.newTab();
		ActionBar.Tab tab2 = bar.newTab();
		tab1.setText(getString(R.string.gateway_tab_status));
		tab2.setText(getString(R.string.gateway_tab_config));
		tab1.setTabListener(new MyTabListener());
		tab2.setTabListener(new MyTabListener());
		bar.addTab(tab1);
		bar.addTab(tab2);

		if (sApp.needsRecreation()) {
			//sApp.reênitializeHandlers();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}


		handler = new Handler(Looper.getMainLooper());
		sApp.dataHandler.addDataObjectListener(this);
		wcListener = new WATCHiTConnectionChangeListener() {
			@Override
			public void onWATCHiTConnectionChanged(boolean on) {
				configFragment.updateView(sApp.OnlineMode, on, sApp.isLocationOn);
				dismissProgress();
			}
		};
		sApp.addListener(wcListener);

		onlinemodeListener = new OnlineModeChangeListener() {	
			@Override
			public void onOnlineModeChanged(boolean on) {
				try {
					configFragment.updateOnlineView(on);
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
				ft.replace(android.R.id.content, configFragment);
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
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_sync:
			if (sApp.currentActiveSpace == null) {
				new GetSpacesTask(this).execute();
				showToast(getString(R.string.toast_event_first));
				return true;
			}
			new GetSpacesTask(this).execute();
			new GetDataFromSpaceTask(this , sApp.currentActiveSpace.getId()).execute();
			return true;
		case R.id.menu_changeSpace:
			new ChooseEventDialog().show(getSupportFragmentManager(), "chooseEventDialog");
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
		super.onDestroy();
		unregisterReceiver(mReceiver);
		sApp.dataHandler.removeDataObjectListener(this);	

	}
	public void handleDataObject(de.imc.mirror.sdk.DataObject dataObject,
			String spaceId) {
		//String objectId = dataObject.getId();
		//hack because sdk is f***. We don't want a notification from a space we currently are not registered to. 
		//also need a hack for the datahandler listener who randomly sends to many objects.
		if (!sApp.currentActiveSpace.getId().equals(spaceId)) return;
		if (lastDataObject!= null) {
			if (lastDataObject.getId().equals(dataObject.getId()) ) return;
		}
		lastDataObject = dataObject;

		//Log.d("dataobject: ", dataObject.toString());
		try {
			//sApp.dataObjects.add(dataObject);
			GenericSensorData data = Parser.buildSimpleXMLObject((DataObject) dataObject);
			gsdata = data;
			handler.post(new Runnable(){
				@Override
				public void run() {
					try {
						sApp.latest = " " +gsdata.getCreationInfo().getPerson() + " :  " + gsdata.getValue().getText();
						statusFragment.updateTextViewLatesInfo(sApp.latest);
						statusFragment.updateTextViewEvent(getString(R.string.members) + " :" + sApp.currentActiveSpace.getMembers().size() + "\n" + getString(R.string.data) + " :" + sApp.genericSensorDataObjects.size());
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
		configFragment.updateOnlineView(sApp.OnlineMode);
	}

	@Override
	public void WATCHiTSwitchClicked(boolean on) {
		if (on) {
			if (sApp.currentActiveSpace == null) {
				showToast(getString(R.string.register_event_first));
				configFragment.updateWATCHiTView(false);
				return;
			}
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
						arrayAdapter.add("Name: " + device.getName() + "\n" + getString(R.string.adress) + " :" + device.getAddress());
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
			showToast(getString(R.string.toast_stopped_watchit_service));
			sApp.isWATChiTOn= false;
		}
		configFragment.updateWATCHiTView(sApp.isWATChiTOn);
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
				configFragment.updateLocationView(false);
				sApp.isLocationOn = false;
			} else {
				if (!sApp.locationService.isRunning()) sApp.locationService.start();
				sApp.isLocationOn = true;
				showToast(getString(R.string.location_enabled));
			}
		} else {
			if (sApp.locationService.isRunning()) sApp.locationService.stop();
			showToast(getString(R.string.toast_stopped_location_service));
			sApp.isLocationOn= false;
		}
		configFragment.updateLocationView(sApp.isLocationOn);
	}

	@Override
	public void onOKChooseOnlineSourceDialogButtonClick() {
		//enableSettings(Settings.ACTION_DATA_ROAMING_SETTINGS);
		try {
			enableSettings(Settings.ACTION_WIRELESS_SETTINGS);
		} catch (Exception e) {
			e.printStackTrace();
			showToast(getString(R.string.toast_enable_wireless));
		}
	}

	@Override
	public void onOKChooseNetworkForLocationClick() {
		enableSettings(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	}

	@Override
	public void blueToothDeviceSelected(int which) {
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
		configFragment.updateWATCHiTView(false);
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
				if (!sApp.bluetoothDevices.contains(device)) sApp.bluetoothDevices.add(device);
				if (!devices.contains(device)) devices.add(device);

			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				int i = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0 );
				if (i == BluetoothAdapter.STATE_ON) {
					showToast(getString(R.string.toast_bluetooth_enabled));
				}
			}
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				showProgress(getString(R.string.progress_title_bluetooth) , getString(R.string.progress_message_bluetooth));
			}
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				btAdapter.cancelDiscovery();
				dismissProgress();
				for (BluetoothDevice device : devices) {
					arrayAdapter.add(getString(R.string.name) + " :" +device.getName() + "\n" + getString(R.string.adress) +" :" + device.getAddress());
				}

				adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				// Set other dialog properties
				builder.setTitle(getString(R.string.choose_device_dialog_title));
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
	public void onSpaceChanged(int position) {
		if (position == -1) return;
		//if (sApp.dataHandler.getHandledSpaces().contains(sApp.currentActiveSpace)) {
		//sApp.dataHandler.removeSpace(sApp.currentActiveSpace.getId());
		//}
		Space space = sApp.spacesInHandler.get(position);
		sApp.currentActiveSpace = space;
		statusFragment.updateTextViewEventLed(space.getName());
		statusFragment.updateEventLED(true);
		new GetDataFromSpaceTask(this, space.getId()).execute(); 
	}

	@Override
	public void onDataFetchedFromSpace() {
		Space space = sApp.currentActiveSpace;
		//String toDisplay = UtilityClass.getStringDataFromDataObjects(sApp.genericSensorDataObjects);
		statusFragment.updateTextViewEvent(getString(R.string.members) + space.getMembers().size() + "\n" + getString(R.string.data) + sApp.genericSensorDataObjects.size() + 
				"\n " );
	}

	@Override
	public void onCancelLocationDialogClick() {
		configFragment.updateLocationView(false);
		sApp.isLocationOn = false;
	}

}
