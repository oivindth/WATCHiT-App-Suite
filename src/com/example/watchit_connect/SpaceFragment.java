package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SpaceFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.space_fragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		Bundle b = this.getArguments();

	       List<String> spaceObject = new ArrayList<String>();
	         spaceObject.add("Id: " +b.getString("id"));
	         spaceObject.add("Name: " +b.getString("name"));
	         spaceObject.add("Members: " + b.getInt("memberCount"));

	        		   ArrayAdapter<String> arrayAdapter2 =      
		    			         new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, spaceObject);

	        		   ListView lw = (ListView)     this.getActivity().findViewById(R.id.listViewSpaceFragment);
		    	        lw.setAdapter(arrayAdapter2);
	}
	
}
