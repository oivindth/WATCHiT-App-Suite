package asynctasks;

import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.exceptions.UnknownEntityException;

public class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {
	
	private BaseActivity mActivity;
	MainApplication sApp;
		
		public GetSpacesTask (BaseActivity activity) {
			mActivity = activity;
			sApp = MainApplication.getInstance();
		}
		
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			//mActivity.showProgress("Syncing", "Syncing......");
			
		}
		
		
		protected Boolean doInBackground(Void...params) {
			try {
					sApp.spacesInHandler = sApp.spaceHandler.getAllSpaces();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		protected void onPostExecute(final Boolean success) {
			mActivity.dismissProgress();
			
	
			if (success) {
				Log.d("GETSPACESTASK", "successfully fetched spaces");
				
			} else {
				Log.d("GETSPACESTASK", "Something went terribly wrong when trying to get spaces." );
			}
		}
    
	
}
