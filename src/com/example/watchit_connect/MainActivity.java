package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;



import com.example.watchit_connect.MainFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.ConnectionStatus;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;

import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnSpaceItemSelectedListener{

	private Context context;
	private String dbName = "sdkcache";
	private SpaceHandler spaceHandler;
	private List<Space> spaces;
	private String userName, password;
	private View updateStatusView;
	private View mainView;
	private List<String> spacesNames;
	private ConnectionConfigurationBuilder connectionConfigurationBuilder;
	private ConnectionConfiguration connectionConfig;
	private ConnectionHandler connectionHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();
    	
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        userName = settings.getString("username", "");
        password = settings.getString("password", "");
        
        userName = "admin";
        password = "opf2013ntnu";
        
        updateStatusView = findViewById(R.id.update_status);
        mainView = findViewById(R.id.main_view);
        
    	MainFragment fragmentMain = new MainFragment();
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
		spaces = spaceHandler.getAllSpaces();
	    spacesNames = new ArrayList<String> ();
		for (de.imc.mirror.sdk.Space space : spaces) {
			spacesNames.add(space.getId());
		}
		
		MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");
		mainfragment.UpdateSpaces(spacesNames); // Do this with db to persist ze data?
		
	}
	
	//ide om egen task for connect men virker litt meningsløst nå tbh.
	private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void...params) {
	      try {
	    	  if (connectionHandler.getStatus() == ConnectionStatus.OFFLINE) 
	    	  connectionHandler.connect();
	    	  System.out.println(connectionHandler.getStatus());
	      } catch (ConnectionStatusException e) {
	    	  e.printStackTrace();
	    	  return false;
	      }
			return true;
		}
		protected void onPostExecute(final Boolean success) {
			showProgress(false);
			if (success) 
				spaceHandler.setMode(Mode.ONLINE); //Default is offline mode, but we have already established an xmmp connection so we want it to be online.
			else
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
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
				MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");
				mainfragment.UpdateSpaces(spacesNames); // Do this with db to persist ze data?
			} else {
				Toast.makeText(getBaseContext(), "Something went wrong. Do you have a connection?", Toast.LENGTH_SHORT).show();
			}
		}
 	}
    

 	@Override
	public void onSpaceItemSelected(int position) {
 		
	    Space space = spaces.get(position);
	
 		SpaceFragment spaceFrag = (SpaceFragment)
                getSupportFragmentManager().findFragmentByTag("space");

        if (spaceFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            spaceFrag.UpdateSpaceInfo("name", "id", 12);
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
 	
 	/**
 	 * Create a private space if it doesen't exist. 
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
 
	
}
