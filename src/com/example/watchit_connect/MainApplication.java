package com.example.watchit_connect;

import java.util.ArrayList;

import com.example.watchit_connect.Spaces.SpacesFragment;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class MainApplication extends Application {
	
	private boolean valuesSet = false;
	
	public boolean getvaluesSet() {
		return valuesSet;
	}
	
	public void setValuesSet(boolean bool) {
		valuesSet = bool;
	}
	
	private ConnectionConfigurationBuilder connectionConfigurationBuilder; //set in loginactivity
	public ConnectionConfigurationBuilder getConnectionConfigurationBuilder() {
		return connectionConfigurationBuilder;
	}
	public void setConnectionConfigurationBuilder(
			ConnectionConfigurationBuilder connectionConfigurationBuilder) {
		this.connectionConfigurationBuilder = connectionConfigurationBuilder;
	}
	public ConnectionConfiguration getConnectionConfig() {
		return connectionConfig;
	}
	public void setConnectionConfig(ConnectionConfiguration connectionConfig) {
		this.connectionConfig = connectionConfig;
	}
	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}
	public void setConnectionHandler(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
	public SpaceHandler getSpaceHandler() {
		return spaceHandler;
	}
	public void setSpaceHandler(SpaceHandler spaceHandler) {
		this.spaceHandler = spaceHandler;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
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
	private ConnectionConfiguration connectionConfig; //set in login activity
	private ConnectionHandler connectionHandler; // set in login activity
	private SpaceHandler spaceHandler; //set in SpacesActivity
	private String dbName = "sdkcache";
	
	private String userName;
	private String password;
	


}
