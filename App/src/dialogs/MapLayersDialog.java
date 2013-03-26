package dialogs;

import listeners.LayersChangeListener;
import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import com.actionbarsherlock.app.SherlockDialogFragment;

import enums.SharedPreferencesNames;

public class MapLayersDialog extends SherlockDialogFragment  {

	final CharSequence[] items = {"Persons", "Mood", "Notes", "Only me"};
    private boolean[] states = {false, false, false,false};
    private SharedPreferences mapPreferences;
    
    LayersChangeListener mListener;

    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mapPreferences = getActivity().getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , 0 );
		states[3] = mapPreferences.getBoolean("user_layer", false);
		states[2] = mapPreferences.getBoolean("notes_layer", false);
		states[1] = mapPreferences.getBoolean("mood_layer", false);
	    states[0] = mapPreferences.getBoolean("person_layer", false);	
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		
		 builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
		        public void onClick(DialogInterface dialogInterface, int item, boolean state) {
		        	
		        }
		    });
		builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			 public void onClick(DialogInterface dialog, int id)
		    {
				SharedPreferences.Editor ed = mapPreferences.edit();
		        SparseBooleanArray checked = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
		        boolean onlyme = checked.get(3);
		        boolean notes = checked.get(2);
		        boolean moods = checked.get(1);
		        boolean person = checked.get(0);
		        
		        ed.putBoolean("user_layer", onlyme);
		        ed.putBoolean("notes_layer", notes);
		        ed.putBoolean("person_layer", person);
		        ed.putBoolean("mood_layer", moods);
		        ed.commit();
		        
		        mListener.onLayersChanged(person, moods, notes, onlyme);
 
		    }
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Do nothing...
			}
		});      

		builder.setTitle("Layer");
		return builder.create();

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (LayersChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement MapLayersDialogListener interface");
		}
	}

}
