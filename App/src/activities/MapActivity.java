package activities;


import listeners.LayersChangeListener;
import listeners.LocationChangedListener;
import parsing.GenericSensorData;
import parsing.Parser;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.imc.mirror.sdk.DataObject;
import de.imc.mirror.sdk.DataObjectListener;
import dialogs.MapLayersDialog;
import enums.SharedPreferencesNames;
import enums.ValueType;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import asynctasks.PublishDataTask;


public class MapActivity extends BaseActivity implements LayersChangeListener {

	private GoogleMap mMap;
	private MainApplication sApp;
	private SharedPreferences mapPreferences;

	DataObjectListener listernerfri;

	private boolean moodLayerMode;
	private boolean personLayerMode;

	Handler handler;
	GenericSensorData data;
	LatLng latlng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		sApp = MainApplication.getInstance();
		mapPreferences = getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , MODE_PRIVATE );
		moodLayerMode = mapPreferences.getBoolean("mood_layer", false);
		personLayerMode = mapPreferences.getBoolean("person_layer", false);	

		handler = new Handler(Looper.getMainLooper());

		listernerfri = new DataObjectListener() {

			@Override
			public void handleDataObject(DataObject dataObject, String spaceId) {
				Log.d("here", "here??");

				data = Parser.buildSimpleXMLObject((de.imc.mirror.sdk.android.DataObject) dataObject);
				Double lat = Double.parseDouble(data.getLocation().getLatitude());
				Double lng = Double.parseDouble(data.getLocation().getLongitude());

				Log.d("listener map22:", "  lat :" + lat + "  long : " + lng);

				latlng = new LatLng(lat, lng);

				handler.post(new Runnable(){

					@Override
					public void run() {
						try {
							mMap.addMarker(new MarkerOptions()
							.position(latlng)
							.title("Person found"))
							.setSnippet("User: " + data.getPublisher()   + " \n  " + data.getTimestamp() );
						} catch (Exception e) {
							e.printStackTrace();
						}
					}  
				});
			}
		};
		sApp.dataHandler.addDataObjectListener(listernerfri);
	}

	@Override
	public void onResume () {
		super.onResume();
		setUpMapIfNeeded();	
		mMap.setMyLocationEnabled(true);

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker arg0) {
				showToast("Here we go to new fragment that displays more info with quiz button etc....");
			}
		});
	}


	@Override
	public void onPause() {
		super.onPause();
		// Save the state of the user choice of layers since the activity may be destroyed.
		SharedPreferences.Editor ed = mapPreferences.edit();
		//ed.putBoolean("mood_layer", moodLayerMode);
		//ed.putBoolean("person_layer", personLayerMode);
		//ed.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case android.R.id.home:
			// iff up instead of back:
			//intent = new Intent(this, MainActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(intent);
			finish();
			return true;

		case R.id.menu_sync:
			showToast("Sync it up");
			return true;

		case R.id.menu_layers:
			new MapLayersDialog().show(getSupportFragmentManager(), "layersdialog");
			return true;

		case R.id.menu_mock_watchit_data:
			DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
					("Person found!", String.valueOf(sApp.getLatitude()) , String.valueOf(sApp.getLongitude()) ), 
					sApp.connectionHandler.getCurrentUser().getBareJID(), sApp.connectionHandler.getCurrentUser().getUsername());
			new PublishDataTask(this, (de.imc.mirror.sdk.android.DataObject) dob, sApp.currentActiveSpace.getId()).execute();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setUpMapIfNeeded() {
		//Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			//  .getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				//The Map is verified. It is now safe to manipulate the map.
			}
		}
	}

	@Override
	public void onLayersChanged(boolean showPersons, boolean showMoods) {
		mMap.clear();
		if (showPersons) {
			Log.d("MapActivity", "size: " + sApp.genericSensorDataObjects.size());
			for (GenericSensorData data : sApp.genericSensorDataObjects) {
				Double lat = Double.parseDouble(data.getLocation().getLatitude());
				Double lng = Double.parseDouble(data.getLocation().getLongitude());
				LatLng latlng = new LatLng(lat, lng);
				mMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title("Person found"))
				.setSnippet("User: " + data.getPublisher()   + " \n  " + data.getTimestamp() );
			}
		}	 
	}

}
