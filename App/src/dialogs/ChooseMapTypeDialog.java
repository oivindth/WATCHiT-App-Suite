package dialogs;

import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.watchit_connect.MainApplication;

import enums.SharedPreferencesNames;

public class ChooseMapTypeDialog extends SherlockDialogFragment {

	private ListAdapter adapter;
	int checkedItem;
	private SharedPreferences mapPreferences;
	private MainApplication mApp;
	private CharSequence[] items;

	MapTypeChangeListener mListener;

	public interface MapTypeChangeListener {
		public void onMapTypeChanged(int which);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mApp = MainApplication.getInstance();
		items = new CharSequence[] {getActivity().getString(R.string.map_type_normal),getActivity().getString(R.string.map_type_sattelite), 
				getActivity().getString(R.string.map_type_terrain),getActivity().getString(R.string.map_type_hybrid)};
		mapPreferences = getActivity().getSharedPreferences(SharedPreferencesNames.MAP_PREFERENCES , 0 );
		checkedItem = mapPreferences.getInt("mapTypePos", 0);

		//adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, events);
		//String [] items = ( events.toArray(new String [events.size()]));

		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkedItem = which;
				SharedPreferences.Editor ed = mapPreferences.edit();
				ed.putInt("mapTypePos", checkedItem);
				ed.commit();
			}
		});

		builder.setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				mListener.onMapTypeChanged(checkedItem);
			}
		});

		builder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});      

		builder.setTitle(getString(R.string.map_type));
		return builder.create();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (MapTypeChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement interface");
		}
	}



}
