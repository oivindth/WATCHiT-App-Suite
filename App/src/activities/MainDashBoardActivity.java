package activities;

import parsing.GenericSensorData;
import parsing.Parser;
import service.LocationService;
import service.ServiceManager;
import service.WATCHiTService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;


public class MainDashBoardActivity extends BaseActivity  {

    private View mDashboardFormView;
	private View mDashboardStatusView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        
        new GetSpacesTask(this).execute();
        
     // Create a service and handle incoming messages
        //TODO: Make handler static to avoid (potential) leaks?
        sApp.service = new ServiceManager(getBaseContext(), WATCHiTService.class, new Handler() {
          @Override
          public void handleMessage(Message msg) {
            // Receive message from service
            switch (msg.what) {
            case WATCHiTService.MSG_CONNECTION_ESTABLISHED:
            	sApp.isWATChiTOn = true;
          	  Log.d("MainActivity Handler ", "Connection established message receieved from service.");
          	  showToast("Connection to WATCHiT established..");
          	  //dismissProgress();
              break;
            case WATCHiTService.MSG_CONNECTION_FAILED:
            	//dismissProgress();
            	sApp.isWATChiTOn = false;
            	Log.d("MainDashBoardActivityHandler: ", " Connection failed");
            	showToast(" Failed to connect to WATCHiT....");
            	
            	break;
            case WATCHiTService.MSG_CONNECTION_LOST:
            	Log.d("MainDashBoardActivity", "Lost connection with WATChiT...");
            	showToast("Warning: Lost connection with WATCHiT!");
            	sApp.isWATChiTOn = false;
            	break;
              case WATCHiTService.MSG_SET_STRING_VALUE_TO_ACTIVITY: 
            	  String data = msg.getData().getString("watchitdata");
                  //textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
                  Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
                  String lat = String.valueOf(sApp.getLatitude());
                  String lot = String.valueOf(sApp.getLongitude());
                  
                  GenericSensorData gsd = Parser.buildSimpleXMLObject(data, lat , lot);
              	  String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain(); 
                  DataObject dataObject = Parser.buildDataObjectFromSimpleXMl(gsd, jid);
                  //new CreateSpaceTask().execute();
                  new PublishDataTask(dataObject, sApp.currentActiveSpace.getId()).execute();
              	Toast.makeText(getBaseContext(), "WATCHiT Dat: " + data, Toast.LENGTH_SHORT).show();
                  Log.d("watchitdata:  ", data);
                  break;
              default:
                super.handleMessage(msg);
            } 
          }
        });
		
		
        
  
        //LocationService.
        sApp.locationService = new ServiceManager(getBaseContext(), LocationService.class, new Handler() {
          @Override
          public void handleMessage(Message msg) {
            // Receive message from service
            switch (msg.what) {
            case LocationService.MSG_UPDATE_LOCATION:
            	Log.d("MainDashBoardActivity", "receieved location update..");
            	double longitude = msg.getData().getDouble("longitude");
            	double latitude = msg.getData().getDouble("latitude");
            	sApp.setLongitude(longitude);
            	sApp.setLatitude(latitude);
            	
            	showToast("New location: " + " long:  " + longitude + " lat: " + latitude );
              break;
     
            } 
          }
        });
        
        sApp.dataHandler = new DataHandler(sApp.connectionHandler, sApp.spaceHandler);

   	    myListener = new DataObjectListener() {
       	 // implement this interface in a controller class of your application
      
   		@Override
   		public void handleDataObject(
   				de.imc.mirror.sdk.DataObject dataObject, String spaceId) {
   				showToast("New object receieved in space");
   			 String objectId = dataObject.getId();
   			 //Log.d("HandleDataObjet", dataObject.getCDMData().)
   			 
   			 Log.d("HandleDataObject", dataObject.toString());
   			 GenericSensorData data = Parser.buildSimpleXMLObject((DataObject) dataObject);
   			 Toast.makeText(getBaseContext(), "Receieved dataobject from space: " + objectId, Toast.LENGTH_SHORT).show();
   	    	 System.out.println("Received object " + objectId + " from space " + spaceId);
   		}
       	 };
        
       	sApp.dataHandler.addDataObjectListener(myListener);
        
        
        mDashboardFormView = findViewById(R.id.dashboard_main);
		mDashboardStatusView = findViewById(R.id.dashboard_status);
        
        // new GetSpacesTask().execute();
        Button buttonMap = (Button) findViewById(R.id.btn_map);
        Button buttonApp2 = (Button) findViewById(R.id.btn_app2);
        Button buttonGateway = (Button) findViewById(R.id.btn_gateway);
        Button buttonProfile = (Button) findViewById(R.id.btn_profile);
        
        TextView textview = (TextView) findViewById(R.id.footerTextView);
        
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
    public void onResume() {
    	super.onResume();
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
    	if (resultCode == ConnectionResult.SUCCESS) {
    		//proceed as normal
    	} else {
    		GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);

    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	  MenuInflater inflater = getSupportMenuInflater();
    	   inflater.inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
    	
      
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
    			
    			//just back use only finish():
               // finish();
    			showToast("Main");
                return true;
    	
            case R.id.menu_logout:
            	showProgress(true);
            	SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        		SharedPreferences.Editor editor = settings.edit();
        		//Set "hasLoggedIn" to true
        		editor.putBoolean("hasLoggedIn", false);
        		editor.putString("username", "");
        		editor.putString("password", "");
        		// Commit the edits!
        		
        		editor.commit();
        		
        		if (sApp.connectionHandler.getStatus() == ConnectionStatus.ONLINE) {
    				sApp.connectionHandler.disconnect();
        		}
        		intent = new Intent(this, LoginActivity.class);
        		startActivity(intent);
        		showProgress(false);
        		finish();
        		
            	return true;
      	
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
	 * Shows the progress UI and hides the login form.
	 */
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mDashboardStatusView.setVisibility(View.VISIBLE);
			mDashboardStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mDashboardStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mDashboardFormView.setVisibility(View.VISIBLE);
			mDashboardFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mDashboardFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mDashboardStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mDashboardFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
    @Override
    protected void onDestroy() {
      super.onDestroy();
  		//unregisterReceiver(mReceiver);
      
      try {
    	   sApp.service.unbind();
      } catch (Exception e) {
    	  e.printStackTrace();
      }
    }

}
