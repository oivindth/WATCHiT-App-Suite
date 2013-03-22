package no.ntnu.wathcitdatageneratorapp;


import java.util.ArrayList;
import java.util.List;

import parsing.GenericSensorData;
import parsing.Parser;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dialogs.ChooseEventDialog;
import dialogs.NoteDialog;
import dialogs.SettingsDialog;
import dialogs.SettingsDialog.SettingsDialogListener;
import dialogs.ShareDialog;
import dialogs.ChooseEventDialog.ChooseEventDialogListener;
import dialogs.NoteDialog.NoteDialogListener;
import dialogs.ShareDialog.ShareDialogListener;


import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;


public class MainActivity extends SherlockFragmentActivity implements OnClickListener, ShareDialogListener, NoteDialogListener, SettingsDialogListener, ChooseEventDialogListener {

	private ImageView happyButton, neutralButton, sadButton, rescueButton, noteButton;
	private String currentText, notes;
	private int currentImageID;
	private Bundle b;
	private ShareDialog dialog;
	private NoteDialog noteDialog;

	private String host, domain, applicationId, username, password;
	private int port;
	
	private Space currentActiveSpace;
	
	private int checkedEvent = -1;
	
	private ProgressDialog mProgressDialog;
	private List<Space> spaces;
	private List<DataObject> dataObjects;
	private List<GenericSensorData> genericSensorDataObjects;
	
	/**
	 * Handlers used for MIRROR Spaces.
	 */
	public ConnectionConfigurationBuilder connectionConfigurationBuilder; 
	public ConnectionConfiguration connectionConfig; 
	public ConnectionHandler connectionHandler; 
	public SpaceHandler spaceHandler; 
	public String dbName = "sdkcache_wdgapp"; 
	public DataHandler dataHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
   
        
        happyButton = (ImageView) findViewById(R.id.imageViewHappy);
        neutralButton = (ImageView) findViewById(R.id.imageViewNeutral);
        sadButton = (ImageView) findViewById(R.id.imageViewSad);
        rescueButton = (ImageView) findViewById(R.id.imageViewRescue);
        noteButton = (ImageView) findViewById(R.id.imageViewNote);
        
        happyButton.setOnClickListener(this); 
        neutralButton.setOnClickListener(this);
        sadButton.setOnClickListener(this);
        rescueButton.setOnClickListener(this);
        noteButton.setOnClickListener(this);
 
        SharedPreferences settings = getSharedPreferences("Settings", 0);
        
        host = settings.getString("host", getString(R.string.host));
    	domain = settings.getString("domain", getString(R.string.domain));
    	applicationId = getString(R.string.application_id);
    	port = settings.getInt("port", 5222); //5222 is standard port.
    	username = settings.getString("username", "");
    	password = settings.getString("password", "");
    	
