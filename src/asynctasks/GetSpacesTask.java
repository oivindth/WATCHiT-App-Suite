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
					MainApplication.spaces = MainApplication.spaceHandler.getAllSpaces();
		  Log.d("getspacestask", "size of spaces " + MainApplication.spaces.size());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		protected void onPostExecute(final Boolean success) {
			if (success) {
			} else {
				mActivity.showToast("Somethying went wrong when trying to fetch spaces..");
			}
		}
    
	
}
