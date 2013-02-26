package com.example.watchit_connect.Applications.trainingprocedure;

import java.util.ArrayList;
import java.util.List;

import com.example.watchit_connect.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TrainingProcedureResultListFragment extends ListFragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.training_procedure_resultlistfragment, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		List<String> dummyData = new ArrayList<String>();
		dummyData.add("User: Admin  " + "Time:  15 min ");
		dummyData.add("User: peter  " + "Time:  12 min ");
		dummyData.add("User: noob  " +  "Time:  23 min ");
		dummyData.add("User: Nora  " +  "Time:  12 min ");
		
		ArrayAdapter<String> arrayAdapter2 =      
				   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, dummyData);
			       setListAdapter(arrayAdapter2);
	}
	
	
}
