package com.example.watchit_connect;

import com.example.watchit_connect.Spaces.SpacesActivity;
import com.example.watchit_connect.Spaces.SpacesFragment;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

/**
 * Base class for our activities to avoid duplicate code.
 * @author oivindth
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	
	private ProgressDialog mProgressDialog;
	

	
		@Override
	    public void onCreate(Bundle savedInstanceState) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			    getActionBar().setHomeButtonEnabled(true);
			}
	        super.onCreate(savedInstanceState); 
	        
	        
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
    			// app icon in action bar clicked; go home
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                
                return true;
    	
            case R.id.menu_sync:
            	//showProgress("...");
            	
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
    

    
    
    
    protected void showToast(String msg ) {
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
    
    protected void showProgress(String msg) {
    	if (mProgressDialog != null && mProgressDialog.isShowing())
    		            dismissProgress();
    		        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    		    }
    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
	
    }
	
}
