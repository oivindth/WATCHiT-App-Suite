package com.example.watchit_connect.Spaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.watchit_connect.MainApplication;
import com.example.watchit_connect.R;
import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.Spaces.SpaceFragment.OnSpaceInfoSelectedListener;
import com.example.watchit_connect.Spaces.SpacesFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

public class SpacesActivity extends BaseActivity implements OnSpaceItemSelectedListener, OnSpaceInfoSelectedListener{

	private List<Space> spaces;
	private View updateStatusView, mainView;
	private List<String> spacesNames;
	private Space space;
	private MainApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spaces);
        app =  (MainApplication) getApplication();
        
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
    	SpaceHandler spaceHandler = new SpaceHandler(getBaseContext(), MainApplication.connectionHandler, MainApplication.dbName);
    	spaceHandler.setMode(Mode.ONLINE);
    	MainApplication.spaceHandler = spaceHandler;
  
			
    		spaces = spaceHandler.getAllSpaces();
    	    spacesNames = new ArrayList<String> ();
    		for (de.imc.mirror.sdk.Space space : spaces) {
    			spacesNames.add(space.getId());
    		}
    		SpacesFragment mainfragment = (SpacesFragment)  getSupportFragmentManager().findFragmentByTag("main");
    		mainfragment.UpdateSpaces(spacesNames); // Do this with db to persist ze data?
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
    
    
	private class PublishDataTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void...params) {
			
		      try {
		    	  if (app.connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
		    		  app.connectionHandler.connect();
		    	  
		      } catch (ConnectionStatusException e) {
		    	  e.printStackTrace();
		    	  return false;
		      }
	
	    	 //DataHandler dataHandler = new DataHandler(app.getConnectionHandler(), app.getSpaceHandler());
	    	 //dataHandler.setMode(Mode.ONLINE);
	    	 
	    	 //try {
				//dataHandler.registerSpace("team#3");
			//} catch (UnknownEntityException e1) {
				// TODO Auto-generated catch block
			//	e1.printStackTrace();
			//}
	    	 DataObjectListener myListener = new DataObjectListener() {
	    	 // implement this interface in a controller class of your application
	   
			@Override
			public void handleDataObject(
					de.imc.mirror.sdk.DataObject dataObject, String spaceId) {
				 String objectId = dataObject.getId();
		    	 System.out.println("Received object " + objectId + " from space " + spaceId);
			}

	    	 };
	    	// dataHandler.addDataObjectListener(myListener);
	    	
	    	 System.out.println("now attempt to get data objects...... ");
	    	 
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
	    	  if (app.connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
	    		  app.connectionHandler.connect();
	    	  
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  Toast.makeText(getBaseContext(), "Could not make a connection. Is wifi or 3g turned on?", Toast.LENGTH_SHORT).show();
	    	  return false;
	      }
			spaces = app.spaceHandler.getAllSpaces();
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
 	 * Get private space. If private space doesen't exist yet, one is created.
 	 * 
 	 * @return Space
 	 */
 	private Space createPrivateSpace() {
 	    Space myPrivateSpace = app.spaceHandler.getDefaultSpace();
 	    if (myPrivateSpace == null) {
 	      try {
 	        myPrivateSpace = app.spaceHandler.createDefaultSpace();
 	      } catch (SpaceManagementException e) {
 	    	  e.printStackTrace();
 	    	  Toast.makeText(getBaseContext(), "Failed to create space... ", Toast.LENGTH_SHORT).show();
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
 		 * Shows the progress UI.
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
