package com.example.watchit_connect;

import service.ServiceManager;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Base class for our activities to avoid duplicate code.
 * @author oivindth
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	
	private ProgressDialog mProgressDialog;
	protected MainApplication app;
	protected ServiceManager service;
	String latitude = "123.334.11"; //TODO: Get real data instead of mock.
	String longitude = "32342343.2"; //TODO: Get real data from ze phone instead of ze mock.
	protected BluetoothAdapter btAdapter;
	protected String bluetoothDeviceName;
	protected BluetoothDevice device;
	protected BluetoothSocket btSocket;
	
		@Override
	    public void onCreate(Bundle savedInstanceState) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			    getActionBar().setHomeButtonEnabled(true);
			    getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			
	        super.onCreate(savedInstanceState); 
	        
	        app = (MainApplication) getApplication();
	        
	        // Create a service and handle incoming messages
	        this.service = new ServiceManager(this, LocalService.class, new Handler() {
	          @Override
	          public void handleMessage(Message msg) {
	            // Receive message from service
	            switch (msg.what) {
	            case LocalService.MSG_CONNECTION_ESTABLISHED: //TODO: Not receiving this from the service when checking the bluetooth...
	          	  Log.d("MainActivity Handler ", "Connection established message receieved from service.");
	          	  //dismissProgress();
	              break;
	              case LocalService.MSG_SET_STRING_VALUE_TO_ACTIVITY: 
	            	  String data = msg.getData().getString("watchitdata");
	                  //textViewUserName.setText("data: " + msg.getData().getString("watchitdata") );     
	                  Log.d("Main", "Receieved from service: " + msg.getData().getString("watchitdata"));
	                  //GenericSensorData gsd = Parser.buildSimpleXMLObject(data, latitude, longitude);
	              	String jid = app.getUserName() + "@" + MainApplication.connectionHandler.getConfiguration().getDomain(); 
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
	        
	        
	         
	    }
		
		
		
		
		protected void enableSettings (String settingId) {
		    Intent settingsIntent = new Intent(settingId);
		    startActivity(settingsIntent);
		    
		}
		
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
    	switch (item.getItemId()) {
    	
    		case android.R.id.home:
    			// iff up instead of back:
                //intent = new Intent(this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();
                return true;
    	
    		case R.id.menu_map:
    			intent = new Intent(this, MapActivity.class);
    			startActivity(intent);
    			//finish();
    			return true;
                
            case R.id.menu_sync:
            	if (MainApplication.onlineMode) {
            		// Sync from internet
            	}
            	//showProgress("...");
            	return true;
            case R.id.menu_spaces:
            	intent = new Intent(this, SpacesActivity.class);
            	startActivity(intent);
            	//finish();
            	return true;	
            
            case R.id.menu_settings:
            	intent = new Intent(this, SettingsActivity.class);
            	startActivity(intent);
            	//finish();
            	return true;
            	
            default:
                return super.onOptionsItemSelected(item);
        }
	
    }
    
	/**
	 * Send message to Service. Return false if it fails to send.
	 * @param message
	 * @return
	 */
	protected boolean sendMessageToService(Message message) {
        try {
			service.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
        return true;
		
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
    
    protected void showProgress(String title, String msg) {
    	if (mProgressDialog != null && mProgressDialog.isShowing())
    		            dismissProgress();
    		        mProgressDialog = ProgressDialog.show(this, title, msg);
    		    }
    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
	
    }
    
    
    
    
    
  
    //TODO: Move to own class
    private class GetDataFromSpacesTask extends AsyncTask<Void, Void, Boolean> {
    	@Override
		protected Boolean doInBackground(Void... params) {
    		//app.dataHandler.addDataObjectListener(myListener);
			try {
				 for (Space space : MainApplication.spaces) {
					 app.dataHandler.registerSpace(space.getId());
					 MainApplication.dataObjects = app.dataHandler.retrieveDataObjects(space.getId()); 
				} 
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
    //TODO: Move to own class.
    protected class GetDataFromSpaceTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			String spaceId = params[0];
			try {
				app.dataHandler.registerSpace(spaceId);
				//app.dataHandler.addDataObjectListener(myListener);
				//MainApplication.dataObjects
				app.dataObjects = app.dataHandler.retrieveDataObjects(spaceId);

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
    
    
    protected class PublishDataTask extends AsyncTask<DataObject, Void, Boolean> {
  
        @Override
        protected void onPreExecute() {
        	super.onPreExecute();
        }
        
		@Override
		protected Boolean doInBackground(DataObject ...params) {
			DataObject dob = params[0];
	
		      //app.dataHandler.addDataObjectListener(myListener);
	    	 try {
				app.dataHandler.registerSpace("team#33");
			} catch (UnknownEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    
	    	 try {
				app.dataHandler.publishDataObject(dob, "team#33");
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
    protected void onDestroy() {
      super.onDestroy();
   
      try { service.unbind(); }
      catch (Throwable t) { }
    }
    
    
	
}
