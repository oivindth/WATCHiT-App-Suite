package asynctasks;

import java.util.List;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;

public class AuthenticateUserTask extends AsyncTask<Void, Void, Boolean> {

	
	private BaseActivity mActivity;
	private MainApplication sApp;
	
	public AuthenticateUserTask (BaseActivity activity ,String userName, String password) {
		Log.d("AuthenticateUserTask", "in constructor");
		mActivity = activity;
		sApp = MainApplication.getInstance();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mActivity.showProgress("Server", "Syncing with server....");
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("AuthenticateUserTask", "doInBackground");
		/*
        //Configure connection
		connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
        connectionConfigurationBuilder.setHost(getString(R.string.host));
        connectionConfig = connectionConfigurationBuilder.build();
        connectionHandler = new ConnectionHandler(mUserName, mPassword, connectionConfig);
        */
		
      try {
    	  sApp.connectionHandler.connect();
    	 
  		  //sApp.dataHandler.registerSpace("team#42");

  		  
      } catch (Exception e) {
    	  e.printStackTrace();
    	  return false;
      }  
      return true; 
	}

	
	@Override
	protected void onPostExecute(final Boolean success) {
		//mAuthTask = null;
		mActivity.dismissProgress();
		Log.d("AuthenticateUserTask", "onPostExecute");
		if (success) {
			mActivity.showToast("Logged in user.");
			Log.d("AUTHENTICATEUSERTASK", "Succsessfully logged in the user");
			 sApp.spaceHandler.setMode(Mode.ONLINE);
	  		  sApp.dataHandler.setMode(Mode.ONLINE);
	  		  sApp.OnlineMode = true;
	  		  sApp.broadCastOnlineModeChanged(true);
		} else {
			mActivity.showToast("Failed to connect user" );
			Log.d("AUTHENTICATEUSERTASK", "failed to connect the user.");
			sApp.OnlineMode = false;
		}
		new GetSpacesTask(mActivity).execute();
	}
	@Override
	protected void onCancelled() {
	
	}

}
