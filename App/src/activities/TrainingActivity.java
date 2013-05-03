package activities;

import java.util.Date;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;

import com.actionbarsherlock.app.ActionBar;

import fragments.StatusFragment;
import fragments.TPMainFragment;

public class TrainingActivity extends BaseActivity {
	
	private Button startButton, stopButton;
	private Date date;
	private CharSequence timeFormat;
	CharSequence inFormat;
	private Chronometer chronometer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_training);
		
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
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometer.start();
			}
		});
		stopButton = (Button) findViewById(R.id.button_stop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chronometer.stop();
			}
		});
		
	}
	
}
