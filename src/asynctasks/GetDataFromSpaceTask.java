package asynctasks;

import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.MainApplication;

import android.os.AsyncTask;
import android.util.Log;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;

public class GetDataFromSpaceTask extends AsyncTask<Void, Void, Boolean> {

	
	private String mSpaceId;
	private BaseActivity mActivity;
	
	public GetDataFromSpaceTask(BaseActivity activity, String spaceId) {
		mSpaceId = spaceId;
		mActivity = activity;
	}
	
		@Override
		protected void onPreExecute() {
			mActivity.showProgress("finished", "'its finished' mancini");
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (MainApplication.dataHandler == null) {
					MainApplication.dataHandler = new DataHandler(MainApplication.connectionHandler, MainApplication.spaceHandler);
				}
				
				MainApplication.dataHandler.setMode(Mode.ONLINE);
				
				MainApplication.dataHandler.registerSpace(mSpaceId); //TODO one space at a time. Register this when u choose space.
				//app.dataHandler.addDataObjectListener(myListener);
				//MainApplication.dataObjects
				MainApplication.dataObjects = MainApplication.dataHandler.retrieveDataObjects(mSpaceId);

			} catch (UnknownEntityException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		protected void onPostExecute(final Boolean success) {
			mActivity.dismissProgress();
			if (success) {
				Log.d("GETDATATASK :", "size of data: " + MainApplication.dataObjects.size());
				
				Log.d("GETDATATASK", MainApplication.dataObjects.get(0).toString());
			} else {
			}
		}
    
	
}
