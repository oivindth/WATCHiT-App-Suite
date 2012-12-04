package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ProviderInitializer;
import de.imc.mirror.sdk.android.SpaceHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LogInDialogFragment.LogInDialogListener {

	private ConnectionConfiguration connectionConfig;
	private Context context;
	private String domain, dbName = "sdkcache";;
	private String host, userName, userPass;
	private int port = 5222;
	private SpaceHandler spaceHandler;
	private List<de.imc.mirror.sdk.Space> spaces;
	private String resource = "myapp-oivind"; // use an unique identifier for your application 
	ListView listOfSpaces;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        domain = getString(R.string.domain);
    	host = getString(R.string.host);
    	
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
    
	public void showLogInDialog() {
        DialogFragment dialog = new LogInDialogFragment();
        dialog.show(getSupportFragmentManager(), "LogInDialogFragment");
    }

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		EditText editUserName = (EditText) dialog.getDialog().findViewById(R.id.username);
		EditText editUserPass = (EditText) dialog.getDialog().findViewById(R.id.password);
		userName = editUserName.getText().toString();
		userPass = editUserPass.getText().toString();
		
		new ConnectTask().execute();
	}

	
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(context, "Y U NO LOG IN???", Toast.LENGTH_SHORT).show();
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
	        
	        try {
	        	connection.connect();
	        	connection.login(userName, userPass, resource);
	        	System.out.println( connection.getUser());
	        	result = "Login success";
	      } catch (XMPPException e) {
	    	  	result = "Login failed.";
	    	  	e.printStackTrace();
	      }
	         // instantiate the implementation for Android
			 spaceHandler = new de.imc.mirror.sdk.android.SpaceHandler(context,connection,userName,domain,dbName);
			 spaceHandler.setConnected(true);
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
				spacesNames.add(space.getId());
				
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
			listOfSpaces = (ListView) findViewById(R.id.listView1);
			ArrayAdapter<String> arrayAdapter =      
			         new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, spacesNames);
			         listOfSpaces.setAdapter(arrayAdapter);
			         Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
			         
			         listOfSpaces.setOnItemClickListener(new OnItemClickListener() {
			    	       public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
			    	         String selectedFromList =(String) (listOfSpaces.getItemAtPosition(myItemInt));
			    	         Toast.makeText(context, "select: " + selectedFromList, Toast.LENGTH_SHORT).show();
			    	         
			    	         Space space = spaceHandler.getSpace(selectedFromList);
			    	         System.out.println(space.getId());
			    	         
			    	         int members = space.getMembers().size();
			    	         Bundle b = new Bundle();
			    	         b.putInt("memberCount", members);
			    	         b.putString("name", space.getName());
			    	         b.putString("id", space.getId());
			    	         
			    	         System.out.println(b.getString("id"));
			    	         
			    	         Intent spaceIntent = new Intent(context, SpaceActivity.class);
			    	         spaceIntent.putExtras(b);
			    	         startActivity(spaceIntent);
			    	       }                 
			    	 });
	    	}
 	}
    



 
	    /* 
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
	     }*/
	
}
