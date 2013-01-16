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

public class SpaceFragment extends ListFragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.space_fragment, container, false);
    }
	


	public void UpdateSpaceInfo(String name, String id, String members) {
		List
		
		ArrayAdapter<String> arrayAdapter2 =      
				   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, space);
			       setListAdapter(arrayAdapter2);
		
	}
	
}
