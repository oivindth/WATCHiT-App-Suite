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

public class MainActivity extends FragmentActivity{

	private ConnectionConfiguration connectionConfig;
	private XMPPConnection connection;
	private Context context;
	private String dbName = "sdkcache";
	private SpaceHandler spaceHandler;
	private List<de.imc.mirror.sdk.Space> spaces;
	private ListView listOfSpaces;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();
    	
    	MainFragment fragmentMain = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain).commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
 
    public void getSpaces(View view) {
    	new GetSpacesTask().execute();
    }
    public void getTheData(View view) {
    	new GetData().execute();
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
			    	    	transaction.addToBackStack("tag");
			    	    	// Commit the transaction
			    	    	transaction.commit();
	
			    	         String selectedFromList =(String) (listOfSpaces.getItemAtPosition(myItemInt));
			    	         
			    	         Space space = spaceHandler.getSpace(selectedFromList);
			    	         System.out.println(space.getId());
			    	         
			    	         int members = space.getMembers().size();
			    	         Bundle b = new Bundle();
			    	         b.putInt("memberCount", members);
			    	         b.putString("name", space.getName());
			    	         b.putString("id", space.getId());
			    	         sf.setArguments(b);        
			    	       }                 
			    	 });
			         //new GetData().execute();
	    	}
 	}
    
 private class GetData extends AsyncTask<String, Integer, String> {
	 
		@Override
		protected String doInBackground(String... params) {
			DataHandler dataHandler =
	    		    new de.imc.mirror.sdk.android.DataHandler(context, connection, "oivind",
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
