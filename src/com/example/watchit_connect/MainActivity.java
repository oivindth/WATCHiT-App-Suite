package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.example.watchit_connect.MainFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.ProviderInitializer;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends FragmentActivity implements OnSpaceItemSelectedListener{

	private XMPPConnection connection;
	private Context context;
	private String dbName = "sdkcache";
	private SpaceHandler spaceHandler;
	private List<de.imc.mirror.sdk.Space> spaces;
	private ListView listOfSpaces;
	private String userName, password;
	private View updateStatusView;
	private View mainView;
	private List<String> spacesNames;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();
    	
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        userName = settings.getString("username", "");
        password = settings.getString("password", "");
        
        updateStatusView = findViewById(R.id.update_status);
        mainView = findViewById(R.id.main_view);
        
    	MainFragment fragmentMain = new MainFragment();
    	SpaceFragment spaceFragment = new SpaceFragment();
    	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, spaceFragment, "space").commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain,"main").commit();
        
        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
            	showProgress(true);
            	new GetSpacesTask().execute();
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			updateStatusView.setVisibility(View.VISIBLE);
			updateStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							updateStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mainView.setVisibility(View.VISIBLE);
			mainView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mainView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			updateStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mainView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
    
	
 private class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {
	 
		@Override
		protected Boolean doInBackground(Void...params) {
			//Before establishing a XMPP connection, a provider manager has to be initialized properly 
	        //to receive data packages
	        ProviderInitializer.initializeProviderManager();

	        //prepare xmp connection	        
		    ConnectionConfiguration connectionConfig = new ConnectionConfiguration(getString(R.string.host), Integer.parseInt( getString(R.string.port) ) ); 
	        XMPPConnection connection = new XMPPConnection(connectionConfig);
	        
	        try {
	        	connection.connect();
	        	connection.login(userName, password, getString(R.string.resource));
	      } catch (XMPPException e) {
	    	  	return false;
	      }
			
	        Context context = getApplicationContext();
	        String domain = getString(R.string.domain);
	        spaceHandler = new de.imc.mirror.sdk.android.SpaceHandler(context, connection, userName, domain, dbName);
	        spaceHandler.setConnected(true);
			spaces = spaceHandler.getAllSpaces();
			
		    spacesNames = new ArrayList<String> ();
			for (de.imc.mirror.sdk.Space space : spaces) {
				spacesNames.add(space.getId());
			}
			return true;
		}

		protected void onPostExecute(final Boolean success) {
			showProgress(false);
			if (success) {
				MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");
				mainfragment.UpdateSpaces(spacesNames); // Do this with db to persist ze data?
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
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

 	@Override
	public void onSpaceItemSelected(int position) {
 	
 		spaces = spaceHandler.getAllSpaces();
		
	    Space space = spaces.get(position);
	
 		SpaceFragment spaceFrag = (SpaceFragment)
                getSupportFragmentManager().findFragmentByTag("space");

        if (spaceFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            spaceFrag.UpdateSpaceInfo("name", "id", "members");
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            SpaceFragment spaceFragment = new SpaceFragment();
            int members = space.getMembers().size();
            Bundle b = new Bundle();
            b.putInt("memberCount", members);
            b.putString("name", space.getName());
            b.putString("id", space.getId());
            spaceFragment.setArguments(b);  
          
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, spaceFragment, "space");
            transaction.addToBackStack("main");

            // Commit the transaction
            transaction.commit();
        }
 		
 
    	     
	
}
	
}
