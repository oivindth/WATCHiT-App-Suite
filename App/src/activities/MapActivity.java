package activities;

/**
 * Copyrigth 2013 �ivind Thorvaldsen
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

import listeners.LayersChangeListener;
import listeners.SpaceChangeListener;
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
import de.imc.mirror.sdk.Space;
import dialogs.ChooseEventDialog;
import dialogs.ChooseMapTypeDialog;
import dialogs.ChooseMapTypeDialog.MapTypeChangeListener;
import dialogs.MapLayersDialog;
import enums.SharedPreferencesNames;
import enums.ValueType;
import Utilities.UtilityClass;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;


public class MapActivity extends BaseActivity implements LayersChangeListener, SpaceChangeListener, MapTypeChangeListener {

	private GoogleMap mMap;
	private MainApplication sApp;
	private SharedPreferences mapPreferences;
	private List<Marker> markers;
	private List<GenericSensorData> tempDataObjects;

	private DataObjectListener dataObjectListener;
	private MapActivity mActivity;

	private DataObject lastDataObject;

	private boolean showMoods;
	private boolean showPersons;
	private boolean showNotes;
	private boolean showOnlyCurrentUser;

	private Handler handler;
	private GenericSensorData data;
	private LatLng latlng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		sApp = MainApplication.getInstance();

		if (sApp.needsRecreation()) {
			//sApp.re�nitializeHandlers();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

		mActivity = this;
		mapPreferences = getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , 0 );

		markers = new ArrayList<Marker>();
		tempDataObjects = new ArrayList<GenericSensorData>();

		handler = new Handler(Looper.getMainLooper());
		handleNewDataObjects();

		sApp.dataHandler.addDataObjectListener(dataObjectListener);
	}

	@Override
	public void onResume () {
		super.onResume();
		setUpMapIfNeeded();	
		mMap.setMyLocationEnabled(true);

		int checkedItem = mapPreferences.getInt("mapTypePos", 0);
		onMapTypeChanged(checkedItem);

		Location l = mMap.getMyLocation();
		//LatLng latlgn = new LatLng(44.606113, 7.844238);

		//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlgn, 8));

		mapPreferences = getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , MODE_PRIVATE );
		showMoods = mapPreferences.getBoolean("mood_layer", false);
		showPersons = mapPreferences.getBoolean("person_layer", false);	
		showNotes = mapPreferences.getBoolean("notes_layer", false);
		showOnlyCurrentUser  = mapPreferences.getBoolean("user_layer",  false);

		this.onLayersChanged(showPersons, showMoods, showNotes, showOnlyCurrentUser);

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker arg0) {
				GenericSensorData data = null;
				for (Marker marker : markers) {
					if (arg0.equals(marker)) {
						data = tempDataObjects.get(markers.indexOf(marker));
					}
				}
				String unit ="";
				String value = data.getValue().getText();
				ValueType VALUETYPE = ValueType.getValue(value);
				if (VALUETYPE == ValueType.PERSON) {
					unit = "person";
				} else if (VALUETYPE == ValueType.NOTES) {
					unit = "notes";
				} else {
					unit = "mood";
				}
				Intent intent = new Intent(mActivity, MapMarkerDetailsActivity.class);
				intent.putExtra("unit", unit);
				intent.putExtra("user", data.getCreationInfo().getPerson());
				intent.putExtra("time", UtilityClass.parseTimeStampToTimeAndDate(data.getTimestamp()));
				intent.putExtra("value", data.getValue().getText());
				startActivity(intent);
			}
		});
	}


	private void handleNewDataObjects () {
		dataObjectListener = new DataObjectListener() {
			@Override
			public void handleDataObject(DataObject dataObject, String spaceId) {
				//hack because sdk is bugged. We don't want a notification from a space we currently are not registered to. 
				//also need a hack because the sdks listener publishes to many notifications
				if (!sApp.currentActiveSpace.getId().equals(spaceId)) return;
				if (lastDataObject!= null) {
					if (lastDataObject.getId().equals(dataObject.getId()) ) return;
				}
				lastDataObject = dataObject;
				data = Parser.buildSimpleXMLObject((de.imc.mirror.sdk.android.DataObject) dataObject);
				Double lat = Double.parseDouble(data.getLocation().getLatitude());
				Double lng = Double.parseDouble(data.getLocation().getLongitude());
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
		//SharedPreferences.Editor ed = mapPreferences.edit();
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
			new GetDataFromSpaceTask(this, sApp.currentActiveSpace.getId()).execute();
			return true;
		case R.id.menu_layers:
			new MapLayersDialog().show(getSupportFragmentManager(), "layersdialog");
			return true;

		case R.id.menu_changeSpace:
			new ChooseEventDialog().show(getSupportFragmentManager(), "chooseEventDialog");
			return true;

		case R.id.menu_maptype_layers:
			new ChooseMapTypeDialog().show(getSupportFragmentManager(), "chooseMapDialog");
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
	public void onLayersChanged(boolean showPersons, boolean showMoods, boolean notes, boolean onlyShowCurrentLocalUsersDataPoints) {
		mMap.clear();
		markers.clear();
		tempDataObjects.clear();
		for (GenericSensorData data : sApp.genericSensorDataObjects) {
			if (onlyShowCurrentLocalUsersDataPoints) {
				//Log.d("personal", "comapre names:  " +"data person: " + data.getCreationInfo().getPerson() + " user: " + sApp.connectionHandler.getCurrentUser().getUsername());
				if (!data.getCreationInfo().getPerson().equals(sApp.connectionHandler.getCurrentUser().getUsername())) {
					continue;
				}
			}
			String value = data.getValue().getText();
			ValueType VALUETYPE = ValueType.getValue(value);

			if (ValueType.PERSON == VALUETYPE && showPersons) {
				addMarker(data, "person");
				tempDataObjects.add(data);
			}
			if ( ( ValueType.MOOD_SAD == VALUETYPE || ValueType.MOOD_HAPPY == VALUETYPE || ValueType.MOOD_NEUTRAL == VALUETYPE) && showMoods)   {
				addMarker(data, "mood");
				tempDataObjects.add(data);
			}
			if (ValueType.NOTES == VALUETYPE && notes) {
				addMarker(data, "notes");
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

		String title = data.getValue().getText();

		if (VALUETYPE == ValueType.PERSON) {
			icon  = BitmapDescriptorFactory.fromResource(R.drawable.social_person_dark);
		}
		if (VALUETYPE == ValueType.MOOD_HAPPY){
			icon = BitmapDescriptorFactory.fromResource(R.drawable.orange_happy_icon);
		}
		if (VALUETYPE == ValueType.MOOD_SAD){
			icon = BitmapDescriptorFactory.fromResource(R.drawable.orange_mood_sad);
		}
		if (VALUETYPE == ValueType.MOOD_NEUTRAL) {
			icon = BitmapDescriptorFactory.fromResource(R.drawable.orange_mood_neutral);
		}
		if (VALUETYPE == ValueType.NOTES){
			icon = BitmapDescriptorFactory.fromResource(R.drawable.note_icon);
			title = getString(R.string.notes);
		}
		Marker marker = mMap.addMarker(new MarkerOptions()
		.position(latlng)
		.title(title)
		.snippet(getString(R.string.user) + " :" + data.getCreationInfo().getPerson() + "  " + getString(R.string.time) + " :" + UtilityClass.parseTimeStampToTimeOnly(data.getTimestamp())).icon(icon));

		markers.add(marker);
	}

	@Override
	public void onSpaceChanged(int position) {
		if (position == -1) return;
		if (sApp.dataHandler.getHandledSpaces().contains(sApp.currentActiveSpace)) {
			sApp.dataHandler.removeSpace(sApp.currentActiveSpace.getId());
		}
		Space space = sApp.spacesInHandler.get(position);
		sApp.currentActiveSpace = space;
		new GetDataFromSpaceTask(this, space.getId()).execute();
	}

	@Override
	public void onDataFetchedFromSpace() {
		handler.post(new Runnable(){
			@Override
			public void run() {
				try {
					mapPreferences = getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , MODE_PRIVATE );
					showMoods = mapPreferences.getBoolean("mood_layer", false);
					showPersons = mapPreferences.getBoolean("person_layer", false);	
					showNotes = mapPreferences.getBoolean("notes_layer", false);
					showOnlyCurrentUser  = mapPreferences.getBoolean("user_layer",  false);
					onLayersChanged(showPersons, showMoods, showNotes, showOnlyCurrentUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}  
		});
	}
	@Override
	public void onDestroy () {
		super.onDestroy();
		sApp.dataHandler.removeDataObjectListener(dataObjectListener);	
	}
	@Override
	public void onMapTypeChanged(int which) {
		if (which == 0) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		if (which == 1) mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		if (which == 2) mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		if (which == 3) mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}

}
