package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import parsing.GenericSensorData;
import parsing.Parser;

import service.ServiceManager;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


/**
 * Application object. Used as a pool for global variables and dataobjects to simplify passing them between activities and services.
 * Implemented as singleton. Also contains WATChiT service object making watchit data available for several applications. 
 * (WATCHiTService should possibly also be implemented as singleton)
 * @author oivindth
 */
public class MainApplication extends Application {
	
    private static MainApplication sInstance;
    
    public ServiceManager service, locationService;

    public static MainApplication getInstance() {
      return sInstance;
    }
    @Override
    public void onCreate() {
      super.onCreate();  
      sInstance = this;
    }
    
   
    
    public DataObjectListener myListener;
    
	
	public boolean OnlineMode, isLocationOn, isWATChiTOn = false; //TODO: Not best place to have this
	
	public List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
	
	/**
	 * Handlers used for MIRROR Spaces.
	 */
	public ConnectionConfigurationBuilder connectionConfigurationBuilder; 
	public ConnectionConfiguration connectionConfig; 
	public ConnectionHandler connectionHandler; 
	public SpaceHandler spaceHandler; 
	public String dbName = "sdkcache2"; 
	public DataHandler dataHandler;

	
	public List<Space> spacesInHandler = new ArrayList<Space>();
	public List<de.imc.mirror.sdk.DataObject> dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
	
	public Space currentActiveSpace;
	
	private String userName;
	private String password;

	private double longitude;;
	private double latitude; 

	

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
	/**
	 * Send message to Service. Return false if it fails to send.
	 * @param message
	 * @return
	 */
	public boolean sendMessageToService(Message message) {
        try {
			service.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
        return true;
		
	}

	public Double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Double longitude) {			
		this.longitude = longitude;
	}
	
	public void setLatitude (Double latitude) {
		this.latitude = latitude;
	}
	
	
	/*
	   @Override
	    protected void onDestroy() {
	      super.onDestroy();
	  		      
	      try { service.unbind(); }
	      catch (Throwable t) {
	    	  t.printStackTrace();
	      }
	    }
	*/
	


}
