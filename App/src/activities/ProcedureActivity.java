package activities;

import java.util.ArrayList;
import java.util.List;

import parsing.Procedure;
import parsing.Step;
import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;

public class ProcedureActivity extends BaseActivity {
	
	private TextView procedureNameTextView;
	
	private Button buttonSelect;
	private MainApplication mApp;
	private int pos;
	private ArrayAdapter<String> adapter;
	private ListView listOfSteps;
	List<Step> steps;
	private Procedure p;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_procedure);
		//listOfProecdures = (ListView) findViewById(R.id.listViewProcedures);	
		
		mApp = MainApplication.getInstance();
		
		listOfSteps = (ListView) findViewById(R.id.listViewSteps);
		
		procedureNameTextView = (TextView) findViewById(R.id.textViewProcedureName);
		Bundle b = getIntent().getExtras();
		pos = b.getInt("procedure_pos");

		p = mApp.procedures.get(pos);
		String name = p.getName();
		
		steps = p.getSteps();
		List<String> stepNames = new ArrayList<String> ();
		for (Step st : steps) {
			stepNames.add(st.getName());
		}
		
		
		buttonSelect = (Button) findViewById(R.id.button_select);
		buttonSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mApp.numberOfSteps = steps.size();
				mApp.currentProcedure = p;
				Log.d("CHOSEN", "mapp: " + mApp.currentProcedure + " p : " + p);
				mApp.TPObjects.clear();
				Intent intent = new Intent(getBaseContext(), TrainingMainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		
		procedureNameTextView.setText(name);
		
		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, stepNames);
		listOfSteps.setAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_training_procedure, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
