package activities;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TrainingMainActivity extends BaseActivity {
	
	private Button buttonTrain;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_main);
		
		buttonTrain = (Button) findViewById(R.id.buttonTrain);
		buttonTrain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
				startActivity(intent);
				
				
			}
		});
	}
}
