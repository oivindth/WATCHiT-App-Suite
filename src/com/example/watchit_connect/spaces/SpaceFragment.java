package com.example.watchit_connect.Spaces;

import java.util.ArrayList;
import java.util.List;

import com.example.watchit_connect.R;
import com.example.watchit_connect.R.layout;
import com.example.watchit_connect.Spaces.SpacesFragment.OnSpaceItemSelectedListener;

import de.imc.mirror.sdk.Space;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SpaceFragment extends ListFragment {
	
	private int pos;
	private Space space;
	
	OnSpaceInfoSelectedListener mListener;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.space_fragment, container, false);
    }
	
	
	//Container Activity must implement this interface
	public interface OnSpaceInfoSelectedListener {
		public void onSpaceInfoSelected(int position);
    	}
		
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
		Toast.makeText(getActivity().getBaseContext(), "clicked on item at position " + position, Toast.LENGTH_SHORT).show();
        mListener.onSpaceInfoSelected(position);
    }
	
	
	@Override
	public void onResume() {
		super.onResume();
		
	
		if (getArguments().size() != 0) 
			pos = getArguments().getInt("pos");
			
		// correct way to get data from activity?
		 space = ((SpacesActivity) getActivity()).getSpace(pos);
		
			//mySpace.
		updateSpaceInfo(space);
			//UpdateSpaceInfo(getArguments().getString("name"), getArguments().getString("id"), getArguments().getInt("memberCount"));		
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSpaceInfoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSpaceItemSelectedListener");
        }
    }

	
	public void updateSpaceInfo(Space space) {
		List<String> spaceInfo = new ArrayList<String>();
		spaceInfo.add(space.getName());
		spaceInfo.add(space.getId());
		spaceInfo.add(Integer.toString(space.getMembers().size()));
		
		ArrayAdapter<String> arrayAdapter2 =      
				   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, spaceInfo);
			       setListAdapter(arrayAdapter2);
	}
	
	public void UpdateSpaceInfo(String name, String id, int members) {
		List<String> space = new ArrayList<String>();
		space.add(name);
		space.add(id);
		space.add(Integer.toString(members));
		
		ArrayAdapter<String> arrayAdapter2 =      
				   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, space);
			       setListAdapter(arrayAdapter2);
		
	}
}
