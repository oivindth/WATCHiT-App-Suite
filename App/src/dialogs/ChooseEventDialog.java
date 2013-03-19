package dialogs;

import java.util.ArrayList;

import listeners.SpaceChangeListener;
import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.watchit_connect.MainApplication;

import enums.SharedPreferencesNames;

public class ChooseEventDialog extends SherlockDialogFragment {
	
	private ListAdapter adapter;
	int checkedItem;
	private SharedPreferences spacePreferences;
	private MainApplication mApp;
	
    SpaceChangeListener mListener;

    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mApp = MainApplication.getInstance();
		spacePreferences = getActivity().getSharedPreferences(SharedPreferencesNames.SPACE_PREFERENCES , 0 );
		spacePreferences.getInt("checked", 0);
		
		Bundle b = getArguments();
		ArrayList<String> events = b.getStringArrayList("adapter");
		adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, events);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());

		
		
		builder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkedItem = which;
				SharedPreferences.Editor ed = spacePreferences.edit();
				ed.putInt("checked", checkedItem);
				ed.commit();
				
				
			}
		});
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				mListener.onSpaceChanged(checkedItem);
				
			}
		});


		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mApp.eventConnected = false;
				mListener.onCancelChangeSpaceClicked();
			}
		});      

		builder.setTitle("Select event");
		return builder.create();

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (SpaceChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SpacechangeListener interface");
		}
	}
	
}
