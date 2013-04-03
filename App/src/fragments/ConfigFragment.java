package fragments;

import org.jraf.android.backport.switchwidget.Switch;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class ConfigFragment extends SherlockFragment implements OnCheckedChangeListener, OnClickListener  {
	
	private MainApplication mApp;
	private StatusChangeListener mListener;
	private Switch switchOnline, switchLocation, switchWATCHiT;
	private Button button;
	public View myFragmentView;
	
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
		
		 myFragmentView = inflater.inflate(R.layout.fragment_config , container, false);
		
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

		//switchOnline.setOnCheckedChangeListener(this);
		//switchLocation.setOnCheckedChangeListener(this);
		//switchWATCHiT.setOnCheckedChangeListener(this);

		switchOnline.setOnClickListener(this);
		switchLocation.setOnClickListener(this);
		switchWATCHiT.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switchOnline:
			mListener.onlineModeClicked(switchOnline.isChecked());
			break;
		case R.id.switchLocation:
			mListener.locationMode(switchLocation.isChecked());
			break;
		case R.id.switchWATCHiT:
			mListener.WATCHiTSwitchClicked(switchWATCHiT.isChecked());
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		switch (buttonView.getId()) {
		case R.id.switchOnline:
			mListener.onlineModeClicked(isChecked);
			break;
		case R.id.switchLocation:
			mListener.locationMode(isChecked);
			break;
		case R.id.switchWATCHiT:
			mListener.WATCHiTSwitchClicked(isChecked);
			break;
		default:
			break;
		}
	
	}

	
	public void updateWATCHiTView(boolean on) {
		switchWATCHiT.setChecked(on);
	}
	
	public void updateLocationView(boolean on) {
		switchLocation.setChecked(on);
		

	}
	public void updateOnlineView(boolean on) {
		try {
			switchOnline.setChecked(on);
		} catch (Exception e) {
			
		}
		
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