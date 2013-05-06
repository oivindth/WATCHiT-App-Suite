package fragments;

import no.ntnu.emergencyreflect.R;

import activities.TrainingActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockFragment;

public class TPMainFragment extends SherlockFragment {

	public View myFragmentView;
	private Button buttonTrain;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		myFragmentView = inflater.inflate(R.layout.tp_main_fragment , container, false);
		
		buttonTrain = (Button) myFragmentView.findViewById(R.id.buttonTrain);
		buttonTrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),TrainingActivity.class);
				startActivity(intent);
				
			}
		});
		//timertextView = (TextView) myFragmentView.findViewById(R.id.textViewTimer);
		//timertextView.setText(timeFormat);
		return myFragmentView;
	}
	@Override
	public void onResume() {
		super.onResume();
		
		
	}
}
