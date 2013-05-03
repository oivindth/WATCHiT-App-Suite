package asynctasks;

import no.ntnu.emergencyreflect.R;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import activities.BaseActivity;
import android.os.AsyncTask;

/**
 * 
 * @author ¯ivind Thorvaldsen
 * @date 2013
 * AsyncTask used for authenticating a user with the server.
 *
 */
public class AuthenticateUserTask extends AsyncTask<Void, Void, Boolean> {

	private BaseActivity mActivity;
	private MainApplication sApp;
	
	public AuthenticateUserTask (BaseActivity activity ,String userName, String password) {
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
      try {
    	  sApp.connectionHandler.connect();
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
		if (success) {
			mActivity.showToast(mActivity.getString(R.string.user_logged_in));
			//Log.d("AUTHENTICATEUSERTASK", "Succsessfully logged in the user");
			 sApp.spaceHandler.setMode(Mode.ONLINE);
	  		  sApp.dataHandler.setMode(Mode.ONLINE);
	  		  sApp.OnlineMode = true;
	  		  sApp.broadCastOnlineModeChanged(true);
		} else {
			mActivity.showToast(mActivity.getString(R.string.log_in_failed));
			//Log.d("AUTHENTICATEUSERTASK", "failed to connect the user.");
			sApp.OnlineMode = false;
		}
		new GetSpacesTask(mActivity).execute();
	}
	@Override
	protected void onCancelled() {
	
	}

}
