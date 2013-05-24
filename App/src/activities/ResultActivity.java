package activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import listeners.SpaceChangeListener;

import parsing.GenericSensorData;
import parsing.Parser;
import parsing.Result;
import parsing.Step;

import no.ntnu.emergencyreflect.R;
import Utilities.UtilityClass;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.watchit_connect.MainApplication;

public class ResultActivity extends BaseActivity implements SpaceChangeListener {
	
	private ListView listViewResults;
	private ArrayAdapter<String> adapter;
	private MainApplication mainApp;
	private List<String> toDisplay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_results);
		mainApp = MainApplication.getInstance();
		
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
		toDisplay = new ArrayList<String>();

		//calculating total time............
		String user;
		String totalTime;

		Log.d("pongo", "tps size: " + mainApp.TPObjects.size());
		int[] times = new int[mainApp.TPObjects.size()];
		mainApp.results = new ArrayList<Result>();
		for ( GenericSensorData  tp : mainApp.TPObjects) {
			int tempTotal = 0;
			user = tp.getCreationInfo().getPerson();
			List<Step> steps = Parser.buildStepList(tp.getValue().getText());
			for (Step step : steps) {
				String time =  step.getTime();
				Log.d("timetag", "timein resuklt:" + step.getTime());
				//int integer = Integer.getInteger(time);
				int integer = Parser.convertStringTimeToSeconds(time);
				Log.d("integer", "tid: " + time);
				Log.d("integer", "sekunder:" + integer);
				tempTotal += integer;
			}
			Result result = new Result(user, UtilityClass.parseTimeStampToTimeAndDate(tp.getTimestamp()), steps, tempTotal );
			
			mainApp.results.add(result);
			//sort?
			times[mainApp.TPObjects.indexOf(tp)] = tempTotal;
			totalTime = Parser.convertSecondsToString(tempTotal);
			tempTotal = 0;
			//toDisplay.add(""+ user + "                                  " + totalTime);
		}
		
		Collections.sort(mainApp.results);
		for (Result rst : mainApp.results) {
			toDisplay.add(""+rst.getUser() + "          " + rst.getTimeToDisplay());
		}
		
		listViewResults = (ListView) findViewById(R.id.listViewResults);
		
		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, toDisplay);
		listViewResults.setAdapter(adapter);
		
		listViewResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View viw, int pos,
					long id) {
				Intent intent = new Intent(getBaseContext(), ResultDetailActivity.class);
				Bundle b = new Bundle();
				b.putInt("pos", pos);
				intent.putExtras(b);
				startActivity(intent);			
			}
		});
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
	@Override
	public void onSpaceChanged(int position) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDataFetchedFromSpace() {
		// TODO Auto-generated method stub
		
	}
}
