package com.example.watchit_connect;

import com.example.watchit_connect.Applications.ApplicationsSettingsFragment.OnApplicationChosenListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainFragment extends Fragment {
	
OnApplicationChosenListener mListener;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main , container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
	}
	

	
}
