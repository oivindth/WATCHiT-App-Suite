package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.ProviderInitializer;
import de.imc.mirror.sdk.android.Space;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LogInDialogFragment.LogInDialogListener {

	ConnectionConfiguration connectionConfig;
	Context context;
	private String domain;
	String host;
	private int port;
	SpaceHandler spaceHandler;
	List<de.imc.mirror.sdk.Space> spaces;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        domain = getString(R.string.domain);
    	host = getString(R.string.host);
    	port = 5222;
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public void LogIn(View view) {
    	showLogInDialog();
    }
    public void getSpaces(View view) {
    	new GetSpacesTask().execute();
    }
    

	@SuppressLint("NewApi")
	public void showLogInDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new LogInDialogFragment();
        dialog.show(getFragmentManager(), "LogInDialogFragment");
        //dialog.show(getSupportFragmentManager(), "LogInDialogFragment");
    }
	@SuppressLint("NewApi")
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		 new ConnectTask().execute();
	}

	@SuppressLint("NewApi")
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
    
    private class ConnectTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute(){
		}
		@Override
		protected String doInBackground(String... params) {
		
			//Before establishing a XMPP connection, a provider manager has to be initialized properly 
	        //to receive data packages
	        ProviderInitializer.initializeProviderManager();
	        String result =" ";
	        //prepare xmp connection
		    ConnectionConfiguration connectionConfig = new ConnectionConfiguration(host, port); 
	        XMPPConnection connection = new XMPPConnection(connectionConfig);
	        
	        //Establish Connection
	        String resource = "myapp-oivind"; // use an unique identifier for your application 
	        EditText editTextUserName = (EditText) findViewById(R.id.username);
	        //String userName = editTextUserName.getText().toString();
	        
	        //EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
	       // String userPass = editTextPassword.getText().toString();
	        String userName = "oivind";
	        String userPass ="mirror";
	        try {
	        connection.connect();
	        connection.login(userName, userPass, resource);
	        connection.getAccountManager().getAccountAttribute("name");
	        result = "Login success";
	      } catch (XMPPException e) {
	    	  result = "Login failed.";
	    	  e.printStackTrace();
	      }
	         // instantiate the implementation for Android
		   	 String dbName = "sdkcache";
			 spaceHandler = new de.imc.mirror.sdk.android.SpaceHandler(context,connection,userName,domain,dbName);
			 spaceHandler.setConnected(true);
			 //spaces = spaceHandler.getAllSpaces();
			 
	      return result;   
		}
		
	    protected void onProgressUpdate(Integer... progress) {
	    	 setProgressPercent(progress[0]);
	     }

	     private void setProgressPercent(Integer integer) {
		 //pbar.setProgress(integer.intValue());
		}

		protected void onPostExecute(String result) {
			ProgressBar bar  = (ProgressBar) findViewById(R.id.progressBar1);
			bar.setVisibility(View.GONE);
			Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
		
    }
    
    
 private class GetSpacesTask extends AsyncTask<String, Integer, String> {
	 List<String> spacesNames;
		@Override
		protected String doInBackground(String... params) {
			spaces = spaceHandler.getAllSpaces();
		    spacesNames = new ArrayList<String> ();
			for (de.imc.mirror.sdk.Space space : spaces) {
				spacesNames.add(space.getName());
			}
			
			return "";
		}
	    protected void onProgressUpdate(Integer... progress) {
	    	 setProgressPercent(progress[0]);
	     }
	     private void setProgressPercent(Integer integer) {
		 //pbar.setProgress(integer.intValue());
		}

		protected void onPostExecute(String result) {
			ListView listOfSpaces = (ListView) findViewById(R.id.listView1);
			ArrayAdapter<String> arrayAdapter =      
			         new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, spacesNames);
			         listOfSpaces.setAdapter(arrayAdapter);
			         
			         Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
	     }
	     }
    



    
    
   // private class YourTask extends AsyncTask<String, Integer, String> {
	    /* 
    	protected String doInBackground(String... urls) {
		        // instantiate the implementation for Android

		   	 String dbName = "sdkcache";
		        
					SpaceHandler spaceHandler =
					new de.imc.mirror.sdk.android.SpaceHandler(context,connection,userName,domain,dbName);
					spaceHandler.setConnected(true);
				
				
				de.imc.mirror.sdk.Space space1 = null;
		        
		        List<de.imc.mirror.sdk.Space> spaces = spaceHandler.getAllSpaces();
		        for (de.imc.mirror.sdk.Space space : spaces) {
		          System.out.println("¿l: " +space.getName() +"id " + space.getId());
		         if (space.getId().equals("team#39")) space1 = space;
		          
		        }
		        
		        Space myPrivateSpace = (Space) spaceHandler.getDefaultSpace();
		        if (myPrivateSpace == null) {
		          try {
		            myPrivateSpace = (Space) spaceHandler.createDefaultSpace();
		          } catch (SpaceManagementException e) {
		            // failed to create space
		            // add proper exception handling
		          } catch (ConnectionStatusException e) {
		            // cannot create a space when offline
		            // add proper exception handling
		        } }
		       System.out.println( myPrivateSpace.getMembers().get(0).getJID()); 
		        //System.out.println(myPrivateSpace.getMembers()[0].getJID()); // JID of the current user
		        
		       
		       DataHandler dataHandler =
		    		    new de.imc.mirror.sdk.android.DataHandler(context, connection, userName,
		    		        (de.imc.mirror.sdk.android.SpaceHandler) spaceHandler);
		       List<de.imc.mirror.sdk.DataObject> liste = null;
		       try {
				dataHandler.registerSpace(space1.getId());
			} catch (UnknownEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
				try {
					
				liste =	dataHandler.retrieveDataObjects(space1.getId());
				} catch (UnknownEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(liste.size());
				for (de.imc.mirror.sdk.DataObject dataObject : liste) {
					System.out.println(dataObject.getCDMData()); 
				}
				publishProgress(100); 
		        
	         return result;
	     }
	     protected void onProgressUpdate(Integer... progress) {
	    	 setProgressPercent(progress[0]);
	     }

	     private void setProgressPercent(Integer integer) {
	    	 Button b =	(Button) findViewById(R.id.button2);
	    	 b.setText("..." + integer.intValue());
		}
		protected void onPostExecute(String result) {
	     }
	     */
	//}
	
}
