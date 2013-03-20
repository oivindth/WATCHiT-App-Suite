package activities;


import no.ntnu.emergencyreflect.R;
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

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;


import Utilities.UtilityClass;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;
import asynctasks.AuthenticateUserTask;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;
import fragments.DashboardFragment;
import fragments.ProfileFragment;


public class MainActivity extends BaseActivity implements ActionBar.TabListener  {


	private DashboardFragment dashBoardFragment;
	private ProfileFragment profileFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		dashBoardFragment = new DashboardFragment();
		profileFragment = new ProfileFragment();
		
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab tab1 = bar.newTab();
		ActionBar.Tab tab2 = bar.newTab();
		tab1.setText("Dashboard");
		tab2.setText("Profile");
		tab1.setTabListener(this);
		tab2.setTabListener(this);
		bar.addTab(tab1);
		bar.addTab(tab2);
		
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
		
		
		createAndHandleWATCHiTService();
		createAndHandleLocationService();
		
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int tabPosition = tab.getPosition();
		switch (tabPosition) {
		case 0:
			ft.replace(android.R.id.content, dashBoardFragment);
			break;
		case 1:
			ft.replace(android.R.id.content, profileFragment);
			//break;
		default:
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
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
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			sApp.service.unbind();
			sApp.locationService.unbind();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case android.R.id.home:
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
		default:
			return super.onOptionsItemSelected(item);
		}
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
	
}
