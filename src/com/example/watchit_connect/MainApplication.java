package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import android.app.Application;
import android.bluetooth.BluetoothDevice;

public class MainApplication extends Application {
	
	private boolean valuesSet = false;
	
	
	public static boolean settingsSwitchOnline, settingsSwitchLocation,settingsSwitchWATChiT = false;
	
	public static boolean onlineMode = false;
	
	
	public static List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
	
	
	public static List<Space> spaces; 
	
	public boolean getvaluesSet() {
		return valuesSet;
	}
	
	public void setValuesSet(boolean bool) {
		valuesSet = bool;
	}
	
	public static ConnectionConfigurationBuilder connectionConfigurationBuilder; 
	public static ConnectionConfiguration connectionConfig; 
	public static ConnectionHandler connectionHandler; 
	public static SpaceHandler spaceHandler; 
	public static String dbName = "sdkcache";
	 public static List<de.imc.mirror.sdk.DataObject> dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
	
	private String userName;
	private String password;


	public DataHandler dataHandler;

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
