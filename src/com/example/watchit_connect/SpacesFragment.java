package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import com.example.watchit_connect.R;

import de.imc.mirror.sdk.Space;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SpacesFragment extends ListFragment {
	
	OnSpaceItemSelectedListener mListener;
	
	MainApplication app;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		app =  (MainApplication) getActivity().getApplication();
        return inflater.inflate(R.layout.spaces_fragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
	    List<String> spacesNames = new ArrayList<String> ();
		for (de.imc.mirror.sdk.Space space : app.spaces) {
			spacesNames.add(space.getName());
		}
		UpdateSpaces(spacesNames); 
	}
	
	//Update ze fragment
	public void UpdateSpaces (List<String> names) {
		   ArrayAdapter<String> arrayAdapter2 =      
		   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, names);
	       setListAdapter(arrayAdapter2);
	}
	
	//Container Activity must implement this interface
	public interface OnSpaceItemSelectedListener {
		public void onSpaceItemSelected(int position);
    	}
		
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
		Toast.makeText(getActivity().getBaseContext(), "clicked on item at position " + position, Toast.LENGTH_SHORT).show();
        mListener.onSpaceItemSelected(position);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSpaceItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSpaceItemSelectedListener");
        }
    }
}