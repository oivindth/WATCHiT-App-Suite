package com.example.watchit_connect;

import parsing.GenericSensorData;
import parsing.Parser;
import service.ServiceManager;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataHandler;
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
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

/**
 * Base class for our activities to avoid duplicate code.
 * @author oivindth
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	
	private ProgressDialog mProgressDialog;
	protected MainApplication app;
	protected ServiceManager service;
	String latitude = "123.33411"; //TODO: Get real data instead of mock.
	String longitude = "32342343.2"; //TODO: Get real data from ze phone instead of ze mock.
	protected BluetoothAdapter btAdapter;
	protected String bluetoothDeviceName;
	protected BluetoothDevice device;
	protected BluetoothSocket btSocket;
	protected DataObjectListener myListener;
	
	
		@Override
	    public void onCreate(Bundle savedInstanceState) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			    getActionBar().setHomeButtonEnabled(true);
			    getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			
	        super.onCreate(savedInstanceState); 
	
	        app = MainApplication.getInstance();
	        app.dataHandler = new DataHandler(app.connectionHandler, app.spaceHandler);

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
	        
	       	app.dataHandler.addDataObjectListener(myListener);
	       	app.dataHandler.setMode(Mode.ONLINE);
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
	              	String jid = app.getUserName() + "@" + app.connectionHandler.getConfiguration().getDomain(); 
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
   
      try { service.unbind(); }
      catch (Throwable t) {
    	  t.printStackTrace();
      }
    }
    
    
	
}