        //Configure connection
		connectionConfigurationBuilder = new ConnectionConfigurationBuilder(domain,applicationId);
        connectionConfigurationBuilder.setHost(host);
        connectionConfigurationBuilder.setPort(port);
        connectionConfig = connectionConfigurationBuilder.build();
        connectionHandler = new ConnectionHandler(username, password, connectionConfig);
        spaceHandler = new SpaceHandler(getBaseContext(), connectionHandler, dbName);
        dataHandler = new DataHandler(connectionHandler, spaceHandler);
        
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
	                    || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
	    	 new UserLoginTask().execute();
	    }
        
        
       
        
    }

	@Override
	public void onClick(View v) {
		
		dialog = new ShareDialog();
		
		switch (v.getId()) {
		case R.id.imageViewHappy:
			happyButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));			
			dialog.setArguments(setDataAndGetBundle("I'm happy", R.drawable.happy_blue));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewNeutral:
			neutralButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			dialog.setArguments(setDataAndGetBundle("I'm so and so", R.drawable.neutral_blue));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewSad:
			sadButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			dialog.setArguments(setDataAndGetBundle("I'm sad", R.drawable.sad_blue));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewRescue:
			rescueButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			dialog.setArguments(setDataAndGetBundle("I rescued someone", R.drawable.rescue_icon));
			dialog.show(getSupportFragmentManager(), "shareDialog");
			break;
		case R.id.imageViewNote:
			noteButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.drawable.image_click));
			noteDialog = new NoteDialog();
			noteDialog.setArguments(setDataAndGetBundle("note", R.drawable.note));
			noteDialog.show(getSupportFragmentManager(), "note_dialog");
			break;
		}	
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	private Bundle setDataAndGetBundle (String text, int imageID) {
		b = new Bundle();
		currentText = text;
		currentImageID = imageID;
		b.putString("text", currentText);
		b.putInt("imageID", currentImageID);
		return b;
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
			return true;
	
		case R.id.menu_logout:
			new SettingsDialog().show(getSupportFragmentManager(), "settings");
		
			
			return true;
			
		case R.id.menu_changeSpace:
			Bundle b = new Bundle();
			ArrayList<String> adapter = new ArrayList<String>();
			for (Space space : spaces) {
				adapter.add(space.getName());
			}
			b.putStringArrayList("events", adapter);
			b.putInt("checkedEvent", checkedEvent);
			
			ChooseEventDialog dialog = new ChooseEventDialog();
			dialog.setArguments(b);
			dialog.show(getSupportFragmentManager(), "event");
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onShareDialogButtonClicked() {
		//Parser.buildSimpleXMLObject(watchitData, latitude, longitude)
		
	}

	@Override
	public void onShareNoteButtonClicked(String notes) {
		if (currentText.equals("note")) {
			//TODO: bygg dataobject med unit note og value text med notes.
			//publish det.
		}
		
	}

	@Override
	public void saveSettingsButtonClick() {
		 SharedPreferences settings = getSharedPreferences("Settings", 0);
	        host = settings.getString("host", getString(R.string.host));
	    	domain = settings.getString("domain", getString(R.string.domain));
	    	applicationId = getString(R.string.application_id);
	    	port = settings.getInt("port", 5222); //5222 is standard port.
	    	username = settings.getString("username", "");
	    	password = settings.getString("password", "");
	    	
	        //Configure connection
			connectionConfigurationBuilder = new ConnectionConfigurationBuilder(domain,applicationId);
	        connectionConfigurationBuilder.setHost(host);
	        connectionConfigurationBuilder.setPort(port);
	        connectionConfig = connectionConfigurationBuilder.build();
	        connectionHandler = new ConnectionHandler(username, password, connectionConfig);
	        spaceHandler = new SpaceHandler(getBaseContext(), connectionHandler, dbName);
	        dataHandler = new DataHandler(connectionHandler, spaceHandler);
	        
	        new UserLoginTask().execute();
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress("Server", "Connecting to server...");
		}
		@Override
		protected Boolean doInBackground(Void... params) {
	      try {
	    	  connectionHandler.connect();
	      } catch (Exception e) {
	    	  e.printStackTrace();
	    	  return false;
	      }
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			dismissProgress();

			if (success) {
				Toast.makeText(getBaseContext(), "Logged in :)", Toast.LENGTH_SHORT).show();
				spaceHandler.setMode(Mode.ONLINE);
				dataHandler.setMode(Mode.ONLINE);
				new GetSpacesTask().execute();
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Is the server settings and/or username/password correct?/Are you connected to the internet?", Toast.LENGTH_LONG).show();
				new GetSpacesTask().execute();
			}
		}

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
	    
	    private class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {    		
	    		@Override
	    		public void onPreExecute() {
	    			super.onPreExecute();
	    			showProgress("Syncing", "Syncing....");
	    			
	    		}
	    		protected Boolean doInBackground(Void...params) {
	    			try {
	    					spaces =  spaceHandler.getAllSpaces();
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    				return false;
	    			}
	    			return true;
	    		}

	    		protected void onPostExecute(final Boolean success) {
	    			dismissProgress();
	    			if (success) {
	    				Log.d("GETSPACESTASK", "successfully fetched spaces");

	    			} else {
	    				Toast.makeText(getBaseContext(), "Failed trying to sync events. Try refresh", Toast.LENGTH_SHORT).show();
	    				Log.d("GETSPACESTASK", "Something wentwrong when syncing events. Try Refresh." );
	    			}
	    		}
	    }


		@Override
		public void eventChosen(int which) {
			try {
				dataHandler.registerSpace(spaces.get(which).getId());
				currentActiveSpace = spaces.get(which);
				checkedEvent = which;
			} catch (UnknownEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		private class PublishDataTask extends AsyncTask<Void, Void, Boolean> {

			private DataObject mDataObject;
			private String mSpaceId;
			

			public PublishDataTask (DataObject dataObject, String spaceId) {
				mDataObject = dataObject;
				mSpaceId = spaceId;
			}
			
		      @Override
		        protected void onPreExecute() {
		        	super.onPreExecute();
		        	showProgress("Sending data", "Sending data...");
		        }
			
			@Override
			protected Boolean doInBackground(Void... params) {
		    	 try {

					  dataHandler.publishDataObject(mDataObject, mSpaceId);
					  Log.d("publish", "dataobject: " + mDataObject.toString());
					  Log.d("publish", "just published dataobject with id: " + mDataObject.getId());
		    	 }
					  catch (NullPointerException e) {
						  e.printStackTrace();
						  Log.d("ERROR:", "trying again...");
						  new PublishDataTask(mDataObject, mSpaceId).execute();
					  
					  
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("ERROR:", "failed to publish dataobject with id: " + mDataObject.getId() );
					return false;
				}
		    	 return true;
			
			}

			protected void onPostExecute(final Boolean success) {
			dismissProgress();
				if (success) {
				Log.d("publish", "success");
				} else {
				Log.d("ERROR:", "Something went wrong");
				Toast.makeText(getBaseContext(), "Failed to send data.", Toast.LENGTH_SHORT).show();
				//mActivity.showToast("Failed to publish dataobject.....");
				}
			}

		}
}
