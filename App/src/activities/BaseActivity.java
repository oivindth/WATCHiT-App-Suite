package activities;

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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Base class for our activities to avoid duplicate code.
 * @author oivindth
 */
public abstract class BaseActivity extends SherlockFragmentActivity {
	private ProgressDialog mProgressDialog;
	protected MainApplication sApp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState); 
		sApp = MainApplication.getInstance();
	}
	@Override
	public void onResume () {
		super.onResume();
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
	} 

	/**
	 * Listen for change in internet connectivity status.
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
				//showToast("reason" + reason);
				if (noConnectivity) {
					sApp.OnlineMode = false;
					sApp.dataHandler.setMode(Mode.OFFLINE);
					sApp.spaceHandler.setMode(Mode.OFFLINE);
					sApp.connectionHandler.disconnect();
				}

				if (isFailover) {
					//Log.d("REC", "FAILOVER");
				}
				// do application-specific task(s) based on the current network state, such 
				// as enabling queuing of HTTP requests when currentNetworkInfo is connected etc.
			}
		};
	};
	protected void enableSettings (String settingId) {
		Intent settingsIntent = new Intent(settingId);
		startActivity(settingsIntent); 
	}

	public void showToast(String msg ) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected void showAlert(String msg) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.app_name))
		.setMessage(msg)
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		}).create().show();
	}

	public void showProgress(String title, String msg) {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			dismissProgress();
		mProgressDialog = ProgressDialog.show(this, title, msg);
	}
	public void dismissProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
