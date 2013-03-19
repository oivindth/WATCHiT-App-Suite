package asynctasks;

import java.util.ArrayList;

import parsing.Parser;

import com.example.watchit_connect.MainApplication;

import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;

public class GetDataFromSpaceTask extends AsyncTask<Void, Void, Boolean> {

	
	private String mSpaceId;
	private BaseActivity mActivity;
	
	
	private MainApplication app;
	
	public GetDataFromSpaceTask(BaseActivity activity, String spaceId) {
		Log.d("GETDATAFROMSPACETASK", "constructor");
		
		mSpaceId = spaceId;
		mActivity = activity;
		app = MainApplication.getInstance();
		
		Log.d("spaceid: ", mSpaceId);
		Log.d("mactivity", " " + mActivity);
	}
	
		@Override
		protected void onPreExecute() {
			mActivity.showProgress("Sync", "Syncing....");
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d("GETDATAFROMSPACETASK", "do in background");
			try {
			
	      		  app.dataHandler.registerSpace(mSpaceId);
				
				app.dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
				app.dataObjects = app.dataHandler.retrieveDataObjects(mSpaceId);

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		protected void onPostExecute(final Boolean success) {
			mActivity.dismissProgress();
			if (success) {
				app.genericSensorDataObjects = Parser.convertDataObjectsToGenericSensordataObjects(app.dataObjects);
				Log.d("getDataFromSpaceTask", "gdo size: " + app.genericSensorDataObjects.size());
				Log.d("GETDATATASK :", "size of data: " + app.dataObjects.size());
				Log.d("getdatafromspacetask :", "spaceid of fetched data: " + mSpaceId);
				mActivity.showToast("Size of data receieved from space " + app.dataObjects.size());
				//Log.d("GETDATATASK", app.dataObjects.get(app.dataObjects.size()-1).toString());
			} else {
				mActivity.showToast("failed. Trying again....");
				new GetDataFromSpaceTask(mActivity, mSpaceId);
				Log.d("GETDATATASK", "FAIL");
			}
		}
	
}
