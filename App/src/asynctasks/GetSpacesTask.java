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
import activities.BaseActivity;
import android.os.AsyncTask;

import com.example.watchit_connect.MainApplication;

/**
 * 
 * @author ¯ivind Thorvaldsen
 * @date 2013
 * AsyncTask used for fetching spaces from the server
 *
 */
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
		//mActivity.dismissProgress();
		if (success) {
			//Log.d("GETSPACESTASK", "successfully fetched spaces");
		} else {
			//Log.d("GETSPACESTASK", "Something went terribly wrong when trying to get spaces." );
		}
	}
}