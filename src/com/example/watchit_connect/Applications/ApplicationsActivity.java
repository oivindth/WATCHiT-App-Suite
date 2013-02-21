package com.example.watchit_connect.Applications;

import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.LoginActivity;
import com.example.watchit_connect.MainApplication;
import com.example.watchit_connect.R;
import com.example.watchit_connect.Applications.ApplicationsSettingsFragment.OnApplicationChosenListener;
import com.example.watchit_connect.Applications.mood.MoodActivity;
import com.example.watchit_connect.Applications.trainingprocedure.TrainingProcedureActivity;
import com.example.watchit_connect.Spaces.SpacesActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class ApplicationsActivity extends BaseActivity implements OnApplicationChosenListener {

	Intent intent;
	ApplicationsSettingsFragment appSettingsFragment;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);
      
        
        MainApplication app =  (MainApplication) getApplication();
        
        
        appSettingsFragment = new ApplicationsSettingsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, appSettingsFragment,"appSettings").commit();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    


    
    public void onRadioButtonClicked(View v){
        appSettingsFragment.onRadioButtonClicked(v);
    }
    
	@Override
	public void onApplicationChosen(int applicationID) {
		switch(applicationID) {
        case R.id.radio_app1:
        	intent = new Intent(this, TrainingProcedureActivity.class);
            //intent.setClass(ApplicationsActivity.this, TrainingProcedureActivity.class);
            startActivity(intent);
            finish();
            break;
        case R.id.radio_app2:
            // App 2.
        	intent = new Intent();
            intent.setClass(ApplicationsActivity.this, MoodActivity.class);
            startActivity(intent);
            finish();
            break;
            
    }	
	}
    
    
//    private void startApplication (Activity c) {
//    	Intent intent = new Intent();
//        intent.setClass(ApplicationsActivity.this, c.getClass());
//        startActivity(intent);
//        finish();
//    }
    
    
	
}
