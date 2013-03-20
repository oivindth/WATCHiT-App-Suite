package dialogs;

import java.util.ArrayList;
import java.util.List;

import listeners.SpaceChangeListener;
import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.Space;
import dialogs.ServerSettingsDialog.ServerSettingsDialogListener;
import enums.SharedPreferencesNames;

public class ChooseAvatarDialog extends SherlockDialogFragment {
	
	private SharedPreferences profilePrefs;
	private int checkedItem;
	
	ChooseAvatarListener mListener;

	//Container Activity must implement this interface
	public interface ChooseAvatarListener {
		public void avatarChosen(int which);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//mApp = MainApplication.getInstance();

		profilePrefs = getActivity().getSharedPreferences(SharedPreferencesNames.PROFILE_PREFERENCES, 0);
		checkedItem = profilePrefs.getInt("checked", -1);
		
		//Bundle b = getArguments();
		//ArrayList<String> events = b.getStringArrayList("adapter");
		//adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, events);
		List<String> events = new ArrayList<String>();

	
		String [] items = new String [] {"Wrestler", "Womanizer", "Mask", "Devil", "Scream"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());

		
		builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkedItem = which;
				SharedPreferences.Editor ed = profilePrefs.edit();
				ed.putInt("checked", checkedItem);
				ed.commit();
			}
		});
		
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				mListener.avatarChosen(checkedItem);
				
			}
		});


		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
			mListener = (ChooseAvatarListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement listener interface");
		}
	}
	
}
