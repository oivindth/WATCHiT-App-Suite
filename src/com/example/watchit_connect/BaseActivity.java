package com.example.watchit_connect;

import com.example.watchit_connect.Applications.ApplicationsActivity;
import com.example.watchit_connect.Spaces.SpacesActivity;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity {
	
	private ProgressDialog mProgressDialog;

		@Override
	    public void onCreate(Bundle savedInstanceState) {
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
            case R.id.menu_sync:
            	//showProgress(true);
            	//new GetSpacesTask().execute();
            	//showProgress(true);
           	 //new PublishDataTask().execute();
            	return true;
            case R.id.menu_config:
            	intent = new Intent(this, SpacesActivity.class);
            	startActivity(intent);
            	finish();
            	return true;	
            case R.id.menu_Home:
            	intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_settings:
            	intent = new Intent(this, ApplicationsActivity.class);
            	startActivity(intent);
            	finish();
            	return true;
            	
            default:
                return super.onOptionsItemSelected(item);
        }
	
    }
    
    
    
    protected void showToast(String msg, int duration ) {
    	Toast.makeText(this, msg, duration).show();
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
