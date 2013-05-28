package com.example.watchit_connect;

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

import no.ntnu.emergencyreflect.R;

import listeners.OnlineModeChangeListener;
import listeners.StepListener;
import listeners.WATCHiTConnectionChangeListener;

import parsing.GenericSensorData;
import parsing.GenericSensorDataTP;
import parsing.Procedure;
import parsing.Result;
import parsing.Step;

import service.ServiceManager;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import activities.LoginActivity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;


/**
 * Application object. Used as a pool for global variables and dataobjects to simplify passing them between activities and services.
 * Implemented as singleton. Also contains WATChiT service object making watchit data available for several applications. 
 * @author oivindth
 */
public class MainApplication extends Application {
	
    private static MainApplication sInstance;
    
	public String latest;
    public ServiceManager service, locationService;
    public BluetoothSocket btSocket;
    public ArrayList<GenericSensorData> TPObjects;
    
    public List<Step> steps = new ArrayList<Step>();
    public int numberOfSteps = 0;
    
    public List<Procedure> procedures = new ArrayList<Procedure>();
    
    
    public List<Result> results;
    
    
    /**
     * Singleton.
     * @return
     */
    public static MainApplication getInstance() {
      return sInstance;
    }
    @Override
    public void onCreate() {
      super.onCreate();  
      sInstance = this;
    }
    
    public void setUpProceduresAndSteps () {
    	procedures = new ArrayList<Procedure>();
    	Procedure p = new Procedure();
    	p.setName("Test");
    	
    	Procedure italy = new Procedure();
    	italy.setName("Corcueno Safety 2013");
    	
    	List<Step> pSteps = new ArrayList<Step>();
    	pSteps.add(new Step("Step 1"));
    	pSteps.add(new Step("Step 2"));
    	pSteps.add(new Step("Step 3"));
    	
    	List<Step> iSteps = new ArrayList<Step>();
    	iSteps.add(new Step("Sicurezza"));
    	iSteps.add(new Step("Coscienza"));
    	iSteps.add(new Step("Inizio del Log. Roll."));
    	iSteps.add(new Step("Log. Roll."));
    	iSteps.add(new Step("Inizio collare"));
    	iSteps.add(new Step("Posizionamento cucchiaio"));
    	iSteps.add(new Step("Fine collare"));
    	iSteps.add(new Step("Fine cucchiaio"));
    	iSteps.add(new Step("Passaggio su spinale"));
    	iSteps.add(new Step("Cuscini"));
    	iSteps.add(new Step("Fine cuscini"));
    	iSteps.add(new Step("Ragno"));
    	iSteps.add(new Step("Condizioni paziente"));
    	iSteps.add(new Step("Fine ragno"));
    	iSteps.add(new Step("Fine con partenza"));
    	
    	p.setSteps(pSteps);
    	italy.setSteps(iSteps);
    	
    	procedures.add(p);
    	procedures.add(italy);
    	currentProcedure = p;
    	//Parser parse = new Parser();
    }
    
    
    /**
     * Set handlers and application to online or offline mode.
     * @param mode
     */
    public void setOnlineMode (Mode mode) {
    	if (mode == Mode.OFFLINE) {
    		OnlineMode = false;
			dataHandler.setMode(Mode.OFFLINE);
			spaceHandler.setMode(Mode.OFFLINE);
    	} else 
    		if(mode == Mode.ONLINE) {
    			OnlineMode = true;
    			dataHandler.setMode(Mode.ONLINE);
    			spaceHandler.setMode(Mode.ONLINE);
    		}
    }
    /**
     * Listeners:
     */
    private List<WATCHiTConnectionChangeListener> wclisteners = new ArrayList<WATCHiTConnectionChangeListener>();
    public void addListener(WATCHiTConnectionChangeListener listener) {
    	if (!wclisteners.contains(listener)) wclisteners.add(listener);
    }
    
