package com.example.watchit_connect;

import com.example.watchit_connect.Applications.ApplicationsActivity;

import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BaseActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        MainApplication app = (MainApplication) getApplication();
        
        if (!app.getvaluesSet()) new UserLoginTask().execute();
        
    	MainFragment fragmentMain = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain,"main").commit(); //TODO: husk Œ endre den i space til spacemain
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			
			ConnectionConfigurationBuilder connectionConfigurationBuilder;
			ConnectionConfiguration connectionConfig;
			ConnectionHandler connectionHandler;
			   //Configure connection
				connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
		        connectionConfigurationBuilder.setHost(getString(R.string.host));
		        connectionConfig = connectionConfigurationBuilder.build();
		        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		        
		        String username = settings.getString("username", "");
		        String password = settings.getString("password", "");
		        connectionHandler = new ConnectionHandler(username , password, connectionConfig);
			
			 MainApplication app =  (MainApplication) getApplication();
			  app.setConnectionConfigurationBuilder(connectionConfigurationBuilder);
			  app.setConnectionConfig(connectionConfig);
			  app.setConnectionHandler(connectionHandler);
			  app.setPassword(password);
			  app.setUserName(username);
			  app.setDbName("sdkcache");
			
	      try {
	    	  connectionHandler.connect();
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  return false;
	      }

			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				//dosomething
			} else {
				//dosomething else
			}
		}
    }
    
}
