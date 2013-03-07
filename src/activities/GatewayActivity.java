package activities;

import parsing.Parser;

import com.example.watchit_connect.R;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;

import fragments.StatusFragment;

import Utilities.UtilityClass;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

/**
 * GateWay App. 
 * Application that supports 
 * @author oivindth
 *
 */
public class GatewayActivity extends BaseActivity {
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway);

		StatusFragment statusFragment = new StatusFragment();
		// Begin fragment transaction.
		getSupportFragmentManager().beginTransaction()
		.add(R.id.gateway_fragment_container, statusFragment  ,"status").commit(); 

	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_gateway, menu);
		return true;
	}
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 Intent intent;
		 switch (item.getItemId()) {

		 case android.R.id.home:
			 // iff up instead of back:
			 //  intent = new Intent(this, MainActivity.class);
			 // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 // startActivity(intent);
			 finish();
			 return true;
		 case R.id.menu_event:
			 intent = new Intent(this, SpaceActivity.class);
			 startActivity(intent);
			 //finish();
			 return true;
		 case R.id.menu_settings:
			 intent = new Intent(this, SettingsActivity.class);
			 startActivity(intent);
			 //finish()
			 return true;

		 default:
			 return super.onOptionsItemSelected(item);
		 }
	 }

	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy () {
		super.onDestroy();
	}
	
	
	
}
