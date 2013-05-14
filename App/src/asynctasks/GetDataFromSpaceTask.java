package asynctasks;

import java.util.ArrayList;

import no.ntnu.emergencyreflect.R;

import listeners.SpaceChangeListener;

import parsing.GenericSensorData;
import parsing.GenericSensorDataTP;
import parsing.Parser;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.android.DataObject;

import activities.BaseActivity;
import android.os.AsyncTask;

/**
 * 
 * @author Øivind Thorvaldsen
 *AsyncTask that fetches data from the XMPP Spaces Server
 *
 */
public class GetDataFromSpaceTask extends AsyncTask<Void, Void, Boolean> {

	
	private String mSpaceId;
	private BaseActivity mActivity;
	private SpaceChangeListener spacChangeListener;
	
	private MainApplication app;
	
	public GetDataFromSpaceTask(BaseActivity activity, String spaceId) {
		mSpaceId = spaceId;
		mActivity = activity;
		app = MainApplication.getInstance();
		spacChangeListener = (SpaceChangeListener) activity;
		//Log.d("spaceid: ", mSpaceId);
		//Log.d("mactivity", " " + mActivity);
	}
		@Override
		protected void onPreExecute() {
			mActivity.showProgress(mActivity.getString(R.string.sync), mActivity.getString(R.string.syncing));
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//app.dataHandler.setMode(Mode.ONLINE);
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
				app.genericSensorDataObjects = new ArrayList<GenericSensorData>();
				app.TPObjects = new ArrayList<GenericSensorDataTP>();
				
				for (de.imc.mirror.sdk.DataObject dobj : app.dataObjects) {
					GenericSensorData temp = Parser.buildSimpleXMLObject((DataObject) dobj);
					if (temp.getValue().getType().equals("note")) {
						app.genericSensorDataObjects.add(temp);
					} else if (temp.getValue().getType().equals("steps")) {
						if (temp.getValue().getUnit().equals(app.currentProcedure.getName())) {
							//hvis stepsa som sendes ned er lik den nåværende proceduren...
							GenericSensorDataTP temptp = Parser.buildSimpleXMLTPObject((DataObject) dobj);
							app.TPObjects.add (temptp);
						}
					} 
				}
				//app.genericSensorDataObjects = Parser.convertDataObjectsToGenericSensordataObjects(app.dataObjects);
				
				//Log.d("getDataFromSpaceTask", "gdo size: " + app.genericSensorDataObjects.size());
				//Log.d("GETDATATASK :", "size of data: " + app.dataObjects.size());
				//Log.d("getdatafromspacetask :", "spaceid of fetched data: " + mSpaceId);
				spacChangeListener.onDataFetchedFromSpace();
			} else {
				mActivity.showToast(mActivity.getString(R.string.data_sync_failed));
				//new GetDataFromSpaceTask(mActivity, mSpaceId);
				//Log.d("GETDATATASK", "FAIL");
			}
		}
}
