package activities;

import listeners.LocationChangeListener;
import parsing.GenericSensorData;
import parsing.Parser;
import service.LocationService;
import service.ServiceManager;
import service.WATCHiTService;
import Utilities.UtilityClass;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.AuthenticateUserTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import no.ntnu.emergencyreflect.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.android.DataObject;


public class MainDashBoardActivity extends BaseActivity  {

	DataObjectListener myListener;
	private Handler handler;
	
	








	
	

	
	/**
	 * Set up the dashboard with buttins and onclick listeners etc.
	 */
	private void setUpDashBoardGUI() {
		// new GetSpacesTask().execute();
		Button buttonMap = (Button) findViewById(R.id.btn_map);
		Button buttonApp2 = (Button) findViewById(R.id.btn_app2);
		Button buttonGateway = (Button) findViewById(R.id.btn_gateway);
		Button buttonProfile = (Button) findViewById(R.id.btn_profile);
		TextView textview = (TextView) findViewById(R.id.footerTextView);

		// open webpage to mirror?
		textview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		buttonMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(getApplicationContext(), MapActivity.class);
				startActivity(i);
			}
		});
		buttonApp2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showToast("Not yet implemented.");
				//Intent i = new Intent(getApplicationContext(), );
				//startActivity(i);
			}
		});
		// Listening to Events button click
		buttonGateway.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), GatewayActivity.class);
				startActivity(i);
			}
		});
		buttonProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showToast("Profile with badges?");
				//Intent i = new Intent(getApplicationContext(), PhotosActivity.class);
				//startActivity(i);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//unregisterReceiver(mReceiver);
		try {
			sApp.service.unbind();
			sApp.locationService.unbind();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
