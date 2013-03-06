package asynctasks;

import parsing.GenericSensorData;
import parsing.Parser;

import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.MainApplication;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
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
			mActivity.showProgress("finished", "trying to fetch dataobjects..");
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d("GETDATAFROMSPACETASK", "do in background");
			try {
			
				//app.dataHandler.registerSpace(mSpaceId); //TODO one space at a time. Register this when u choose space.
				//app.dataHandler.addDataObjectListener(myListener);
				//MainApplication.dataObjects
				app.dataHandler.setMode(Mode.ONLINE);
				app.dataHandler.registerSpace(mSpaceId);
				app.dataObjects = app.dataHandler.retrieveDataObjects(mSpaceId);

			} catch (UnknownEntityException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		protected void onPostExecute(final Boolean success) {
			mActivity.dismissProgress();
			if (success) {
				Log.d("GETDATATASK :", "size of data: " + app.dataObjects.size());
				//Log.d("GETDATATASK", app.dataObjects.get(app.dataObjects.size()-1).toString());
			} else {
				Log.d("GETDATATASK", "FAIL");
			}
		}
	
}
