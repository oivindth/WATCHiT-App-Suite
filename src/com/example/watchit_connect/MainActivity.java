package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import parsing.Parser;
import parsing.GenericSensorData;
import com.example.watchit_connect.MainFragment.MainFragmentListener;
import com.example.watchit_connect.SpacesFragment.OnSpaceItemSelectedListener;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;


public class MainActivity extends BaseActivity  {


	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardlayout);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        // new GetSpacesTask().execute();
        /**
         * Creating all buttons instances
         * */
        // Dashboard News feed button
        Button buttonEvent = (Button) findViewById(R.id.btn_event);
 
        // Dashboard Friends button
        Button buttonSetUp = (Button) findViewById(R.id.btn_setup);
 
        // Dashboard Messages button
        Button buttonMap = (Button) findViewById(R.id.btn_map);
 
        // Dashboard Places button
        Button buttonTP = (Button) findViewById(R.id.btn_tp);
 
        // Dashboard Events button
        Button buttonShare = (Button) findViewById(R.id.btn_share);
 
        // Dashboard Photos button
        Button buttonProfile = (Button) findViewById(R.id.btn_profile);
        
        //DashBoard mirror text view
        TextView textview = (TextView) findViewById(R.id.footerTextView);
        
        textview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showToast("Open webview to view web page here...?");
				
			}
		});
        
 
        /**
         * Handling all button click events
         * */
        buttonEvent.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), SpaceActivity.class);
                startActivity(i);
            }
        });
 
        buttonSetUp.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
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
 
        buttonTP.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	showToast("Not yet implemented.");
                // Launching News Feed Screen
                //Intent i = new Intent(getApplicationContext(), );
                //startActivity(i);
            }
        });
 
        // Listening to Events button click
        buttonShare.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	showToast("Placeholder for somethting else");
                // Launching News Feed Screen
                //Intent i = new Intent(getApplicationContext(), EventsActivity.class);
                //startActivity(i);
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
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
                return true;
    	
            case R.id.menu_sync:
            	new GetSpacesTask(this).execute();
            	app.dataHandler.setMode(Mode.ONLINE);
            	app.dataHandler.addDataObjectListener(myListener);
            	new GetDataFromSpaceTask(this ,"team#38").execute();
            	DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
            			("HelloWorld", "44.84866", "10.30683"), "admin" + "@" + app.connectionHandler.getConfiguration().getDomain());
            	Log.d("BASEACTIVITY", dob.toString());
            	new PublishDataTask(this, dob, "team#38").execute();
            	  
            	//showProgress("...", "zz");
            	return true;
      
            
         

            	
            default:
                return super.onOptionsItemSelected(item);
        }
	
    }

       

	



}
