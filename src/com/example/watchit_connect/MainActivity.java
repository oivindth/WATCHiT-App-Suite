package com.example.watchit_connect;

import service.ServiceManager;

import com.example.watchit_connect.Applications.ApplicationsSettingsFragment;
import com.example.watchit_connect.Applications.trainingprocedure.TrainingProcedureActivity;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends BaseActivity  {
	
	TextView textViewUserName, textViewData;
	View myTestView;
	MainFragment fragmentMain;
	private SpinnerAdapter mSpinnerAdapter;
	private OnNavigationListener mOnNavigationListener;
	private ActionBar actionBar;
	Fragment fragment;
	private ServiceManager service;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        MainApplication app = (MainApplication) getApplication();
        
       
        
     // Create a service 'SomeService1' (see below) and handle incoming messages
        this.service = new ServiceManager(this, LocalService.class, new Handler() {
          @Override
          public void handleMessage(Message msg) {
            // Receive message from service
            switch (msg.what) {
              case LocalService.MSG_SET_STRING_VALUE:
            	  
                  textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
                  Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
                break;
              default:
                super.handleMessage(msg);
            } 
          }
        });
        
     // Now you can control the service via .start(), .stop()
        //service.start();
        //service.stop();
     
        /*
        // Or send messages to it
        //service.send(Message.obtain(null, SomeService1.MSG_VALUE, 12345, 0));
        Bundle b = new Bundle();
        b.putString("watchitdata", "Hello WATCHiT");
        Message message = Message.obtain(null, LocalService.MSG_SET_STRING_VALUE);
        message.setData(b);
        service.send(message);
        */
        
        if (!app.getvaluesSet()) new UserLoginTask().execute();
        
    	fragmentMain = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain,"main").commit(); //TODO: husk Œ endre den i space til spacemain
        
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
     
        mSpinnerAdapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.action_list, 
    			android.R.layout.simple_spinner_dropdown_item);
   
        
        mOnNavigationListener = new OnNavigationListener() {
      	  // Get the same strings provided for the drop-down's ArrayAdapter
      	  String[] strings = getResources().getStringArray(R.array.action_list);
      	 
        	
      	
      	  @Override
      	  public boolean onNavigationItemSelected(int position, long itemId) {
      		
      		
      		FragmentTransaction ft;
      		  switch (position) {
			case 0:
				fragmentMain = new MainFragment();
				ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, fragmentMain, strings[position]);
				ft.commit();
				break;
			case 1:
				fragment = new ApplicationsSettingsFragment();
				ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, fragment, strings[position]);
				ft.commit();
				break;
				
			case 2:
				
			default:
				break;
			}
            return true;
      	  }
      	};
      	actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);	
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");
    	textViewUserName = (TextView) mainfragment.getView().findViewById(R.id.textViewUserName);
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        System.out.println("uname " +  settings.getString("username", ""));
        textViewUserName.setText("homseskuleh");
        
        mainfragment.update("uname " +  settings.getString("username", ""));
      
        
    }
    
    @Override
    protected void onDestroy() {
      super.onDestroy();
   
      try { service.unbind(); }
      catch (Throwable t) { }
    }
    
    
	public void onRadioButtonClicked(View view) {
		Intent intent;
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_app1:
	            if (checked) {
	            	intent = new Intent(this, TrainingProcedureActivity.class);
	            	startActivity(intent);
	            	//finish();
	            }
	            break;
	        case R.id.radio_app2:
	            if (checked) showToast("Not yet implemented");
	            break;
	    }
	}
    
    public void toggleWATCHiT(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        
        if (on) {
            showToast("Start service with thread here and wait for watchit data.");
            service.start();
            MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");

            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            System.out.println("uname " +  settings.getString("username", ""));
          
            textViewUserName = (TextView) mainfragment.getView().findViewById(R.id.textViewUserName);
            mainfragment.update("uname fdfd");
          
            
        } else {
            showToast("Stopped sync with WATCHiT");
            service.stop();
        }
    }
    
    //TODO: Set app global values in LoginActivity, not here..
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			//showProgress("waiting");
			ConnectionConfigurationBuilder connectionConfigurationBuilder;
			ConnectionConfiguration connectionConfig;
			ConnectionHandler connectionHandler;
			   //Configure connection
				connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
		        connectionConfigurationBuilder.setHost(getString(R.string.host));
		        connectionConfig = connectionConfigurationBuilder.build();
		        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		        
		        String username = settings.getString("username", "");
		        String password = settings.getString("password", "");
		        connectionHandler = new ConnectionHandler(username , password, connectionConfig);
			
			 MainApplication app =  (MainApplication) getApplication();
			  app.setConnectionConfigurationBuilder(connectionConfigurationBuilder);
			  app.setConnectionConfig(connectionConfig);
			  app.setConnectionHandler(connectionHandler);
			  app.setPassword(password);
			  app.setUserName(username);
			  app.setDbName("sdkcache");
			
	      try {
	    	  connectionHandler.connect();
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  return false;
	      }

			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			//dismissProgress();
			if (success) {
				//dosomething
			} else {
				//dosomething else
			}
		}
		
		
    }

    
    
}
