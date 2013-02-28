package com.example.watchit_connect;

import java.util.Set;

import com.example.watchit_connect.R;
import com.example.watchit_connect.Spaces.SpacesActivity;
import com.example.watchit_connect.Spaces.SpaceFragment.OnSpaceInfoSelectedListener;

import de.imc.mirror.sdk.SpaceMember;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

public class ApplicationsSettingsFragment extends Fragment {

	OnApplicationChosenListener mListener;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.applications_settings_fragment, container, false);
    }
	
	//Container Activity must implement this interface
		public interface OnApplicationChosenListener {
			public void onApplicationChosen(int applicationID);
	    	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/*
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnApplicationChosenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnApplicationChosenListener");
        }
    }
	*/
	
}
