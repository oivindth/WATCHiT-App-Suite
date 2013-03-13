package dialogs;

import no.ntnu.emergencyreflect.R;
import activities.LoginActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class MapLayersDialog extends SherlockDialogFragment {

	final CharSequence[] items = {"Persons", "Mood"};
    final boolean[] states = {false, false, false};
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

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
		        SparseBooleanArray CheCked = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
		        if (CheCked.get(0))
		        {
		            Toast.makeText(getActivity().getBaseContext(), "Item 1", Toast.LENGTH_SHORT).show();
		        }
		        if (CheCked.get(1))
		        {
		            Toast.makeText(getActivity().getBaseContext(), "Item 2", Toast.LENGTH_SHORT).show();
		        }
		        if (CheCked.get(2))
		        {
		            Toast.makeText(getActivity().getBaseContext(), "Item 3", Toast.LENGTH_SHORT).show();
		        }
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

}
