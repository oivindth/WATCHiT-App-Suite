package activities;

import java.util.ArrayList;
import java.util.List;

import parsing.GenericSensorDataTP;
import parsing.Step;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import asynctasks.GetDataFromSpaceTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;

public class ResultActivity extends BaseActivity {
	
	private ListView listViewResults;
	
	private ArrayAdapter<String> adapter;
	
	private MainApplication mainApp;
	
	private List<String> toDisplay = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_results);
		mainApp = MainApplication.getInstance();
		
		new GetDataFromSpaceTask(this, mainApp.currentActiveSpace.getId());
		
		//calculating total time............
		String user;
		String totalTime;

		for ( GenericSensorDataTP  tp : mainApp.TPObjects) {
			int tempTotal = 0;
			user = tp.getCreationInfo().getPerson();
			for (Step step : tp.getValue().getSteps()) {
				String time =  step.getTime();
				int integer = Integer.getInteger(time);
				tempTotal += integer;
			}
			totalTime = Integer.toString(tempTotal);
			tempTotal = 0;
			toDisplay.add("User: " + user + "  Total time: " + totalTime);
		}
		
		listViewResults = (ListView) findViewById(R.id.listViewResults);
		
		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, toDisplay);
		listViewResults.setAdapter(adapter);
		
		listViewResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View viw, int pos,
					long id) {
				Intent intent = new Intent(getBaseContext(), ResultDetailsActivity.class);
				Bundle b = new Bundle();
				b.putInt("pos", pos);
				intent.putExtras(b);
				startActivity(intent);			
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
