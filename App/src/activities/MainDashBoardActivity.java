package activities;

import parsing.GenericSensorData;
import parsing.Parser;
import service.LocationService;
import service.ServiceManager;
import service.WATCHiTService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import no.ntnu.emergencyreflect.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.android.DataObject;


public class MainDashBoardActivity extends BaseActivity  {

	private DataObjectListener myListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboardlayout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		createAndHandleWATCHiTService();
		createAndHandleLocationService();
		setUpDashBoardGUI();
		createDataObjectListener();
		sApp.dataHandler.addDataObjectListener(myListener);
		
		new GetSpacesTask(this).execute();

		
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
			// iff up instead of back:
			//  intent = new Intent(this, MainActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);

			//just back use only finish():
			// finish();
			showToast("Main");
			return true;

		case R.id.menu_logout:
			SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			//Set "hasLoggedIn" to true
			editor.putBoolean("hasLoggedIn", false);
			editor.putString("username", "");
			editor.putString("password", "");
			// Commit the edits!

			editor.commit();

			//if (sApp.connectionHandler.getStatus() == ConnectionStatus.ONLINE) {
			//sApp.connectionHandler.disconnect();
			//}
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Set up the dataobject listener for handling published dataobjects. 
	 * Currently only works when published from own phone. Need to put this in a thread to make
	 * it work when other phones also publish?
	 */
	private void createDataObjectListener() {
		myListener = new DataObjectListener() {
			// implement this interface in a controller class of your application

			@Override
			public void handleDataObject(de.imc.mirror.sdk.DataObject dataObject,
					String spaceId) {
				String objectId = dataObject.getId();
				Log.d("¿l: ", "Received object " + objectId + " from space " + spaceId);
				Toast.makeText(getApplicationContext(), "Object receieved", Toast.LENGTH_SHORT).show();
			}
		};
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
				case WATCHiTService.MSG_CONNECTION_ESTABLISHED:
					sApp.isWATChiTOn = true;
					Log.d("MainActivity Handler ", "Connection established message receieved from service.");
					showToast("Connection to WATCHiT established..");
					//dismissProgress();
					break;
				case WATCHiTService.MSG_CONNECTION_FAILED:
					//dismissProgress();
					sApp.isWATChiTOn = false;
					Log.d("MainDashBoardActivityHandler: ", " Connection failed");
					showToast(" Failed to connect to WATCHiT....");

					break;
				case WATCHiTService.MSG_CONNECTION_LOST:
					Log.d("MainDashBoardActivity", "Lost connection with WATChiT...");
					showToast("Warning: Lost connection with WATCHiT!");
					sApp.isWATChiTOn = false;
					break;
				case WATCHiTService.MSG_SET_STRING_VALUE_TO_ACTIVITY: 
					String data = msg.getData().getString("watchitdata");
					//textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
					Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
					String lat = String.valueOf(sApp.getLatitude());
					String lot = String.valueOf(sApp.getLongitude());

					GenericSensorData gsd = Parser.buildSimpleXMLObject(data, lat , lot);
					String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain(); 
					DataObject dataObject = Parser.buildDataObjectFromSimpleXMl(gsd, jid);
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
					break;

				} 
			}
		});
	}
	
	/**
	 * Set up the dashboard gui.
	 */
	private void setUpDashBoardGUI() {
		// new GetSpacesTask().execute();
		Button buttonMap = (Button) findViewById(R.id.btn_map);
		Button buttonApp2 = (Button) findViewById(R.id.btn_app2);
		Button buttonGateway = (Button) findViewById(R.id.btn_gateway);
		Button buttonProfile = (Button) findViewById(R.id.btn_profile);
		TextView textview = (TextView) findViewById(R.id.footerTextView);

		// open webpage to mirror?
		textview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		buttonMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(getApplicationContext(), MapActivity.class);
				startActivity(i);
			}
		});
		buttonApp2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showToast("Not yet implemented.");
				//Intent i = new Intent(getApplicationContext(), );
				//startActivity(i);
			}
		});
		// Listening to Events button click
		buttonGateway.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), GatewayActivity.class);
				startActivity(i);
			}
		});
		buttonProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showToast("Profile with badges?");
				//Intent i = new Intent(getApplicationContext(), PhotosActivity.class);
				//startActivity(i);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//unregisterReceiver(mReceiver);
		try {
			sApp.service.unbind();
			sApp.locationService.unbind();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
