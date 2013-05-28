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
import com.example.watchit_connect.MainApplication;

import activities.BaseActivity;
import android.os.AsyncTask;
import de.imc.mirror.sdk.android.DataObject;

/**
 * AsyncTask used for publishing dataobject to a space.
 * @author ¯ivind Thorvaldsen
 *
 */
public class PublishDataTask extends AsyncTask<Void, Void, Boolean> {

	private BaseActivity mActivity;
	private DataObject mDataObject;
	private String mSpaceId;
	private MainApplication sApp;

	public PublishDataTask (BaseActivity activiy, DataObject dataObject, String spaceId) {
		mActivity = activiy;
		mDataObject = dataObject;
		mSpaceId = spaceId;
		sApp = MainApplication.getInstance();
	}
	public PublishDataTask (DataObject dataObject, String spaceId) {
		mDataObject = dataObject;
		mSpaceId = spaceId;
		sApp = MainApplication.getInstance();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {

			sApp.dataHandler.publishDataObject(mDataObject, mSpaceId);
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			//Log.d("ERROR:", "trying again...");
			new PublishDataTask(mDataObject, mSpaceId).execute();
		} catch (Exception e) {
			e.printStackTrace();
			//Log.d("ERROR:", "failed to publish dataobject with id: " + mDataObject.getId() );
			return false;
		}
		return true;

	}

	protected void onPostExecute(final Boolean success) {

		if (success) {
			//Log.d("publish", "success");
		} else {
			//Log.d("ERROR:", "Something went wrong");
			mActivity.showToast("Failed to publish dataobject.....");
		}
	}
}
	

