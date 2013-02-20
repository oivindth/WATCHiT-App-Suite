package com.example.watchit_connect.Spaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;

import parsing.Parser;
import parsing.TrainingProcedure;
import parsing.XMLDataObject;

import com.example.watchit_connect.LoginActivity;
import com.example.watchit_connect.MainApplication;
import com.example.watchit_connect.R;
import com.example.watchit_connect.Applications.ApplicationsActivity;
import com.example.watchit_connect.Applications.mood.MoodActivity;
import com.example.watchit_connect.R.id;
import com.example.watchit_connect.R.layout;
import com.example.watchit_connect.R.menu;
import com.example.watchit_connect.R.string;
import com.example.watchit_connect.Spaces.SpaceFragment.OnSpaceInfoSelectedListener;
import com.example.watchit_connect.Spaces.SpacesFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataModel;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.android.SpaceConfiguration;
import de.imc.mirror.sdk.android.SpaceMember;

import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts.Settings;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SpacesActivity extends FragmentActivity implements OnSpaceItemSelectedListener, OnSpaceInfoSelectedListener{

	private Context context;
	private SpaceHandler spaceHandler;
	private List<Space> spaces;
	private View updateStatusView;
	private View mainView;
	private List<String> spacesNames;
	private ConnectionConfigurationBuilder connectionConfigurationBuilder;
	private ConnectionConfiguration connectionConfig;
	private ConnectionHandler connectionHandler;
	private Space space;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spaces);
        context = this.getBaseContext();
        MainApplication app =  (MainApplication) getApplication();
        
        //Do this in LoginActivity to update variables in mainapplication object.
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        app.userName = settings.getString("username", ""); 
        userName = settings.getString("username", "");
        password = settings.getString("password", "");
        
        userName = "admin";
        password = "opf2013ntnu";
        
        updateStatusView = findViewById(R.id.update_status);
        mainView = findViewById(R.id.main_view);
        
    	SpacesFragment fragmentMain = new SpacesFragment();
    	//SpaceFragment spaceFragment = new SpaceFragment();
    	//getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, spaceFragment, "space").commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain,"main").commit();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	 ConfigureSpaces();
    	
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
            	return true;
            case R.id.menu_config:
            	showProgress(true);
            	 new PublishDataTask().execute();
            	return true;	
            case R.id.menu_Home:
            	Intent intent = new Intent(this, ApplicationsActivity.class);
                //intent.setClass(SpacesActivity.this, ApplicationsActivity.class);
                startActivity(intent);
                //finish();
                return true;	
            default:
                return super.onOptionsItemSelected(item);
        }
     
    }
    
	private void ConfigureSpaces() {
		 //Configure connection
        connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
        connectionConfigurationBuilder.setHost(getString(R.string.host));
        connectionConfig = connectionConfigurationBuilder.build();
        connectionHandler = new ConnectionHandler(userName, password, connectionConfig);
        
        spaceHandler = new SpaceHandler(context, connectionHandler, dbName);
        spaceHandler.setMode(Mode.ONLINE);
		spaces = spaceHandler.getAllSpaces();
	    spacesNames = new ArrayList<String> ();
		for (de.imc.mirror.sdk.Space space : spaces) {
			spacesNames.add(space.getId());
		}
		SpacesFragment mainfragment = (SpacesFragment)  getSupportFragmentManager().findFragmentByTag("main");
		mainfragment.UpdateSpaces(spacesNames); // Do this with db to persist ze data?
	}
	
	
	
	
	
	private class PublishDataTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void...params) {
			
		      try {
		    	  if (connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
		    		  connectionHandler.connect();
		    	  
		      } catch (ConnectionStatusException e) {
		    	  e.printStackTrace();
		    	  return false;
		      }
			/*
			// create space configuration with the current user as space moderator
	    	 Space.Type type = Space.Type.TEAM;
	    	 String name = "Dream Team";
	    	 String owner = connectionHandler.getCurrentUser().getBareJID();
	    	 boolean isPersistent = false;
	    	 SpaceConfiguration spaceConfig = new SpaceConfiguration(type, name, owner, isPersistent);

	    	 // add the user bob as space member
	    	 String bobsJID = "bob" + "@" + connectionHandler.getConfiguration().getDomain();
	    	 spaceConfig.addMember(new SpaceMember(bobsJID, SpaceMember.Role.MEMBER));

	    	 // create space with this configuration
	    	 Space myNewTeamSpace = null;
	    	 try {
	    	 myNewTeamSpace = spaceHandler.createSpace(spaceConfig);
	    	 } catch (SpaceManagementException e) {
	    	 // failed to create space
	    	 // add proper exception handling
	    	 } catch (ConnectionStatusException e) {
	    	 // cannot create a space when offline
	    	 // add proper exception handling
	    	 }*/
	    	 
	    	 DataHandler dataHandler = new DataHandler(connectionHandler, spaceHandler);
	    	 dataHandler.setMode(Mode.ONLINE);
	    	 
	    	 
	    	 try {
				dataHandler.registerSpace("team#3");
			} catch (UnknownEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	 DataObjectListener myListener = new DataObjectListener() {
	    	 // implement this interface in a controller class of your application
	   

			@Override
			public void handleDataObject(
					de.imc.mirror.sdk.DataObject dataObject, String spaceId) {
				 String objectId = dataObject.getId();
		    	 System.out.println("Received object " + objectId + " from space " + spaceId);
			}

	    	 };
	    	 dataHandler.addDataObjectListener(myListener);
	    	
	    	// create the data object using the object builder
	    	 DataObjectBuilder dataObjectBuilder =
	    	 new DataObjectBuilder("trainingprocedure", "mirror:application:watchIt_Reflection_App:trainingprocedure");
	    	 dataObjectBuilder.addElement("Time", "12", false);
	    	 dataObjectBuilder.addElement("step1", "3", false).addElement("step2", "4", false);
	    	 //dataObjectBuilder.addCDTCreationInfo(date, connectionHandler.getCurrentUser().getBareJID(), application);
	    	 DataObject dataObject = dataObjectBuilder.build();
	    	 // publish the data
	    	 try {
	    	 dataHandler.publishDataObject(dataObject, "team#3");
	    	 DataObject dddd = (DataObject) dataHandler.retrieveDataObjects("team#3").get(0);
	    	 System.out.println(dddd.getElement().getName());
	    	 } catch (UnknownEntityException e) {
	    	 // space doesn't exist or is not accessible.
	    	 // add proper exception handling
	    		 System.out.println("space not exist or not accesible.");
	    	 }
	    	  
	    
	    	 
System.out.println("now attempt to get data objects...... ");
	    	 
	    	 try {
				List<de.imc.mirror.sdk.DataObject> dataobjects = dataHandler.retrieveDataObjects("team#3");
				System.out.println("getting data objects...");
				System.out.println("size of data: " + dataobjects.size());
				
				 for (de.imc.mirror.sdk.DataObject dobj : dataobjects) {
					List<Attribute> attributes =  dobj.getElement().getAttributes();
					
					Parser p = new Parser();
					TrainingProcedure oddgeir = (TrainingProcedure) p.DeSerialize(dobj.toString());
					System.out.println(oddgeir.getId());
					//p.Serialize(oddgeir, getBaseContext());
					
				   System.out.println("dataobject totring" + dobj.toString());
				   
				   p.Serialize(oddgeir,  getBaseContext());
				   
				
					for (Attribute attribute : attributes) {
						System.out.println("attr name: " + attribute.getName());
					}
						System.out.println("name of data: " + dobj.getElement().getName()); 
						System.out.println("content of the data:**************");
						List<Content> contents = dobj.getElement().getContent();
						for (Content content : contents) {
							//content.getParent().getContent()
							System.out.println(content.getValue()); 

						 //Må nok kjøre Simple xml på dataobjekter for å PARSE. 
						}
						System.out.println("*************");
						System.out.println("data value: "+  dobj.getElement().getValue()); 
						System.out.println("data text: " +dobj.getElement().getText()); 
						System.out.println("xml til streng: " + dobj.getElement().toString()); 
					}
			} catch (UnknownEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	 
   	 return true;
		}

		protected void onPostExecute(final Boolean success) {
			showProgress(false);
			if (success) {
				Toast.makeText(getBaseContext(), "Data published", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
 	}	
	
	
 private class GetSpacesTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void...params) {
	      try {
	    	  if (connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
	    		  connectionHandler.connect();
	    	  
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  return false;
	      }
			spaces = spaceHandler.getAllSpaces();
		    spacesNames = new ArrayList<String> ();
			for (Space space : spaces) {
				spacesNames.add(space.getId());
			}
			return true;
		}

		protected void onPostExecute(final Boolean success) {
			showProgress(false);
			if (success) {
				SpacesFragment mainfragment = (SpacesFragment)  getSupportFragmentManager().findFragmentByTag("main");
				mainfragment.UpdateSpaces(spacesNames); // Do this with db to persist ze data?
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
 	}
    
 	@Override
	public void onSpaceItemSelected(int position) {
	    space = spaces.get(position);
 		SpaceFragment spaceFrag = (SpaceFragment)
                getSupportFragmentManager().findFragmentByTag("space");

        if (spaceFrag != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the SpaceFragment to update its content
            spaceFrag.UpdateSpaceInfo(space.getName(), space.getId(), space.getMembers().size());
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected space
            SpaceFragment spaceFragment = new SpaceFragment();
            //int members = space.getMembers().size();
            Bundle b = new Bundle();
            b.putInt("pos", position);
            //b.putString("name", space.getName());
            //b.putString("id", space.getId());
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
 	
 	/**
 	 * Create a private space if it doesen't exist and returns the space
 	 * @return
 	 */
 	private Space createPrivateSpace() {
 	    Space myPrivateSpace = spaceHandler.getDefaultSpace();
 	    if (myPrivateSpace == null) {
 	      try {
 	        myPrivateSpace = spaceHandler.createDefaultSpace();
 	      } catch (SpaceManagementException e) {
 	    	  e.printStackTrace();
 	    	  Toast.makeText(getBaseContext(), "Failed to create space. ", Toast.LENGTH_SHORT).show();
 	        return null;
 	      } catch (ConnectionStatusException e) {
 	         e.printStackTrace();
 	         Toast.makeText(getBaseContext(), "You need a connection to the internet to create a space", Toast.LENGTH_SHORT).show();
 	         return null;
 	      } 
 	    }
 	    return myPrivateSpace;
 	}
 	
 	
 	
 	public Space getSpace (int pos) {
 		return spaces.get(pos);
 	}
 	public Set<de.imc.mirror.sdk.SpaceMember> getCurrentSpaceMembers() {
 		return space.getMembers();
 		
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

	@Override
	public void onSpaceInfoSelected(int position) {
	    // if position clicked is not the members field just return.
		if (position != 2 ) return;
		
	    //memberfragment here
		SpaceMembersFragment spacemembersfragment = 
				(SpaceMembersFragment) getSupportFragmentManager().findFragmentByTag("spacemembers");
		
        if (spacemembersfragment != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the SpaceFragment to update its content
            //spaceFrag.UpdateSpaceInfo(space.getName(), space.getId(), space.getMembers().size());
        	
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected space
            SpaceMembersFragment spaceMembersFragment = new SpaceMembersFragment();
           
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, spaceMembersFragment, "spacemembers");
            transaction.addToBackStack("space");

            // Commit the transaction
            transaction.commit();
		
	}
 
	
}
	
}
