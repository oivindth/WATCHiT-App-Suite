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
import android.os.AsyncTask;
import android.widget.Toast;

public class MainApplication extends Application {

	private ConnectionHandler connectionHandler;
	private ConnectionConfigurationBuilder connectionConfigurationBuilder;
	private ConnectionConfiguration connectionConfig;
	private SpaceHandler spaceHandler;
   public String userName, password;
	private String dbName = "sdkcache";
	
	/**
	 * @deprecated
	 * Idea a dedicated task to connect. Not necesarry at the moment..
	 *
	 */
	public class ConnectTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void...params) {
	      try {
	    	  if (connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
	    	  connectionHandler.connect();
	    	  System.out.println(connectionHandler.getStatus());
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  return false;
	      }
			return true;
		}
		protected void onPostExecute(final Boolean success) {
			//showProgress(false);
			if (success) 
				spaceHandler.setMode(Mode.ONLINE); //Default is offline mode, but we have already established an xmmp connection so we want it to be online.
			else
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void ConfigureSpaces() {
		 //Configure connection
       connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
       connectionConfigurationBuilder.setHost(getString(R.string.host));
       connectionConfig = connectionConfigurationBuilder.build();
       connectionHandler = new ConnectionHandler(userName, password, connectionConfig);
   
       spaceHandler = new SpaceHandler(getBaseContext(), connectionHandler, dbName);
       spaceHandler.setMode(Mode.ONLINE);
	}	
}
