package asynctasks;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;

public class AuthenticateUserTask extends AsyncTask<Void, Void, Boolean> {

	
	private String mUserName;
	private String mPassword;
	private BaseActivity mActivity;
	private MainApplication sApp;
	
	public AuthenticateUserTask (BaseActivity activity ,String userName, String password) {
		Log.d("AuthenticateUserTask", "in constructor");
		mUserName = userName;
		mPassword = password;
		mActivity = activity;
		sApp = MainApplication.getInstance();
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
    	  MainApplication.getInstance().connectionHandler.connect();
    	
    	 
      } catch (ConnectionStatusException e) {
    	  e.printStackTrace();
    	  return false;
    	  
      }  catch (Exception e) {
 		 e.printStackTrace();
 		 return false;
		
	}
      return true; 
	}

	
	@Override
	protected void onPostExecute(final Boolean success) {
		//mAuthTask = null;
		//showProgress(false);
		Log.d("AuthenticateUserTask", "onPostExecute");
		if (success) {
			mActivity.showToast("Logged in user.");
			Log.d("AUTHENTICATEUSERTASK", "Succsessfully logged in the user");
			sApp.OnlineMode =true;
			sApp.setApplicationMode(Mode.ONLINE);
		} else {
			mActivity.showToast("Failed to connect user" );
			
			Log.d("AUTHENTICATEUSERTASK", "failed to connect the user.");
			sApp.OnlineMode =false;
			sApp.setApplicationMode(Mode.OFFLINE);
		}
	}
	@Override
	protected void onCancelled() {
	
	}

}
