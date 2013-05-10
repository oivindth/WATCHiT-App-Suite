package activities;

import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import asynctasks.EndPointsTestTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ProceduresActivity extends BaseActivity {

	private ListView listOfProecdures;
	private ArrayAdapter<String> adapter;
	private String [] procedures = new String [] {"Test", "CorcuenoSafety" }; //TODO: hardcoded procedures
	private Button newButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_procedures);
		listOfProecdures = (ListView) findViewById(R.id.listViewProcedures);	
		
		newButton = (Button) findViewById(R.id.buttonNew);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new EndPointsTestTask().execute(getBaseContext());
				showToast("Test step added. Check backend!");
			}
		});
		
		adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, procedures);
		listOfProecdures.setAdapter(adapter);
		
		listOfProecdures.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				
				Bundle b = new Bundle();
				b.putString("procedure_name", procedures[position]);
				
				Intent intent = new Intent(getBaseContext(), ProcedureActivity.class);
				startActivity(intent);
				finish();
			}
		});
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