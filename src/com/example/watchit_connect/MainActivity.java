package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import parsing.Parser;
import parsing.GenericSensorData;
import service.ServiceManager;

import com.example.watchit_connect.Applications.trainingprocedure.TrainingProcedureActivity;
import com.example.watchit_connect.Spaces.SpaceFragment;
import com.example.watchit_connect.Spaces.SpacesActivity;
import com.example.watchit_connect.Spaces.SpacesFragment;
import com.example.watchit_connect.Spaces.SpacesFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceConfiguration;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.android.SpaceMember;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends BaseActivity implements OnSpaceItemSelectedListener  {
	
	TextView textViewUserName, textViewData;
	View myTestView;
	MainFragment fragmentMain;
	private SpinnerAdapter mSpinnerAdapter;
	private OnNavigationListener mOnNavigationListener;
	private ActionBar actionBar;
	Fragment fragment;
	private ServiceManager service;
	MainApplication app;
	Space space;
	String latitude = "123.334.11";
	String longitude = "32342343.2";
	DataObject dataObject;
	DataObjectListener myListener;
	SpaceHandler spaceHandler;
	 DataHandler dataHandler;
	 Dialog dialog;
	 List<de.imc.mirror.sdk.DataObject> dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        app = (MainApplication) getApplication();

     // Create a service and handle incoming messages
        this.service = new ServiceManager(this, LocalService.class, new Handler() {
          @Override
          public void handleMessage(Message msg) {
            // Receive message from service
            switch (msg.what) {
            case LocalService.MSG_CONNECTION_ESTABLISHED:
          	  Log.d("MainActivity Handler ", "Connection established message receieved from service.");
          	  dismissProgress();
              break;
              case LocalService.MSG_SET_STRING_VALUE: 
            	  String data = msg.getData().getString("watchitdata");
                  textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
                  Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
                  GenericSensorData gsd = Parser.buildSimpleXMLObject(data, latitude, longitude);
              	String jid = app.getUserName() + "@" + MainApplication.connectionHandler.getConfiguration().getDomain(); 
              	Log.d("handlemessage","text set?: "+ gsd.getValue().getText());
                  dataObject = Parser.buildDataObjectFromSimpleXMl(gsd, jid);
                  //new CreateSpaceTask().execute();
                  new PublishDataTask().execute();
                  break;
              default:
                super.handleMessage(msg);
            } 
          }
        });
        
        /*
        // Or send messages to it
        //service.send(Message.obtain(null, SomeService1.MSG_VALUE, 12345, 0));
        Bundle b = new Bundle();
        b.putString("watchitdata", "Hello WATCHiT");
        Message message = Message.obtain(null, LocalService.MSG_SET_STRING_VALUE);
        message.setData(b);
        service.send(message);
        */
        
      
        
   	    myListener = new DataObjectListener() {
    	 // implement this interface in a controller class of your application
   
		@Override
		public void handleDataObject(
				de.imc.mirror.sdk.DataObject dataObject, String spaceId) {
			 String objectId = dataObject.getId();
			 //Log.d("HandleDataObjet", dataObject.getCDMData().)
			 showAlert("new data published");
			 Log.d("HandleDataObject", dataObject.toString());
			 GenericSensorData data = Parser.buildSimpleXMLObject((DataObject) dataObject);
			 Toast.makeText(getBaseContext(), "Receieved dataobject from space: " + objectId, Toast.LENGTH_SHORT).show();
	    	 System.out.println("Received object " + objectId + " from space " + spaceId);
	    	 Toast.makeText(getBaseContext(), "Data: Location:  + longitude" + data.getLocation().getLongitude() + " latitude: " + data.getLocation().getLatitude()+ 
	    			 "Type: " + data.getValue().getType() + " unit: " + data.getValue().getUnit() + " value: " + data.getValue(), Toast.LENGTH_LONG).show();
	    	 Log.d("Handler", "funker ikke mer? :  " + data.getValue().getText());
	    	 
		}

    	 };
    	
        
        dataHandler = new DataHandler(MainApplication.connectionHandler, MainApplication.spaceHandler);
        dataHandler.setMode(Mode.ONLINE);
   	 	dataHandler.addDataObjectListener(myListener);

        
        
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
				fragment = new SpacesFragment();
				ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, fragment, strings[position]);
				ft.commit();
				break;
				
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
    
    public void createSpace(View view) {
    	// create a fracking dialog.
    	
    }
    
    @Override
    protected void onDestroy() {
      super.onDestroy();
   
      try { service.unbind(); }
      catch (Throwable t) { }
    }
    
    /*
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
	*/
    
    public void toggleWATCHiT(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        
        if (on) {
        	showToast("Start service with thread here and wait for watchit data.");
        	service.start();
            
            //showProgress("Waiting for ack from WATCHiT");
            
            /*
            MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");

            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            System.out.println("uname " +  settings.getString("username", ""));
          
            textViewUserName = (TextView) mainfragment.getView().findViewById(R.id.textViewUserName);
            mainfragment.update("uname fdfd");
          */
            
        } else {
            showToast("Stopped sync with WATCHiT");
            service.stop();
        }
    }
    
   
			
    private class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void...params) {
	      try {
	    	  if (MainApplication.connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
	    		  MainApplication.connectionHandler.connect();
	    	  
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  Toast.makeText(getBaseContext(), "Could not make a connection. Is wifi or 3g turned on?", Toast.LENGTH_SHORT).show();
	    	  return false;
	      }
	     
	      SpaceHandler sHandler = new SpaceHandler(getBaseContext(), MainApplication.connectionHandler, MainApplication.dbName);
	      sHandler.clear();
			sHandler.setMode(Mode.ONLINE);
			app.spaces = sHandler.getAllSpaces();
		  Log.d("getspacestask", "size of spaces " + app.spaces.size());
			
			return true;
		}

		protected void onPostExecute(final Boolean success) {
			
			if (success) {
			
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
    }


    private class CreateSpaceTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			
			// create space configuration with the current user as space moderator
			 Space.Type type = Space.Type.TEAM;
			 String name = "Test team";
			 String owner = MainApplication.connectionHandler.getCurrentUser().getBareJID();
			 boolean isPersistent = true;
			 SpaceConfiguration spaceConfig = new SpaceConfiguration(type, name, owner, isPersistent);
			 SpaceHandler spaceHandler = new SpaceHandler(getBaseContext(), MainApplication.connectionHandler, MainApplication.dbName);
		    	spaceHandler.setMode(Mode.ONLINE);
		    	MainApplication.spaceHandler = spaceHandler;
		    
			 

			 // add the user bob as space member
			 String bobsJID = "bob" + "@" + MainApplication.connectionHandler.getConfiguration().getDomain();
			 spaceConfig.addMember(new SpaceMember(bobsJID, SpaceMember.Role.MEMBER));
			 
			 // create space with this configuration
			 Space myNewTeamSpace = null;
			 try {
				 if (MainApplication.spaceHandler.getSpace(spaceConfig.getName()) == null)
			 myNewTeamSpace = MainApplication.spaceHandler.createSpace(spaceConfig);
				 else return false;
			
			 } catch (SpaceManagementException e) {
			  showToast("Fauled to create space....");
			  return false;
			 } catch (ConnectionStatusException e) {
				 showToast("Cannot create space when offline..");
				 // add exception handling
				 return false;
			 }
			 space = myNewTeamSpace;
			return true;
		}
		
		protected void onPostExecute(final Boolean success) {
			dismissProgress();
			if (success) {
				Toast.makeText(getBaseContext(), "Space created", Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
    
    private class GetDataFromSpaceTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String spaceId = params[0];
			try {
				dataHandler.registerSpace(spaceId);
				dataHandler.addDataObjectListener(myListener);
				dataObjects = dataHandler.retrieveDataObjects(spaceId);
				MainApplication.dataObjects = dataObjects;
				Log.d("getdatatask ", spaceId);
				Log.d("GetDataFromSpaceTask", " received objects with size:  " + dataObjects.size());

				
			} catch (UnknownEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
				
			}
			return true;
		}
		
		protected void onPostExecute(final Boolean success) {
			//dismissProgress();
			if (success) {
				Toast.makeText(getBaseContext(), "Data receieved", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
    
    
    private class PublishDataTask extends AsyncTask<Void, Void, Boolean> {
    	
    
    	
        @Override
        protected void onPreExecute() {
            
        }
        
    	
		@Override
		protected Boolean doInBackground(Void...params) {
			//showProgress("Publishing data to space");
			//showToast("Trying to publish data");
		      try {
		    	  if (MainApplication.connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
		    		  MainApplication.connectionHandler.connect();
		    	  
		      } catch (ConnectionStatusException e) {
		    	  e.printStackTrace();
		    	  return false;
		      }
	
	    	
		      dataHandler.addDataObjectListener(myListener);
	    	 try {
				dataHandler.registerSpace("team#33");
			} catch (UnknownEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    
	    	
	    	 try {
				dataHandler.publishDataObject(dataObject, "team#33");
			} catch (UnknownEntityException e) {
				//showToast("Failed to publish dataobject to space:  " + space.getId());
				e.printStackTrace();
				return false;
			}
	    	 
	    	 
	    	 return true;
		}

		protected void onPostExecute(final Boolean success) {
			
			if (success) {
				Toast.makeText(getBaseContext(), "Data published", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
 	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
    	switch (item.getItemId()) {
    	
    		case android.R.id.home:
    			// app icon in action bar clicked; go home
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                
                return true;
    	
            case R.id.menu_sync:
            	new GetSpacesTask().execute();
            	new GetDataFromSpaceTask().execute("team#33");
            	
            	return true;
            case R.id.menu_spaces:
            	intent = new Intent(this, SpacesActivity.class);
            	startActivity(intent);
            	finish();
            	return true;	
            
            case R.id.menu_settings:
            	//intent = new Intent(this, ApplicationsActivity.class);
            	//startActivity(intent);
            	finish();
            	return true;
            	
            default:
                return super.onOptionsItemSelected(item);
        }
	
    }

	@Override
	public void onSpaceItemSelected(int position) {
		
		
		Space space = app.spaces.get(position);
		new GetDataFromSpaceTask().execute(space.getId());
        DataObjectsFragment dataObjectsFragment = new DataObjectsFragment();
        //int members = space.getMembers().size();
        Bundle b = new Bundle();
        b.putInt("pos", position);
        dataObjectsFragment.setArguments(b);  
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, dataObjectsFragment, "dataobjects");
        transaction.addToBackStack("main");
        transaction.commit();
	}
    
    
}
