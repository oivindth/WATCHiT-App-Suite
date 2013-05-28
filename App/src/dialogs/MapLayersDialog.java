package dialogs;
/**
 * Copyrigth 2013 ¯ivind Thorvaldsen
 * This file is part of Reflection App

    Reflection App is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
import listeners.LayersChangeListener;
import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import com.actionbarsherlock.app.SherlockDialogFragment;

import enums.SharedPreferencesNames;

public class MapLayersDialog extends SherlockDialogFragment  {

	private CharSequence[] items;
    private boolean[] states = {false, false, false,false};
    private SharedPreferences mapPreferences;
    
    LayersChangeListener mListener;

    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		items = new CharSequence [] {getString(R.string.persons), getString(R.string.moods), getString(R.string.notes), getString(R.string.only_me)};
		
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
		builder.setTitle(getString(R.string.datapoints));
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
