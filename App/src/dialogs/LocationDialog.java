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
import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import com.actionbarsherlock.app.SherlockDialogFragment;

import dialogs.OnlineModeDialog.OnlineModeDialogListener;

public class LocationDialog extends SherlockDialogFragment {

private LocationDialogListener mListener;
	
	//Container Activity must implement this interface
		public interface LocationDialogListener {
			public void onOKChooseNetworkForLocationClick();
			public void onCancelLocationDialogClick();
			//public void onCancelButtonClicked();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {


			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());		
			builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mListener.onOKChooseNetworkForLocationClick(); 
				}
			});
			builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Do nothing, just return.
				}
			});      
			builder.setMessage(getString(R.string.location_enable_sensors));
			builder.setTitle(getString(R.string.location));

			return builder.create();
	
}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (LocationDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnlineModeDialog Listener");
		}
	}

}
