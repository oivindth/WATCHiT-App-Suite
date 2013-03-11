package fragments;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;
import com.example.watchit_connect.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

public class ApplicationsSettingsFragment extends SherlockFragment {

	ApplicationsSettingsFfragmentListener mApplicationsSettingsFragmentListener;

	ToggleButton toggleButtonOnlineMode, toggleButtonLocation, toggleButtonWATCHiT;
	Button buttonOK;
	MainApplication sApp;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sApp = MainApplication.getInstance();
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.applications_settings_fragment, container, false);

		toggleButtonOnlineMode = (ToggleButton) myFragmentView.findViewById(R.id.toggleButtonOnline);
		toggleButtonLocation = (ToggleButton) myFragmentView.findViewById(R.id.toggleButtonLocation);
		toggleButtonWATCHiT = (ToggleButton) myFragmentView.findViewById(R.id.toggleButtonWATCHiT);

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

		toggleButtonOnlineMode.setChecked(sApp.OnlineMode); 
		toggleButtonLocation.setChecked(sApp.isLocationOn);
		toggleButtonWATCHiT.setChecked(sApp.isWATChiTOn);
		
		

		

		//buttonOK =  (Button) getView().findViewById(R.id.buttonSettingsOK);

		toggleButtonOnlineMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplicationsSettingsFragmentListener.onlineModeClicked(toggleButtonOnlineMode.isChecked());
				onlineModeClicked(toggleButtonOnlineMode.isChecked());
			}
		});

		toggleButtonLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplicationsSettingsFragmentListener.locationMode(toggleButtonLocation.isChecked());
				locationModeClicked(toggleButtonLocation.isChecked());
			}
		});
		toggleButtonWATCHiT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplicationsSettingsFragmentListener.WATCHiTSwitchClicked(toggleButtonWATCHiT.isChecked());
			    wathcitSwitchClicked(toggleButtonWATCHiT.isChecked());
			}
		});
	}

	protected void wathcitSwitchClicked(boolean checked) {
		// TODO Auto-generated method stub
	}
	protected void locationModeClicked(boolean checked) {
		// TODO Auto-generated method stub
		
	}
	private void onlineModeClicked(boolean checked) {
		// TODO Auto-generated method stub
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

	public void updateView(boolean online, boolean location, boolean watchit) {
		toggleButtonOnlineMode.setChecked(online);
		toggleButtonLocation.setChecked(location);
		toggleButtonWATCHiT.setChecked(watchit);
	}
	public void updateView(boolean watchitSwitch) {
		toggleButtonWATCHiT.setChecked(watchitSwitch);
	}

}
