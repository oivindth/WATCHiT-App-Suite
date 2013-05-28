package asynctasks;
/**
 * Copyrigth 2013 Øivind Thorvaldsen
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
import android.util.Log;

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
	}
		@Override
		protected void onPreExecute() {
			mActivity.showProgress(mActivity.getString(R.string.sync), mActivity.getString(R.string.syncing));
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d("running", "getdata task running");
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
				app.TPObjects = new ArrayList<GenericSensorData>();
				
				for (de.imc.mirror.sdk.DataObject dobj : app.dataObjects) {
					GenericSensorData temp = Parser.buildSimpleXMLObject((DataObject) dobj);
					Log.d("dobj", dobj.toString());
					if (temp.getValue().getType().equals("note")) {
						app.genericSensorDataObjects.add(temp);
					} else if (temp.getValue().getType().equals("steps")) {
						Log.d("nullhm", "unit:" + temp.getValue().getUnit());
						Log.d("nullhm", "proc " + app.currentProcedure.getName() );
						
						try {
							if (temp.getValue().getUnit().equals(app.currentProcedure.getName())) {
								//hvis stepsa som sendes ned er lik den nåværende proceduren...
								Log.d("dobj", dobj.toString());
								app.TPObjects.add (temp);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} 
				}
				//app.genericSensorDataObjects = Parser.convertDataObjectsToGenericSensordataObjects(app.dataObjects);
				
				//Log.d("getDataFromSpaceTask", "gdo size: " + app.genericSensorDataObjects.size());
				//Log.d("GETDATATASK :", "size of data: " + app.dataObjects.size());
				//Log.d("getdatafromspacetask :", "spaceid of fetched data: " + mSpaceId);
				
				//sort
				
				
				spacChangeListener.onDataFetchedFromSpace();
			} else {
				mActivity.showToast(mActivity.getString(R.string.data_sync_failed));
				//new GetDataFromSpaceTask(mActivity, mSpaceId);
				//Log.d("GETDATATASK", "FAIL");
			}
		}
}
