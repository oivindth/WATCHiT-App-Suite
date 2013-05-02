package fragments;

import no.ntnu.emergencyreflect.R;
import activities.GatewayActivity;
import activities.MapActivity;
import activities.TrainingProcedureActivity;
import activities.VirtualWATCHiTActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DashboardFragment extends SherlockFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.dashboardlayout, container, false);

			// new GetSpacesTask().execute();
			Button buttonMap = (Button) myFragmentView.findViewById(R.id.btn_map);
			Button buttonTP = (Button) myFragmentView.findViewById(R.id.btn_TP);
			Button buttonGateway = (Button) myFragmentView.findViewById(R.id.btn_gateway);
			Button buttonvwatchit = (Button) myFragmentView.findViewById(R.id.btn_vwatchit);
			TextView textview = (TextView) myFragmentView.findViewById(R.id.footerTextView);

			// open webpage to mirror?
			textview.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			buttonMap.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(getActivity().getApplicationContext(), MapActivity.class);
					startActivity(i);
				}
			});
			buttonTP.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(getActivity().getApplicationContext(), TrainingProcedureActivity.class);
					startActivity(i);
				}
			});
			// Listening to Events button click
			buttonGateway.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(getActivity().getApplicationContext(), GatewayActivity.class);
					startActivity(i);
				}
			});
			
			buttonvwatchit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(getActivity().getApplicationContext(), VirtualWATCHiTActivity.class);
					startActivity(i);
				}
			});
		

		return myFragmentView;
	}



	@Override
	public void onResume() {
		super.onResume();
	}

	


	
}
