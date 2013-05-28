package asynctasks;
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
