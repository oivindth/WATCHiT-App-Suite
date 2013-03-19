package fragments;

import org.jraf.android.backport.switchwidget.Switch;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;

import no.ntnu.emergencyreflect.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class StatusFragment extends SherlockFragment {
	
	private TextView textViewEvent, textViewLatest;
	private MainApplication sApp;
	private RadioButton radioButtonOnline, radioButtonLocation, radioButtonWATCHiT, radioButtonEvent;
	private Switch switchOnline, switchLocation, switchWATCHiT, switchEvent;
	private StatusChangeListener mListener;
	private ImageView onlineInfo, watchitInfo, locationInfo, eventInfo;
	
	//Container Activity must implement this interface
	public interface StatusChangeListener {
		public void onlineModeClicked(boolean on);
		public void WATCHiTSwitchClicked(boolean on);
		public void locationMode(boolean on);
		public void eventChangeClick(boolean on);
	}
	
	@Override
	/**
	 * The system calls this when creating the fragment. Within your implementation, 
	 * you should initialize essential components of the fragment that you want to retain when the fragment is paused or stopped, then resumed.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sApp = MainApplication.getInstance();
		
	}
	
	@Override
	/**
	 * The system calls this when it's time for the fragment to draw its user interface for the first time.
	 *  To draw a UI for your fragment, you must return a View from this method that is the root of your fragment's layout. 
	 *  You can return null if the fragment does not provide a UI.
	 */
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View myFragmentView = inflater.inflate(R.layout.fragment_status, container, false);
		textViewEvent = (TextView) myFragmentView.findViewById(R.id.textViewEventName);
		textViewLatest = (TextView) myFragmentView.findViewById(R.id.textViewLatestInfo);
		radioButtonOnline = (RadioButton) myFragmentView.findViewById(R.id.radioButtonOnline);
		radioButtonLocation = (RadioButton) myFragmentView.findViewById(R.id.radioButtonLocation);
		radioButtonWATCHiT = (RadioButton) myFragmentView.findViewById(R.id.radioButtonwatchit);
		radioButtonEvent = (RadioButton) myFragmentView.findViewById(R.id.radioButtonEvent);
		
		switchOnline = (Switch) myFragmentView.findViewById(R.id.switchOnline);
		switchLocation = (Switch) myFragmentView.findViewById(R.id.switchLocation);
		switchWATCHiT = (Switch) myFragmentView.findViewById(R.id.switchWATCHiT);
		switchEvent = (Switch) myFragmentView.findViewById(R.id.switchEvent);
		
		onlineInfo = (ImageView) myFragmentView.findViewById(R.id.imageViewOnlineInfo);
		watchitInfo = (ImageView) myFragmentView.findViewById(R.id.imageViewWatchitInfo);
		locationInfo = (ImageView) myFragmentView.findViewById(R.id.imageViewLocatiohInfo);
		eventInfo = (ImageView) myFragmentView.findViewById(R.id.imageViewEventInfo);
		
		return  myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (sApp.currentActiveSpace == null) {
			textViewEvent.setText("You have not registered to an event");
		} else {
			Log.d("currentspace", "currentspace: " + sApp.currentActiveSpace);
			textViewEvent.setText(sApp.currentActiveSpace.getName());
		}
		
		radioButtonLocation.setChecked(sApp.isLocationOn);
		radioButtonOnline.setChecked(sApp.OnlineMode);
		radioButtonWATCHiT.setChecked(sApp.isWATChiTOn);
		radioButtonEvent.setChecked(sApp.eventConnected);
		
		switchOnline.setChecked(sApp.OnlineMode);
		switchLocation.setChecked(sApp.isLocationOn);
		switchWATCHiT.setChecked(sApp.isWATChiTOn);
		switchEvent.setChecked(sApp.eventConnected);
		
		if (sApp.latest != null)
		updateTextViewLatesInfo(sApp.latest);

		
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
		
	
		
		switchEvent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.eventChangeClick(switchEvent.isChecked());	
				
			}
		});
		
		
		
		watchitInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getBaseContext(), "WATCHiT can be enabled here." , Toast.LENGTH_LONG).show();
				
			}
		});
		
		onlineInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getBaseContext(), "Enable online mode to sync with server and get updates from other users. " , Toast.LENGTH_LONG).show();
				
			}
		});
		
		locationInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getBaseContext(), "Enable this to use your location. need network or GPS. Use GPS for more accurate estimates." , Toast.LENGTH_LONG).show();
				
			}
		});
		
	eventInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getBaseContext(), "Connect to an event to share and compare data with other workers." , Toast.LENGTH_LONG).show();
				
			}
		});
		
		
		
		
	}
	
	/**
	 * The system calls this method as the first indication that the user is leaving the fragment 
	 * (though it does not always mean the fragment is being destroyed). 
	 * This is usually where you should commit any changes that should be persisted beyond the current user session
	 *  (because the user might not come back).
	 */
	@Override
	public void onPause () {
		super.onPause();
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

	
	public void updateWATCHiTView(boolean on) {
		radioButtonWATCHiT.setChecked(on);
		switchWATCHiT.setChecked(on);
	}
	
	public void updateLocationView(boolean on) {
		radioButtonLocation.setChecked(on);
		switchLocation.setChecked(on);
	}
	public void updateOnlineView(boolean on) {
		radioButtonOnline.setChecked(on);
		switchOnline.setChecked(on);
	}
	
	public void updateEventView(boolean on) {
		radioButtonEvent.setChecked(on);
		switchEvent.setChecked(on);
		if (sApp.currentActiveSpace == null) {
			textViewEvent.setText("You have not registered to an event");
		} else {
			Log.d("currentspace", "currentspace: " + sApp.currentActiveSpace);
			textViewEvent.setText(sApp.currentActiveSpace.getName());
		}
	}
	

	
	public void updateView(boolean online, boolean watchit, boolean location) {
		radioButtonLocation.setChecked(location);
		switchLocation.setChecked(location);
		
		radioButtonOnline.setChecked(online);
		switchOnline.setChecked(online);
		
		radioButtonWATCHiT.setChecked(watchit);
		switchWATCHiT.setChecked(watchit);
	}
	
	public void updateTextViewLatesInfo (String text) {
		textViewLatest.setText(text);
	}
	
	
}
