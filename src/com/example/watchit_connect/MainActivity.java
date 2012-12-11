package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.ProviderInitializer;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LogInDialogFragment.LogInDialogListener {

	private ConnectionConfiguration connectionConfig;
	private XMPPConnection connection;
	private Context context;
	private String domain, dbName = "sdkcache";;
	private String host, userName, userPass;
	private int port = 5222;
	private SpaceHandler spaceHandler;
	private List<de.imc.mirror.sdk.Space> spaces;
	private String resource = "myapp-oivind"; // use an unique identifier for your application 
	private ListView listOfSpaces;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();
        domain = getString(R.string.domain);
    	host = getString(R.string.host);
    	
    	MainFragment fragmentMain = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain).commit();
        
      
    	
		//userName = "oivind";
		//userPass = "mirror";
		
		//new ConnectTask().execute();
    	
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
    public void getTheData(View view) {
    	new GetData().execute();
    }
    
	public void showLogInDialog() {
        DialogFragment dialog = new LogInDialogFragment();
        dialog.show(getSupportFragmentManager(), "LogInDialogFragment");
    }

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        
        //ListView lw = (ListView) findViewById(R.id.listView1);
        ListView lw = (ListView)  findViewById(R.id.listViewMainFragment);
        
        lw.setEmptyView(progressBar);

        
        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
		
		
		EditText editUserName = (EditText) dialog.getDialog().findViewById(R.id.username);
		EditText editUserPass = (EditText) dialog.getDialog().findViewById(R.id.password);
		userName = editUserName.getText().toString();
		userPass = editUserPass.getText().toString();
		
		userName = "oivind";
		userPass = "mirror";
		
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
	        connection = new XMPPConnection(connectionConfig);
	        
	        try {
	        	connection.connect();
	        	//if ( connection.getUser() != null);
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
			//ProgressBar bar  = (ProgressBar) findViewById(R.id.progressBar1);
			//bar.setVisibility(View.GONE);
			Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
			new GetSpacesTask().execute();
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

		protected void onPostExecute(String result) {
			listOfSpaces = (ListView) findViewById(R.id.listViewMainFragment);
			ArrayAdapter<String> arrayAdapter =      
			         new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, spacesNames);
			         listOfSpaces.setAdapter(arrayAdapter);
			         Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
			         
			         listOfSpaces.setOnItemClickListener(new OnItemClickListener() {
			    	       public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
			    	    	   
			    	    	   
			    	    	   FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			    	    	   SpaceFragment sf = new SpaceFragment();
			    	    	// Replace whatever is in the fragment_container view with this fragment,
			    	    	// and add the transaction to the back stack so the user can navigate back
			    	    	transaction.replace(R.id.fragment_container, sf);
			    	    	transaction.addToBackStack(null);
			    	    	// Commit the transaction
			    	    	transaction.commit();
			    	    	   
			    	  
			    	    	   
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
			    	         
			    	         List<String> spaceObject = new ArrayList<String>();
			    	         spaceObject.add("Id: " +b.getString("id"));
			    	         spaceObject.add("Name: " +b.getString("name"));
			    	         spaceObject.add("Members: " + b.getInt("memberCount"));
			    	         
			    	      
			    	        
			    	    
			    	        		
			    	        		   ArrayAdapter<String> arrayAdapter2 =      
						    			         new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, spaceObject);
						    	       ListView  lw3 = (ListView) findViewById(R.id.listViewSpaceFragment);
						    	        lw3.setAdapter(arrayAdapter2);
			    	        		
			    	         
			    	       }                 
			    	 });
			         new GetData().execute();
	    	}
 	}
    
 private class GetData extends AsyncTask<String, Integer, String> {
	 
		@Override
		protected String doInBackground(String... params) {
			DataHandler dataHandler =
	    		    new de.imc.mirror.sdk.android.DataHandler(context, connection, userName,
	    		        (de.imc.mirror.sdk.android.SpaceHandler) spaceHandler);
			dataHandler.setConnected(true);
			
			
	       List<de.imc.mirror.sdk.DataObject> liste = new ArrayList<de.imc.mirror.sdk.DataObject> ();
	       Space space = spaceHandler.getSpace("team#39");
	       System.out.println(space.getId());
	       
	       try {
			dataHandler.registerSpace(space.getId());
			
		} catch (UnknownEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	       DataObjectListener myListener = new DataObjectListener() {
	    	   // implement this interface in a controller class of your application
	    	   @Override
	    	   public void handleDataObject(de.imc.mirror.sdk.DataObject dataObject, String spaceId) {
	    	     String objectId = dataObject.getId();
	    	     System.out.println(
	    	         "I received object " + objectId + " published on space " + spaceId + "!");
	    	 } };
	    	 dataHandler.addDataObjectListener(myListener);
	       
			try {
				
			liste =	dataHandler.retrieveDataObjects(space.getId());
			System.out.println(liste.size());
			} catch (UnknownEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (de.imc.mirror.sdk.DataObject dataObject : liste) {
				System.out.println("<"+dataObject.getElement().getName() +">");
				for (Element el : dataObject.getElement().getChildren()) {
					System.out.println("<" + el.getName()+ ">" );
					for (Content con : el.getContent()) {
						System.out.println("content ni:" + con.getValue());
					}
					
					for (Attribute att2 : el.getAttributes()) {
						System.out.print("<" +att2.getName() +"> " + att2.getValue() + "/>" + "\n" );
					}
					System.out.println("</" + el.getName()+ ">" );
				}
				System.out.println("</"+dataObject.getElement().getName() +">");
			}
		return "";	
		}
	
		protected void onPostExecute(String result) { 	                   
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
		        
		       
		       
				publishProgress(100); 
		        
	         return result;
	     }*/
	
}
