package asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.MainApplication;

public class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {
	
	private BaseActivity mActivity;
		
		public GetSpacesTask (BaseActivity activity) {
			mActivity = activity;
		}
		protected Boolean doInBackground(Void...params) {
			try {
					MainApplication.getInstance().spacesInHandler = MainApplication.getInstance().spaceHandler.getAllSpaces();
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
