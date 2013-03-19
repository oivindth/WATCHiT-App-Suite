package fragments;

import java.util.ArrayList;
import java.util.List;

import listeners.LayersChangeListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AppsFragment extends SherlockListFragment {
	
	OnAppItemSelectedListener mListener;
	
	MainApplication app;
	int selected;
	
	
	//Container Activity must implement this interface
	public interface OnAppItemSelectedListener {
		public void onAppItemSelected(int position);
    	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		app =  MainApplication.getInstance();
        return inflater.inflate(R.layout.apps_fragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
	    String[] appNames = 
	    getResources().getStringArray(R.array.apps_list);
		
		UpdateFragment(appNames); 
	}
	
	//Update ze fragment
	public void UpdateFragment (List<String> names) {
		   ArrayAdapter<String> arrayAdapter2 =      
		   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, names);
	       setListAdapter(arrayAdapter2);
	}
	//Update ze fragment
	public void UpdateFragment (String [] names) {
		   ArrayAdapter<String> arrayAdapter2 =      
		   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, names);
	       setListAdapter(arrayAdapter2);
	}

		
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
		selected = position;
		this.setSelection(selected);
        mListener.onAppItemSelected(position);
    }
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAppItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnAPpItemSelectedListener interface");
		}
	}

}