package activities;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fragments.CheckListFragment;

public class QuizActivity extends BaseActivity {
	
	CheckListFragment checkListFrag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.quiz_activity);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		checkListFrag = new CheckListFragment();
		
		getSupportFragmentManager().beginTransaction()
        .add(R.id.quiz_fragment_container, checkListFrag ,"persondetails").commit();
	

	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.main, menu);
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
