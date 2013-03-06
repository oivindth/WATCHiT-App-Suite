package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.app.Application;
import android.bluetooth.BluetoothDevice;

/**
 * Application object. Used as a pool for global variables and dataobjects to simplify passing them between activities and services.
 * Implemented as singleton.
 * @author oivindth
 *
 */
public class MainApplication extends Application {
	
    private static MainApplication sInstance;

    public Mode applicationMode;
    
    public static MainApplication getInstance() {
      return sInstance;
    }
    @Override
    public void onCreate() {
      super.onCreate();  
      sInstance = this;
      //sInstance.initializeInstance();
    }

    //protected void initializeInstance() {
        // do all your initialization here
        //sessionHandler = new SessionHandler( 
          //  this.getSharedPreferences( "PREFS_PRIVATE", Context.MODE_PRIVATE ) );
	
	private boolean valuesSet = false;
	
	public boolean OnlineMode, isLocationOn, isWATChiTOn = false; //TODO: Not best place to have this
	
	public List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
	
	public ConnectionConfigurationBuilder connectionConfigurationBuilder; 
	public ConnectionConfiguration connectionConfig; 
	
	public ConnectionHandler connectionHandler; 
	public SpaceHandler spaceHandler; 
	public String dbName = "sdkcache"; //TODO: put in resources string
	public List<Space> spacesInHandler = new ArrayList<Space>();
	public List<de.imc.mirror.sdk.DataObject> dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
	public DataHandler dataHandler;
	public Space currentActiveSpace;
	
	private String userName;
	private String password;


	/**
	 * Switch the current active space.
	 * @param space
	 */
	public void switchSpace(Space space) {
		try {
			resetHandler();
			dataHandler.registerSpace(space.getId());
			currentActiveSpace = space;
		} catch (UnknownEntityException e) {
			e.printStackTrace();
		}
	}
	
	private void resetHandler() {
		dataHandler = new DataHandler(connectionHandler, spaceHandler);
	}
	
	public void setApplicationMode (Mode mode) {
		spaceHandler.setMode(mode);
		dataHandler.setMode(mode);

		
		if (OnlineMode) {
			//TODO: do something
		}
		
		if (mode == Mode.ONLINE) {
		// If we need to do more than update the handlers.
		} else {
		}
	}
	
	//public List<Space> spaces; 
	public boolean getvaluesSet() {
		return valuesSet;
	}
	public void setValuesSet(boolean bool) {
		valuesSet = bool;
	}
	


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	


}
