package fragments;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.model.LatLng;

public class PersonDetailsFragment extends SherlockFragment {
	
	
	private String user;
	private LatLng latlng;
	private String time;
	private String value;

	private TextView userNameTextView, timeTextView, textViewHeader;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = this.getArguments();
		
		user = b.getString("user");
		time = b.getString("time");
		value = b.getString("value");
		Log.d("PersonDetailsFragment", "user: " + user);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.person_details_fragment, container, false);
		
		userNameTextView = (TextView) myFragmentView.findViewById(R.id.textViewUserName);
		userNameTextView.setText(user);
		timeTextView= (TextView) myFragmentView.findViewById(R.id.textViewTime);
		timeTextView.setText(time);
		textViewHeader = (TextView) myFragmentView.findViewById(R.id.textViewHeader);
		//textViewHeader.setText(value);
		
		return myFragmentView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
}
