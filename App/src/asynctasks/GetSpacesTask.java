package asynctasks;

import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.watchit_connect.MainApplication;

public class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {
	
	private BaseActivity mActivity;
	MainApplication sApp;
		
		public GetSpacesTask (BaseActivity activity) {
			mActivity = activity;
			sApp = MainApplication.getInstance();
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
			if (success) {
				Log.d("GETSPACESTASK", "successfully fetched spaces");
			} else {
				Log.d("GETSPACESTASK", "Something went terribly wrong when trying to get spaces." );
			}
		}
    
	
}
