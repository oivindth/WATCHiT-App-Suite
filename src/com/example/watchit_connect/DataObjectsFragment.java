package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import parsing.GenericSensorData;
import parsing.Parser;

import com.example.watchit_connect.Spaces.SpacesActivity;
import com.example.watchit_connect.Spaces.SpacesFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DataObjectsFragment extends ListFragment {
	
OnSpaceItemSelectedListener mListener;
	
	MainApplication app;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		app =  (MainApplication) getActivity().getApplication();
        return inflater.inflate(R.layout.data_objects_fragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();

		Log.d("Fragment dataobjects", "sice of data: " + MainApplication.dataObjects.size());
	    List<String> dataAdapter = new ArrayList<String> ();
		
	    List<GenericSensorData> simplexmlobjects = new ArrayList<GenericSensorData>();
	    
	    for (de.imc.mirror.sdk.DataObject data : MainApplication.dataObjects) { //TODO: do in activity and add pbjects to app globals.
			simplexmlobjects.add( Parser.buildSimpleXMLObject((DataObject) data));
		}
	    for (GenericSensorData genericSensorData : simplexmlobjects) {
			dataAdapter.add(genericSensorData.getValue().getText());
		}
	    
	    
		UpdateList(dataAdapter); 
		

	}
	
	//Update ze fragment
	public void UpdateList (List<String> data) {
		   ArrayAdapter<String> arrayAdapter2 =      
		   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, data);
	       setListAdapter(arrayAdapter2);
	}
	

		
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
		Toast.makeText(getActivity().getBaseContext(), "clicked on item at position " + position, Toast.LENGTH_SHORT).show();
    }
	

	
}