    public void broadcastWATCHiTConnectionChange(boolean on) {
    	for (WATCHiTConnectionChangeListener listener : wclisteners) {
    		Log.d("broadcasting ", " broadcasting watchit connecting change");
    		listener.onWATCHiTConnectionChanged(on);
		}
    }
    
    private List<OnlineModeChangeListener> onlineModeListeners = new ArrayList<OnlineModeChangeListener>();
    public void addOnlineModeChangeListener (OnlineModeChangeListener listener) {
    	if (!onlineModeListeners.contains(listener)) onlineModeListeners.add(listener);
    }
    public void broadCastOnlineModeChanged(boolean on) {
    	for (OnlineModeChangeListener listener : onlineModeListeners) {
			listener.onOnlineModeChanged(on);
		}
    }
    
    private List<StepListener> stepListeners = new ArrayList<StepListener>();
    public void addStepListener(StepListener stepListener) {
    	if (!stepListeners.contains(stepListener)) {
    		stepListeners.add(stepListener);
    	}
    }
    public void broadCastStepsChanged(int position) {
    	for (StepListener listener : stepListeners) {
    		listener.stepAdded(position);
    	}
    	
    }
    public void broadCastAllStepsFinished() {
    	for (StepListener listener : stepListeners) {
    		listener.allStepsCompleted();
    	}
    }
    
    
    public DataObjectListener myListener;
    
	public boolean OnlineMode, isLocationOn, isWATChiTOn; //TODO: Not best place to have this
	
	public List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
	
	/**
	 * Handlers used for MIRROR Spaces.
	 */
	public ConnectionConfigurationBuilder connectionConfigurationBuilder; 
	public ConnectionConfiguration connectionConfig; 
	public ConnectionHandler connectionHandler; 
	public SpaceHandler spaceHandler; 
	public String dbName = "sdkcache20"; 
	public DataHandler dataHandler;

	
	public boolean needsRecreation() {
		
		if (connectionHandler == null || userName == null || password == null || spaceHandler == null || dataHandler == null) {
			return true;
		}
		return false;
	}
	
	public void reênitializeHandlers () {
		
		// Get host and domain from sharedpreferences or use default values(mirror-server-ntnu) if they are not set.
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String host = settings.getString("host", getString(R.string.host));
    	String domain = settings.getString("domain", getString(R.string.domain));
    	String applicationId = getString(R.string.application_id);
    	int port = settings.getInt("port", 5222); //5222 is standard port.
    	
    	 userName = settings.getString("username", "");
    	 password = settings.getString("password", "");
		
		 //Configure connection
		connectionConfigurationBuilder = new ConnectionConfigurationBuilder(domain,applicationId);
        connectionConfigurationBuilder.setHost(host);
        connectionConfigurationBuilder.setPort(port);
        connectionConfig = connectionConfigurationBuilder.build();
        connectionHandler = new ConnectionHandler(userName, password, connectionConfig);
        
        spaceHandler = new SpaceHandler(getApplicationContext(), connectionHandler, "databasett");
	    dataHandler = new DataHandler(connectionHandler, spaceHandler);
	    
	    spaceHandler.setMode(Mode.ONLINE);
	    dataHandler.setMode(Mode.ONLINE); 
	}
	
	public List<Space> spacesInHandler = new ArrayList<Space>();
	public List<de.imc.mirror.sdk.DataObject> dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
	
	public List<GenericSensorData> genericSensorDataObjects = new ArrayList<GenericSensorData>();
	
	public Space currentActiveSpace;
	
	private String userName;
	private String password;

	private double longitude;;
	private double latitude;

	public Procedure currentProcedure; 

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
		
		Double oldLng = this.longitude;
		this.longitude = longitude;
		 //if( longitude != oldLng) {
		//	 this.variableChangeListener.onVariableChanged(longitude);
		 //}
	}
	public void setLatitude (Double latitude) {
		this.latitude = latitude;
	}
	
}
