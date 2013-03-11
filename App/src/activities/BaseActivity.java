package activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.watchit_connect.MainApplication;
import com.example.watchit_connect.R;


import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Base class for our activities to avoid duplicate code.
 * @author oivindth
 */
public abstract class BaseActivity extends SherlockFragmentActivity {
	
	private ProgressDialog mProgressDialog;
	protected MainApplication sApp;
	
	String mockLatitude = "123"; 
	String mockLongitude = "323"; 

	protected DataObjectListener myListener;
	
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
		            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
		            //showToast("reason" + reason);
		            if (noConnectivity) {
		            	Log.d("BROADCASTRECEIEVER BASEACTIVITY:", "no connectivity");
		            	sApp.OnlineMode = false;
		            	sApp.setApplicationMode(Mode.OFFLINE);
		            	//showToast("Lost connection to the internet. Application online mode disabled.");
		            }
		            	
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
      unregisterReceiver(mReceiver);
    }
}
