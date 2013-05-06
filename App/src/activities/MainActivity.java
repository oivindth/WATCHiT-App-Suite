package activities;


import no.ntnu.emergencyreflect.R;
import parsing.GenericSensorData;
import parsing.Parser;
import parsing.Step;
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
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;
import dialogs.ChooseAvatarDialog.ChooseAvatarListener;
import Utilities.UtilityClass;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import asynctasks.AuthenticateUserTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;
import fragments.DashboardFragment;
import fragments.ProfileFragment;


public class MainActivity extends BaseActivity implements ActionBar.TabListener, ChooseAvatarListener, DataObjectListener  {


	private DashboardFragment dashBoardFragment;
	private ProfileFragment profileFragment;
	private Handler handler;
	private GenericSensorData data;
	
	private de.imc.mirror.sdk.DataObject lastDataObject;
	
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
		tab1.setText(getString(R.string.main_activity_tab_dasboard));
		tab2.setText(R.string.main_ativity_tab_profile);
		tab1.setTabListener(this);
		tab2.setTabListener(this);
		bar.addTab(tab1);
		bar.addTab(tab2);
		
		handler = new Handler(Looper.getMainLooper());
		
		if (sApp.needsRecreation()) {
			//sApp.reÍnitializeHandlers();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		if (UtilityClass.isConnectedToInternet(getBaseContext())) {
			if (sApp.connectionHandler.getStatus() == ConnectionStatus.OFFLINE) {
				sApp.OnlineMode = false;
				new AuthenticateUserTask(this, sApp.getUserName(), sApp.getPassword()).execute();
			} else {
				new GetSpacesTask(this).execute();
			}
		} else {
			new GetSpacesTask(this).execute();
		}
		sApp.dataHandler.addDataObjectListener(this);
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
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			sApp.service.unbind();
			sApp.locationService.unbind();
			sApp.connectionHandler.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sApp.dataHandler.removeDataObjectListener(this);
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
					//Log.d("MainDashBoardActivity", "Lost connection with WATChiT...");
					showToast(getString(R.string.toast_watchit_connection_lost));
					sApp.isWATChiTOn = false;
					sApp.broadcastWATCHiTConnectionChange(false);
					break;
				case WATCHiTService.MSG_SET_STRING_VALUE_TO_ACTIVITY: 
					String data = msg.getData().getString("watchitdata");
					//textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
					//Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
					
					String lat = String.valueOf(sApp.getLatitude());
					String lot = String.valueOf(sApp.getLongitude());
					GenericSensorData gsd = Parser.buildSimpleXMLObject(data, lat , lot);
					
					String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain(); 
					// If watchit data is note
					if (gsd.getValue().getType().equals("note")) {
						DataObject dataObject = Parser.buildDataObjectFromSimpleXMl(gsd, jid, sApp.connectionHandler.getCurrentUser().getUsername());
						new PublishDataTask(dataObject, sApp.currentActiveSpace.getId()).execute();
						Toast.makeText(getBaseContext(), "WATCHiT Data: " + data, Toast.LENGTH_SHORT).show();
					
						// If WATCHiT data is Steps.
					} else if (gsd.getValue().getType().equals("step")) {
						Step step = new Step(gsd.getValue().getText());
						sApp.steps.add(step); 
						sApp.broadCastStepsChanged(sApp.steps.size()-1);
						if (sApp.steps.size() == sApp.numberOfSteps) { //eller start stopp knapp i appen...? review hvordan det gikk så uploade? tror det er bedre.
							//GenericSensorDataTP gsdtp = Parser.buildSimpleXMLObject(sApp.steps);
							
						}
					}
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
					//Log.d("MainDashBoardActivity", "receieved location update..");
					double longitude = msg.getData().getDouble("longitude");
					double latitude = msg.getData().getDouble("latitude");
					sApp.setLongitude(longitude);
					sApp.setLatitude(latitude);
					
					//locationListener.onLocationChanged(latitude, longitude);
					break;
				} 
			}
		});
	}
	@Override
	public void avatarChosen(int which) {
		profileFragment.updateView(which);
	}

	@Override
	public void handleDataObject(de.imc.mirror.sdk.DataObject dataObject,
			String spaceId) {
		//String objectId = dataObject.getId();
		//Log.d("dataObject: ", "Received object " + objectId + " from space " + spaceId);
		//Log.d("dataobject: ", dataObject.toString());
		//hack because of sdk bug. We don't want a notification from a space we currently are not registered to. 
				if (!sApp.currentActiveSpace.getId().equals(spaceId)) return;
				//hack because dataobjectlistener in sdk is not working as expected.
				if (lastDataObject!= null) {
					if (lastDataObject.getId().equals(dataObject.getId()) ) return;
				}
				lastDataObject = dataObject;	
		try {
			//TODO: TP object sjekk....
			// må endre hver eneste handler for å sjekke om det er tp eller vanlig object? Bør lagres et felles sted.
			data = Parser.buildSimpleXMLObject((DataObject) dataObject);
			sApp.genericSensorDataObjects.add(data);
			handler.post(new Runnable(){
				@Override
				public void run() {
					try {
						vibrateAndSoundNotification();
						Toast.makeText(getApplicationContext(), getString(R.string.toast_new_data_published) + ": \n " + sApp.currentActiveSpace.getName() + " \n " + data.getCreationInfo().getPerson() , Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}  
			});
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
	
	private void vibrateAndSoundNotification () {
		//TODO: Notification..
		 Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
		  v.vibrate(300); 
		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	        r.play();
	}
	
}
