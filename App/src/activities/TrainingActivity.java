package activities;

import java.util.ArrayList;

import parsing.GenericSensorData;
import parsing.GenericSensorDataTP;
import parsing.Parser;
import parsing.Step;
import listeners.StepListener;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.TextView;
import asynctasks.PublishDataTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.android.DataObject;

public class TrainingActivity extends BaseActivity implements StepListener {
	
	private Button startButton, shareButton;
	CharSequence inFormat;
	private Chronometer chronometer;
	private Chronometer stepChronometer;
	private MainApplication mApp;
	private TextView currentStep, maxSteps;
	private boolean debug = true; //set to false for release
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_training);
		mApp = MainApplication.getInstance();
		
		mApp.addStepListener(this);
		
		currentStep = (TextView) findViewById(R.id.textViewCurrentStep);
		maxSteps = (TextView) findViewById(R.id.textViewTotalSteps);
		currentStep.setText("0");
		maxSteps.setText("" + sApp.numberOfSteps);
		
		stepChronometer = (Chronometer) findViewById(R.id.chronometerStep);

		chronometer = (Chronometer) findViewById(R.id.chronometer1);
		chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				
			}
		});
		startButton = (Button) findViewById(R.id.button_start);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//if (sApp.currentActiveSpace == null) {
					//showToast("You must choose an event first.");
					//return;
				//}
				stepChronometer.setBase(SystemClock.elapsedRealtime());
				stepChronometer.start();
				
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometer.start();
				startButton.setClickable(false);
				startButton.setVisibility(View.INVISIBLE);
			}
		});
		
		shareButton = (Button) findViewById(R.id.buttonShare);
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Create TP object for sharing to space!
				String dataToServer = Parser.buildCustomStringFromSteps(sApp.steps);
				Log.d("toshare", "step data: " + dataToServer);
				GenericSensorData gsdtp = Parser.buildSimpleXMLObject(dataToServer, sApp.currentProcedure.getName());
				//GenericSensorDataTP gsdtp = Parser.buildSimpleXMLTPObject(sApp.currentProcedure.getName(), sApp.steps);
				String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain();
				DataObject dataObject = Parser.buildDataObjectFromSimpleXMl(gsdtp, jid, sApp.connectionHandler.getCurrentUser().getUsername());
				Log.d("toshare", dataObject.toString());
				
				if (sApp.currentActiveSpace!= null) {
					new PublishDataTask(dataObject, sApp.currentActiveSpace.getId()).execute();
				} else {
					showToast("Choose an event first!");
					//choose event dialog......?
				}
				//Reset.
				sApp.steps = new ArrayList<Step>();
				chronometer.setText("00");
				shareButton.setEnabled(false);
				startButton.setVisibility(View.VISIBLE);
				currentStep.setText("0");
				maxSteps.setText("0");
			}
		});
		
		if (debug = true) {
			startButton.setClickable(false);
			shareButton.setClickable(true);
			shareButton.setEnabled(true);
			sApp.steps = new ArrayList<Step>();
			Step s1 = new Step();
			Step s2 = new Step();
			s1.setTime("00:20");
			s2.setTime("00:40");
			sApp.steps.add(s1);
			sApp.steps.add(s2);
			sApp.numberOfSteps = 2;
		}
	}
	
	@Override
	public void stepAdded(int pos) {
		stepChronometer.stop();
		int step = pos+1;
		// set the time of the step to the stepchronometer time.
		sApp.steps.get(pos).setTime((String) stepChronometer.getText());
		//reset the stepchronometer to make it count from zero on the next step.
		stepChronometer.setBase(SystemClock.elapsedRealtime());
		stepChronometer.start();
		
		currentStep.setText(Integer.toString(step));
		showToast("step finished");
	}
	
	@Override
	public void allStepsCompleted() {
		showToast("all steps completed! Share your time?"); //notification with vibration maybe?
		CharSequence totalTime = chronometer.getText();
		shareButton.setEnabled(true);
		chronometer.stop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_training_procedure, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;
	
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
