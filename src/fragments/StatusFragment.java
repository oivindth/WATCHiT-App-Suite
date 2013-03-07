package fragments;

import com.example.watchit_connect.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatusFragment extends Fragment {
	
	@Override
	/**
	 * The system calls this when creating the fragment. Within your implementation, 
	 * you should initialize essential components of the fragment that you want to retain when the fragment is paused or stopped, then resumed.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	/**
	 * The system calls this when it's time for the fragment to draw its user interface for the first time.
	 *  To draw a UI for your fragment, you must return a View from this method that is the root of your fragment's layout. 
	 *  You can return null if the fragment does not provide a UI.
	 */
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_status, container, false);
		
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
	
}
