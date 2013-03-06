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

import Utilities.UtilityClass;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ArrayAdapter;
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
	       	
	
	       

	   
	    }
		
		@Override
		public void onResume () {
			super.onResume();

			
			// Register the BroadcastReceiver
	    	IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	    	//mReceiver.
		}
		//TODO: I MAin istedenforher . 
		
		/**
		 * Global Broadcast receiever. Listen for change in internet connectivity status.
		 */
		private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
		        	
		        	boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
		            showToast("reason" + reason);
		            if (noConnectivity) Log.d("BREC.", "no connectivity");
		            if (isFailover) Log.d("REC", "FAILOVER");
		            
		            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
		                       
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

  		
  		//unregisterReceiver(mReceiver);
    }
      

    
    
	
}
