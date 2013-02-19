package com.example.watchit_connect.Spaces;

import java.util.List;

import com.example.watchit_connect.R;

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
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.spaces_fragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
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