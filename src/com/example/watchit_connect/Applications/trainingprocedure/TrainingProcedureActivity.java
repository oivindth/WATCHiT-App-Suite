package com.example.watchit_connect.Applications.trainingprocedure;

import com.example.watchit_connect.ApplicationsSettingsFragment;
import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.MainFragment;
import com.example.watchit_connect.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrainingProcedureActivity extends BaseActivity {
	
	private ActionBar actionBar;
	private SpinnerAdapter mSpinnerAdapter;
	private OnNavigationListener mOnNavigationListener;
	private Fragment fragment;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_procedure);
      
        TrainingprocedureMainFragment trainingproceduremainFragment = new TrainingprocedureMainFragment();
 
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, trainingproceduremainFragment,"trainingprocedure").commit();
        
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
     
        mSpinnerAdapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.action_list, 
    			android.R.layout.simple_spinner_dropdown_item);
   
        
    
        
        
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    

	
}
