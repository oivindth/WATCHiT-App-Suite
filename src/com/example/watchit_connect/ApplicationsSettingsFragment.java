package com.example.watchit_connect;

import com.example.watchit_connect.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class ApplicationsSettingsFragment extends Fragment {

	ApplicationsSettingsFfragmentListener mApplicationsSettingsFragmentListener;
	
	Switch switch1, switch2, switch3;
	Button buttonOK;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		

		View myFragmentView = inflater.inflate(R.layout.applications_settings_fragment, container, false);
		
		switch1 = (Switch) myFragmentView.findViewById(R.id.switchOnline);
		switch2 = (Switch) myFragmentView.findViewById(R.id.switchLocation);
		switch3 = (Switch) myFragmentView.findViewById(R.id.switchWATCHiT);
	
		
		
        return myFragmentView;
    }
	

	
	//Container Activity must implement this interface
		public interface ApplicationsSettingsFfragmentListener {
			public void onlineModeClicked(boolean on);
			public void WATCHiTSwitchClicked(boolean on);
			public void locationMode(boolean on);
			
	    	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	
		//buttonOK =  (Button) getView().findViewById(R.id.buttonSettingsOK);
		
		
		switch1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplicationsSettingsFragmentListener.onlineModeClicked(switch1.isChecked());
			}
		});
		
		switch2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplicationsSettingsFragmentListener.locationMode(switch2.isChecked());
			}
		});
		
		
		
		switch3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplicationsSettingsFragmentListener.WATCHiTSwitchClicked(switch3.isChecked());
				
			}
		});
		

	}
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mApplicationsSettingsFragmentListener = (ApplicationsSettingsFfragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Listener");
        }
    }

	public void updateView(boolean s1, boolean s2, boolean s3) {
		switch1.setChecked(s1);
		switch2.setChecked(s2);
		switch3.setChecked(s3);
	}
	
}
