package activities;


import java.util.ArrayList;
import java.util.List;

import listeners.LayersChangeListener;
import parsing.GenericSensorData;
import parsing.Parser;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.imc.mirror.sdk.DataObject;
import de.imc.mirror.sdk.DataObjectListener;
import dialogs.MapLayersDialog;
import enums.SharedPreferencesNames;
import enums.ValueType;
import fragments.MoodFragment;
import fragments.PersonDetailsFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import asynctasks.PublishDataTask;


public class MapActivity extends BaseActivity implements LayersChangeListener {

	private GoogleMap mMap;
	private MainApplication sApp;
	private SharedPreferences mapPreferences;
	private List<Marker> markers;
	private List<GenericSensorData> tempDataObjects;
	
	private List<GenericSensorData> moods;
	private List<GenericSensorData> persons;
	
	
	DataObjectListener listernerfri;
	private MapActivity mActivity;
	
	
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
		mActivity = this;
		
		markers = new ArrayList<Marker>();
		tempDataObjects = new ArrayList<GenericSensorData>();
		
		handler = new Handler(Looper.getMainLooper());
		handleNewDataObjects();
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
				GenericSensorData data = null;
				for (Marker marker : markers) {
					if (arg0.equals(marker)) {
						data = tempDataObjects.get(markers.indexOf(marker));
					}
				}
				
				//String unit = data.getValue().getUnit();
				String unit ="";
				String value = data.getValue().getText();
				if (value.equals("I rescued someone")) {
					unit = "person";
				} else {
					unit = "mood";
				}
				
				Intent intent = new Intent(mActivity, MapMarkerDetailsActivity.class);
				intent.putExtra("unit", unit);
				intent.putExtra("user", data.getCreationInfo().getPerson());
				intent.putExtra("time", data.getTimestamp());
				intent.putExtra("value", data.getValue().getText());
				startActivity(intent);
				
			}
		});
	}


	private void handleNewDataObjects () {
		listernerfri = new DataObjectListener() {

			@Override
			public void handleDataObject(DataObject dataObject, String spaceId) {

				data = Parser.buildSimpleXMLObject((de.imc.mirror.sdk.android.DataObject) dataObject);
				Double lat = Double.parseDouble(data.getLocation().getLatitude());
				Double lng = Double.parseDouble(data.getLocation().getLongitude());

				Log.d("listener map:", "  lat :" + lat + "  long : " + lng);
				latlng = new LatLng(lat, lng);
				handler.post(new Runnable(){

					@Override
					public void run() {
						try {
							addMarker(data, " ");
							tempDataObjects.add(data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}  
				});
			}
		};
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
					("I rescued someone", String.valueOf(sApp.getLatitude()) , String.valueOf(sApp.getLongitude()) ), 
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
		markers.clear();
		tempDataObjects.clear();
			Log.d("MapActivity", "size: " + sApp.genericSensorDataObjects.size());
			for (GenericSensorData data : sApp.genericSensorDataObjects) {
				String value = data.getValue().getText();
				ValueType VALUETYPE = ValueType.getValue(value);
				if (ValueType.PERSON == VALUETYPE && showPersons) {
					addMarker(data, "person");
					tempDataObjects.add(data);
				}
				if (ValueType.MOOD == VALUETYPE && showMoods)   {
					addMarker(data, "mood");
					tempDataObjects.add(data);
				}
			}
	}

	
	private void addMarker(GenericSensorData data, String type) {
		Double lat = Double.parseDouble(data.getLocation().getLatitude());
		Double lng = Double.parseDouble(data.getLocation().getLongitude());
		LatLng latlng = new LatLng(lat, lng);
		BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker();
		ValueType VALUETYPE = ValueType.getValue(data.getValue().getText());

		if (VALUETYPE == ValueType.PERSON) icon  = BitmapDescriptorFactory.fromResource(R.drawable.ic_social_person);
		if (VALUETYPE == ValueType.MOOD) icon = BitmapDescriptorFactory.fromResource(R.drawable.device_access_bightness_low_light);

		Marker marker = mMap.addMarker(new MarkerOptions()
		.position(latlng)
		.title(data.getValue().getText())
		.snippet("User: " + data.getCreationInfo().getPerson()).icon(icon) );

		markers.add(marker);

	}
	
	


}
