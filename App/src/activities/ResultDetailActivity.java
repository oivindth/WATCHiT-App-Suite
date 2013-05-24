package activities;

import java.util.ArrayList;
import java.util.List;

import parsing.GenericSensorData;
import parsing.Parser;
import parsing.Step;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultDetailActivity extends BaseActivity {
	
	private MainApplication mainApp;
	private ArrayList<String> toDisplay;
	private ListView listView;
	private ArrayAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_result_details);
		mainApp = MainApplication.getInstance();
		toDisplay = new ArrayList<String>();
		
		Bundle b = getIntent().getExtras();
		int pos = b.getInt("pos");
		
		GenericSensorData data = mainApp.TPObjects.get(pos);
		listView = (ListView) findViewById(R.id.listViewResultDetails);
		
		List<Step> steps = Parser.buildStepList(data.getValue().getText());
		int stepCount =1;
		for (Step	step : steps) {
			toDisplay.add("Step: " + stepCount + "  Time:  " + step.getTime());
			stepCount++;
		}
		
		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, toDisplay);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View viw, int pos,
					long id) {
				
			}
		});
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_training_procedure, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	
	@Override
	public void onResume() {
		super.onResume();
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
