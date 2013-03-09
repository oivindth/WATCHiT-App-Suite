package activities;

import parsing.GenericSensorData;
import parsing.Parser;
import service.ServiceManager;
import service.WATCHiTService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.R;

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
        sApp.service = new ServiceManager(this, WATCHiTService.class, new Handler() {
          @Override
          public void handleMessage(Message msg) {
            // Receive message from service
            switch (msg.what) {
            case WATCHiTService.MSG_CONNECTION_ESTABLISHED: //TODO: Not receiving this from the service when checking the bluetooth...
          	  Log.d("MainActivity Handler ", "Connection established message receieved from service.");
          	  dismissProgress();
              break;
              case WATCHiTService.MSG_SET_STRING_VALUE_TO_ACTIVITY: 
            	  String data = msg.getData().getString("watchitdata");
                  //textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
                  Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
                  //GenericSensorData gsd = Parser.buildSimpleXMLObject(data, latitude, longitude);
              	String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain(); 
                  //dataObject = Parser.buildDataObjectFromSimpleXMl(gsd, jid);
                  //new CreateSpaceTask().execute();
                  //new PublishDataTask().execute(dataObject);
              	Toast.makeText(getBaseContext(), "WATCHiT Dat: " + data, Toast.LENGTH_SHORT).show();
                  break;
              default:
                super.handleMessage(msg);
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
        /**
         * Creating all buttons instances
         * */
        //Button buttonEvent = (Button) findViewById(R.id.btn_event);
 

        //Button buttonSetUp = (Button) findViewById(R.id.btn_setup);
 
        // Dashboard Messages button
        Button buttonMap = (Button) findViewById(R.id.btn_map);
 
        // Dashboard Places button
        Button buttonApp2 = (Button) findViewById(R.id.btn_app2);

        Button buttonGateway = (Button) findViewById(R.id.btn_gateway);
 
        // Dashboard Photos button
        Button buttonProfile = (Button) findViewById(R.id.btn_profile);
        
        //DashBoard mirror text view
        TextView textview = (TextView) findViewById(R.id.footerTextView);
        
        textview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
        
 
        buttonMap.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(i);
            }
        });
 
        buttonApp2.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	showToast("Not yet implemented.");
                // Launching News Feed Screen
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
 
        // Listening to Photos button click
        buttonProfile.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
            	showToast("Profile with badges?");
                //Intent i = new Intent(getApplicationContext(), PhotosActivity.class);
                //startActivity(i);
            }
        });
    }
          
    @Override
    public void onResume() {
    	super.onResume();
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
            	//showProgress(true);
            	//TODO: Not here
       
            	
            	showToast("Logging out...");
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
