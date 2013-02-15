package com.example.watchit_connect.Applications;

import com.example.watchit_connect.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class ApplicationsActivity extends FragmentActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);
      
        
    	//MainFragment fragmentMain = new MainFragment();
    	//SpaceFragment spaceFragment = new SpaceFragment();
    	//getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, spaceFragment, "space").commit();
        //getSupportFragmentManager().beginTransaction()
          //      .add(R.id.fragment_container, fragmentMain,"main").commit();
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
