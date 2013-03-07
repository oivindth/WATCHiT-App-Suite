package activities;

import parsing.Parser;

import com.example.watchit_connect.R;
import com.example.watchit_connect.R.id;
import com.example.watchit_connect.R.layout;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;
import fragments.SpacesFragment;
import fragments.SpacesFragment.OnSpaceItemSelectedListener;
import android.R.menu;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        getMenuInflater().inflate(R.menu.activity_space, menu);
        return true;
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
            	sApp.dataHandler.setMode(Mode.ONLINE);
            	sApp.dataHandler.addDataObjectListener(myListener);
            	new GetDataFromSpaceTask(this ,"team#38").execute();
            	DataObject dob =  Parser.buildDataObjectFromSimpleXMl(Parser.buildSimpleXMLObject
            			("HelloWorld", "44.84866", "10.30683"), "admin" + "@" + sApp.connectionHandler.getConfiguration().getDomain());
            	Log.d("BASEACTIVITY", dob.toString());
            	new PublishDataTask(this, dob, "team#38").execute();
            	  
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
		new GetDataFromSpaceTask(this, space.getId()).execute(); //TODO: To heavy?
	}
        
}
