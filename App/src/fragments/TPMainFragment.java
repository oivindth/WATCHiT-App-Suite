package fragments;

import no.ntnu.emergencyreflect.R;

import org.jraf.android.backport.switchwidget.Switch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;

public class TPMainFragment extends SherlockFragment {
	
	public View myFragmentView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		
		 myFragmentView = inflater.inflate(R.layout.fragment_tpmain , container, false);
		
        return myFragmentView;
    }
	
	
}
