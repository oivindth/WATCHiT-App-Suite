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

	final CharSequence[] items = {"Persons", "Mood", "Notes"};
    private boolean[] states = {false, false, false};
    private SharedPreferences mapPreferences;
    
    LayersChangeListener mListener;

    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mapPreferences = getActivity().getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , 0 );
		states[2] = mapPreferences.getBoolean("notes_layer", false);
		states[1] = mapPreferences.getBoolean("mood_layer", false);
	    states[0] = mapPreferences.getBoolean("person_layer", false);	
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		//View view = inflater.inflate(R.layout.dialog_server_settings, null);
		//builder.setView(view);
		
		
		
		 builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
		        public void onClick(DialogInterface dialogInterface, int item, boolean state) {
		        	
		        }
		    });
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			 public void onClick(DialogInterface dialog, int id)
		    {
				SharedPreferences.Editor ed = mapPreferences.edit();
		        SparseBooleanArray checked = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
		        boolean notes = checked.get(2);
		        boolean moods = checked.get(1);
		        boolean person = checked.get(0);
		        
		        ed.putBoolean("notes_layer", notes);
		        ed.putBoolean("person_layer", person);
		        ed.putBoolean("mood_layer", moods);
		        ed.commit();
		        
		        mListener.onLayersChanged(person, moods, notes);
 
		    }
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Do nothing...
			}
		});      

		builder.setTitle("Layers");
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
