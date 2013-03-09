package activities;

import parsing.Parser;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.R;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataObject;
import fragments.SpacesFragment;
import fragments.SpacesFragment.OnSpaceItemSelectedListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import asynctasks.GetDataFromSpaceTask;
import asynctasks.GetSpacesTask;
import asynctasks.PublishDataTask;

/**
 * 
 * @author oivindth
 *
 */
public class SpaceActivity extends BaseActivity implements OnSpaceItemSelectedListener  {

	
	private SpacesFragment fragment = new SpacesFragment();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spaces);
        
        fragment = new SpacesFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment,"spaces").commit(); 
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    }

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("Save")
        .setIcon(R.drawable.ic_navigation_refresh)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    menu.add("Switch User")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    return super.onCreateOptionsMenu(menu);
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
                finish();
                return true;
    	
            case R.id.menu_sync:
            	new GetSpacesTask(this).execute();
            	new GetDataFromSpaceTask(this ,sApp.currentActiveSpace.getId()).execute();
            	DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
            			("HelloWorld", "44.84866", "10.30683"), "admin" + "@" + sApp.connectionHandler.getConfiguration().getDomain());
            	Log.d("BASEACTIVITY", dob.toString());
            	new PublishDataTask(this, dob, sApp.currentActiveSpace.getId()).execute();
            	  
            	//showProgress("...", "zz");
            	return true;
      
            default:
                return super.onOptionsItemSelected(item);
        }
	
    }
	
	@Override
	public void onSpaceItemSelected(int position) {
		//Space space = app.spaceHandler.getAllSpaces().get(position); //TODO: Too heavy.
		Space space = sApp.spacesInHandler.get(position);
		Log.d("SIZE", "s : " + sApp.spacesInHandler.size());
		sApp.switchSpace(space);
		//app.dataObjects = new ArrayList<de.imc.mirror.sdk.DataObject>();
		//new GetDataFromSpaceTask(this, space.getId()).execute(); //TODO: To heavy?
		showToast("Noe registered to event: " + space.getName());
		finish();
		
	}
        
}
