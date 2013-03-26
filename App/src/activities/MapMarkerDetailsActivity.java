package activities;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import fragments.MoodDetailsFragment;
import fragments.NoteDetailsFragment;
import fragments.PersonDetailsFragment;
import no.ntnu.emergencyreflect.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MapMarkerDetailsActivity extends BaseActivity {
	
	PersonDetailsFragment personDetailsFragment;
	MoodDetailsFragment moodDetailsFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_found_activity);
		
		
		Intent intent = getIntent();
		Bundle b = new Bundle();
		b.putString("user", intent.getStringExtra("user"));
		b.putString("time", intent.getStringExtra("time"));
		b.putString("value", intent.getStringExtra("value"));
		//b.putString("lat", data.getLocation().getLatitude());
		//b.putString("lng", data.getLocation().getLongitude());
		
		if (intent.getStringExtra("unit").equals("mood") ) {
			moodDetailsFragment = new MoodDetailsFragment();
			moodDetailsFragment.setArguments(b);
			 getSupportFragmentManager().beginTransaction()
	         .add(R.id.fragment_container_person_activity, moodDetailsFragment ,"mood_details").commit();
		} else
		
		if (intent.getStringExtra("unit").equals("person")) {
			personDetailsFragment = new PersonDetailsFragment();
			personDetailsFragment.setArguments(b);
			 getSupportFragmentManager().beginTransaction()
	         .add(R.id.fragment_container_person_activity, personDetailsFragment ,"persondetails").commit();
		} else
		if (intent.getStringExtra("unit").equals("notes")) {
			NoteDetailsFragment notedetailsfrag = new NoteDetailsFragment();
			notedetailsfrag.setArguments(b);
			getSupportFragmentManager().beginTransaction()
	         .add(R.id.fragment_container_person_activity, notedetailsfrag ,"notedetails").commit();
		}
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
