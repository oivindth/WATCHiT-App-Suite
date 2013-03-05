package asynctasks;

import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.MainApplication;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import android.os.AsyncTask;

/**
 * Represents an asynchronous login task used to authenticate
 * the user.
 */
public class AuthenticateUserTask extends AsyncTask<Void, Void, Boolean> {

	
	private String mUserName;
	private String mPassword;
	private BaseActivity mActivity;
	
	public AuthenticateUserTask (BaseActivity activity ,String userName, String password) {
		mUserName = userName;
		mPassword = password;
		mActivity = activity;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		/*
        //Configure connection
		connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
        connectionConfigurationBuilder.setHost(getString(R.string.host));
        connectionConfig = connectionConfigurationBuilder.build();
        connectionHandler = new ConnectionHandler(mUserName, mPassword, connectionConfig);
        */
      try {
    	  MainApplication.connectionHandler.connect();
      } catch (ConnectionStatusException e) {
    	  e.printStackTrace();
    	  return false;
      }
		return true;
	}

	
	@Override
	protected void onPostExecute(final Boolean success) {
		//mAuthTask = null;
		//showProgress(false);

		if (success) {
			mActivity.showToast("Logged in user.");
		} else {
			mActivity.showToast("Failed to connect user" );
		}
	}
	@Override
	protected void onCancelled() {
	
	}

}
