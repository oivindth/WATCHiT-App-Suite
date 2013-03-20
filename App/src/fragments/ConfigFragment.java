package fragments;

import org.jraf.android.backport.switchwidget.Switch;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class ConfigFragment extends SherlockFragment {
	
	private MainApplication mApp;
	private StatusChangeListener mListener;
	private Switch switchOnline, switchLocation, switchWATCHiT;
	
	//Container Activity must implement this interface
	public interface StatusChangeListener {
		public void onlineModeClicked(boolean on);
		public void WATCHiTSwitchClicked(boolean on);
		public void locationMode(boolean on);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		mApp =  MainApplication.getInstance();
		
		View myFragmentView = inflater.inflate(R.layout.fragment_config , container, false);
		
		switchOnline = (Switch) myFragmentView.findViewById(R.id.switchOnline);
		switchLocation = (Switch) myFragmentView.findViewById(R.id.switchLocation);
		switchWATCHiT = (Switch) myFragmentView.findViewById(R.id.switchWATCHiT);
		
        return myFragmentView;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		
		switchOnline.setChecked(mApp.OnlineMode);
		switchLocation.setChecked(mApp.isLocationOn);
		switchWATCHiT.setChecked(mApp.isWATChiTOn);
		
		switchOnline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onlineModeClicked(switchOnline.isChecked());
				
			}
		});

		switchLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.locationMode(switchLocation.isChecked());	
			}
		});
		
		switchWATCHiT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.WATCHiTSwitchClicked(switchWATCHiT.isChecked());
				
			}
		});
		
	}
	

	public void updateWATCHiTView(boolean on) {
		switchWATCHiT.setChecked(on);
	}
	
	public void updateLocationView(boolean on) {
		switchLocation.setChecked(on);
	}
	public void updateOnlineView(boolean on) {
		switchOnline.setChecked(on);
	}
	

	
	public void updateView(boolean online, boolean watchit, boolean location) {
		try {
			switchLocation.setChecked(location);
			switchOnline.setChecked(online);
			switchWATCHiT.setChecked(watchit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (StatusChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement Listener");
		}
	}


}