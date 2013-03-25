package fragments;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class NoteDetailsFragment extends SherlockFragment {
	
	private String user, time, value;
	private EditText editTextNotesView;
	private TextView userNameView, timeView;
	
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

		View myFragmentView = inflater.inflate(R.layout.note_details_fragment, container, false);
		
		userNameView = (TextView) myFragmentView.findViewById(R.id.textViewUserName);
		userNameView.setText(user);
		timeView= (TextView) myFragmentView.findViewById(R.id.textViewTime);
		timeView.setText(time);
		editTextNotesView = (EditText) myFragmentView.findViewById(R.id.editTextNotesdetailFragment);
		editTextNotesView.setText(value);
		
		return myFragmentView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
	}
	
}
