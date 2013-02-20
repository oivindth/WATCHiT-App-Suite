package com.example.watchit_connect.Applications.trainingprocedure;

import com.example.watchit_connect.R;
import com.example.watchit_connect.Applications.ApplicationsSettingsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class TrainingProcedureActivity extends FragmentActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_procedure);
      
        TrainingprocedureMainFragment trainingproceduremainFragment = new TrainingprocedureMainFragment();
 
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, trainingproceduremainFragment,"trainingprocedure").commit();
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
}
