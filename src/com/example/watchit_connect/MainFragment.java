package com.example.watchit_connect;

import com.example.watchit_connect.Applications.ApplicationsSettingsFragment.OnApplicationChosenListener;
import com.example.watchit_connect.Spaces.SpaceFragment.OnSpaceInfoSelectedListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainFragment extends Fragment {
	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
  
	    
        return inflater.inflate(R.layout.fragment_main , container, false);
    }
	
	@Override
	public void onResume() {
	 
		super.onResume();
	}
	

	
}
