package com.example.watchit_connect;


import de.imc.mirror.sdk.Space;

import Utilities.UtilityClass;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import asynctasks.CreateSpaceTask;

public class MainFragment extends Fragment {
	
	
	
	private MainFragmentListener mMainFragmentListener;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
		
		
        return inflater.inflate(R.layout.fragment_main , container, false);
    }
	
	//Container Activity must implement this interface
		public interface MainFragmentListener {
			
	    	}
	
	@Override
	public void onResume() {
	 //TODO: Bruk en Listener og send til aktiviteten?
		//toggleButton.setChecked(on);

		
		
	
		super.onResume();
	}
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mMainFragmentListener = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSpaceItemSelectedListener");
        }
    }


	
	
	public void update(String msg) {
		//TextView view = (TextView) this.getView().findViewById(R.id.textViewUserName);
		//view.setText(msg);
	}
	

	
}
