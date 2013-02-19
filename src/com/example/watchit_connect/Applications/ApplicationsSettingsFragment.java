package com.example.watchit_connect.Applications;

import java.util.Set;

import com.example.watchit_connect.R;
import com.example.watchit_connect.Spaces.SpacesActivity;

import de.imc.mirror.sdk.SpaceMember;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ApplicationsSettingsFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.applications_settings_fragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
}
