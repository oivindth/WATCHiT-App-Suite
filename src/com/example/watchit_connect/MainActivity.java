package com.example.watchit_connect;

import parsing.Parser;
import parsing.GenericSensorData;
import com.example.watchit_connect.MainFragment.MainFragmentListener;
import com.example.watchit_connect.Spaces.SpacesFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.SpaceHandler;
import Utilities.UtilityClass1;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends BaseActivity implements OnSpaceItemSelectedListener, MainFragmentListener  {
	
	TextView textViewUserName, textViewData;
	View myTestView;
	MainFragment fragmentMain;

	DataObjectListener myListener;
	SpaceHandler spaceHandler;
	 DataHandler dataHandler;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

   	    myListener = new DataObjectListener() {
    	 // implement this interface in a controller class of your application
   
		@Override
		public void handleDataObject(
				de.imc.mirror.sdk.DataObject dataObject, String spaceId) {
				showToast("New object receieved in space");
			 String objectId = dataObject.getId();
			 //Log.d("HandleDataObjet", dataObject.getCDMData().)
			 
			 showAlert("new data published");
			 Log.d("HandleDataObject", dataObject.toString());
			 GenericSensorData data = Parser.buildSimpleXMLObject((DataObject) dataObject);
			 Toast.makeText(getBaseContext(), "Receieved dataobject from space: " + objectId, Toast.LENGTH_SHORT).show();
	    	 System.out.println("Received object " + objectId + " from space " + spaceId);
	    	 Toast.makeText(getBaseContext(), "Data: Location:  + longitude" + data.getLocation().getLongitude() + " latitude: " + data.getLocation().getLatitude()+ 
	    			 "Type: " + data.getValue().getType() + " unit: " + data.getValue().getUnit() + " value: " + data.getValue(), Toast.LENGTH_LONG).show();
		}
    	 };

        dataHandler = new DataHandler(MainApplication.connectionHandler, MainApplication.spaceHandler);
        dataHandler.setMode(Mode.ONLINE);
   	 	dataHandler.addDataObjectListener(myListener);

    	fragmentMain = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentMain,"main").commit(); 

    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	MainFragment mainfragment = (MainFragment)  getSupportFragmentManager().findFragmentByTag("main");
    	textViewUserName = (TextView) mainfragment.getView().findViewById(R.id.textViewUserName);
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        System.out.println("uname " +  settings.getString("username", ""));
        textViewUserName.setText(settings.getString("username", ""));
        
        mainfragment.update(settings.getString("username", ""));   
    }
       

    

	@Override
	public void onSpaceItemSelected(int position) {
		
		
		Space space = app.spaces.get(position);
		new GetDataFromSpaceTask().execute(space.getId());
        DataObjectsFragment dataObjectsFragment = new DataObjectsFragment();
        //int members = space.getMembers().size();
        Bundle b = new Bundle();
        b.putInt("pos", position);
        dataObjectsFragment.setArguments(b);  
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, dataObjectsFragment, "dataobjects");
        transaction.addToBackStack("main");
        transaction.commit();
	}
	

	
	public void dataToWATCHiTOnClick(View view) {
		//Send data as a String
	    Bundle b = new Bundle();
	    GenericSensorData data = Parser.buildSimpleXMLObject((DataObject) MainApplication.dataObjects.get(MainApplication.dataObjects.size()-1));
	    b.putString("watchitdata", data.getValue().getText());
	    Message message = Message.obtain(null, LocalService.MSG_SET_STRING_VALUE);
	    message.setData(b);
	    sendMessageToService(message);
	}

	@Override
	public void onWATCHiTButtonClicked(boolean on) {
		//ToggleButton button;
        if (on) {
        	if (UtilityClass1.isBluetoothConnected()) service.start();
        	else 
        		showToast("Please turn on bluetooth and pair it with WATCHiT");
        	
            //showProgress("Waiting for ack from WATCHiT");
        } else {
            showToast("Stopped sync with WATCHiT");
            service.stop();
        }	
	}    
}
