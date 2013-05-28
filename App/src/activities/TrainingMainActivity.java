package activities;
/**
 * Copyrigth 2013 ¯ivind Thorvaldsen
 * This file is part of Reflection App

    Reflection App is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
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
