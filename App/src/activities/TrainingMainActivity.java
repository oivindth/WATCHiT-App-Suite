package activities;

import listeners.SpaceChangeListener;

import com.example.watchit_connect.MainApplication;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import asynctasks.GetDataFromSpaceTask;

public class TrainingMainActivity extends BaseActivity implements SpaceChangeListener {
	
	private Button buttonTrain;
	private Button buttonResults;
	private MainApplication mainAPp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_main);
		mainAPp = MainApplication.getInstance();
		
		buttonTrain = (Button) findViewById(R.id.buttonTrain);
		buttonTrain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
				startActivity(intent);
			}
		});
		buttonResults = (Button) findViewById(R.id.buttonResults);
		buttonResults.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ResultActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		
	}

	@Override
	public void onSpaceChanged(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataFetchedFromSpace() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume () {
		super.onResume();
		new GetDataFromSpaceTask(this, mainAPp.currentActiveSpace.getId()).execute();
	}
	
}
