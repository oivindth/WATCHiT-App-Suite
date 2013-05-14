package activities;

import java.util.ArrayList;

import parsing.GenericSensorData;
import parsing.Parser;
import parsing.Step;
import listeners.StepListener;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
	private MainApplication mApp;
	private TextView currentStep, maxSteps;
	
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
				String dataToServer = Parser.buildCustomStringXmlFromSteps(sApp.steps);
				GenericSensorData gsdtp = Parser.buildSimpleXMLObject(dataToServer, sApp.currentProcedure.getName());
				String jid = sApp.getUserName() + "@" + sApp.connectionHandler.getConfiguration().getDomain();
				DataObject dataObject = Parser.buildDataObjectFromSimpleXMl(gsdtp, jid, sApp.connectionHandler.getCurrentUser().getUsername());
				new PublishDataTask(dataObject, sApp.currentActiveSpace.getId()).execute();
				
				//reset.
				sApp.steps = new ArrayList<Step>();
				chronometer.setText("00");
				shareButton.setEnabled(false);
				startButton.setVisibility(View.VISIBLE);
				
			}
		});
	}
	
	@Override
	public void stepAdded(int pos) {
		//textview currentstep increment
		// hvis jeg skal lagre tiden så må jeg hente ut tiden.........
		//sApp.steps.get(pos).setTime((String) chronometer.getText());
		currentStep.setText(Integer.toString(pos));
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
