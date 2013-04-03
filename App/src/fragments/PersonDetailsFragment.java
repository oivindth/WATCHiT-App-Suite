package fragments;

import no.ntnu.emergencyreflect.R;
import activities.QuizActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.model.LatLng;

public class PersonDetailsFragment extends SherlockFragment {
	
	
	private String user;
	private LatLng latlng;
	private String time;
	private String value;

	private TextView userNameTextView, timeTextView, textViewHeader;
	
	private Button checkListButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = this.getArguments();
		
		user = b.getString("user");
		time = b.getString("time");
		value = b.getString("value");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.person_details_fragment, container, false);
		
		userNameTextView = (TextView) myFragmentView.findViewById(R.id.textViewUserName);
		userNameTextView.setText(" : " +user);
		timeTextView= (TextView) myFragmentView.findViewById(R.id.textViewTime);
		timeTextView.setText(" : " +time);
		textViewHeader = (TextView) myFragmentView.findViewById(R.id.textViewHeader);
		//textViewHeader.setText(value);
		checkListButton = (Button) myFragmentView.findViewById(R.id.buttonCheckList);
		
		checkListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent (getActivity(), QuizActivity.class);
				startActivity(intent);
				
			}
		});
		
		return myFragmentView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
}
