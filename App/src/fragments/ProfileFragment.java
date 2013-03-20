package fragments;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;
import activities.GatewayActivity;
import activities.MapActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ProfileFragment extends SherlockFragment {




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

		return myFragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
