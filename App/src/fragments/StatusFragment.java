package fragments;

import org.jraf.android.backport.switchwidget.Switch;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;

import no.ntnu.emergencyreflect.R;
import no.ntnu.emergencyreflect.R.drawable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class StatusFragment extends SherlockFragment {
	
	private TextView textViewEvent, textViewEventName, textViewLatest, textViewLEDEvent;
	private MainApplication sApp;
	

	private ImageView onlineLED, watchitLED, locationLED, eventLED;
	

	
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
		textViewEventName = (TextView) myFragmentView.findViewById(R.id.textViewEvent);
		textViewLatest = (TextView) myFragmentView.findViewById(R.id.textViewLatestInfo);
		textViewLEDEvent = (TextView) myFragmentView.findViewById(R.id.textViewEventLedEvent);
		
		onlineLED = (ImageView) myFragmentView.findViewById(R.id.imageViewOnlineLED);
		watchitLED = (ImageView) myFragmentView.findViewById(R.id.imageViewWatchitLED);
		locationLED = (ImageView) myFragmentView.findViewById(R.id.imageViewLocatiohLED);
		eventLED = (ImageView) myFragmentView.findViewById(R.id.imageViewEventLED);
		
		return  myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (sApp.currentActiveSpace == null) {
			textViewEvent.setText(getString(R.string.event_not_registered));
		} else {
			Log.d("currentspace", "currentspace: " + sApp.currentActiveSpace);
			textViewEvent.setText(sApp.currentActiveSpace.getName());
			textViewLEDEvent.setText(sApp.currentActiveSpace.getName());
			textViewEventName.setText(sApp.currentActiveSpace.getName());
			textViewEvent.setText("Members:  " + sApp.currentActiveSpace.getMembers().size() + "\n" + "Data:  " + sApp.genericSensorDataObjects.size());
			
		}
		
		
		onlineLED.setImageResource(sApp.OnlineMode ? drawable.circle_green_light : drawable.circle_red_light);
		locationLED.setImageResource(sApp.isLocationOn ? drawable.circle_green_light : drawable.circle_red_light);
		watchitLED.setImageResource(sApp.isWATChiTOn ? drawable.circle_green_light : drawable.circle_red_light);
		eventLED.setImageResource(sApp.currentActiveSpace != null ? drawable.circle_green_light : drawable.circle_red_light);
		
		if (sApp.latest != null)
		updateTextViewLatesInfo(sApp.latest);



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
	
	


	
	public void updateEventView(boolean on) {
		if (sApp.currentActiveSpace == null) {
			textViewEvent.setText(getString(R.string.event_not_registered));
		} else {
			textViewEvent.setText(sApp.currentActiveSpace.getName());
			textViewEventName.setText(sApp.currentActiveSpace.getName());
		}
	}
	
	public void updateTextViewLatesInfo (String text) {
		textViewLatest.setText(text);
	}

	public void  updateTextViewEvent(String name) {
		textViewEvent.setText(name);		
	}
	
	public void updateEventLED(boolean on) {
		eventLED.setImageResource(R.drawable.circle_green_light);
	}

	public void updateTextViewEventLed(String name) {
		textViewLEDEvent.setText(name);
		
	}

	
	
	
	
	
}
