package fragments;

import com.actionbarsherlock.app.SherlockFragment;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TPConfigFragment extends SherlockFragment {
	
	private View myFragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		myFragmentView = inflater.inflate(R.layout.tp_config_fragment , container, false);
		
		return myFragmentView;
	}
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
}
