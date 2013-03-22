package dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockDialogFragment;


public class ChooseEventDialog extends SherlockDialogFragment {
	
	int checkedItem;
	private SharedPreferences spacePreferences;
	//private MainApplication mApp;
	
    ChooseEventDialogListener mListener;
    
    public interface ChooseEventDialogListener {
    	public void eventChosen(int which);
    }

    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle b = getArguments();
		checkedItem = b.getInt("checkedEvent", -1);
		List<String> events = b.getStringArrayList("events");

		String [] items = ( events.toArray(new String [events.size()]));
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());

		
		builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkedItem = which;
				mListener.eventChosen(which);
			}
		});
		
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//mListener.onSpaceChanged(checkedItem);
				
			}
		});


		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});      

		builder.setTitle("Select event");
		return builder.create();

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (ChooseEventDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SpacechangeListener interface");
		}
	}
	
}
