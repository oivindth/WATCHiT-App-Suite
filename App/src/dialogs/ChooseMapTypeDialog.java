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
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.Space;
import enums.SharedPreferencesNames;

public class ChooseMapTypeDialog extends SherlockDialogFragment {

		
		private ListAdapter adapter;
		int checkedItem;
		private SharedPreferences mapPreferences;
		private MainApplication mApp;
		final CharSequence[] items = {"Normal", "Sattelite", "Terrain", "Hybrid"};
	    
		MapTypeChangeListener mListener;
		
		
		public interface MapTypeChangeListener {
			public void onMapTypeChanged(int which);
		}
		
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			mApp = MainApplication.getInstance();
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
			
			
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mListener.onMapTypeChanged(checkedItem);
					
				}
			});


			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});      

			builder.setTitle("Map Type");
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
