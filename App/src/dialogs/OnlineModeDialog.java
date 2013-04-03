package dialogs;

import com.actionbarsherlock.app.SherlockDialogFragment;

import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class OnlineModeDialog extends SherlockDialogFragment {
	
	private OnlineModeDialogListener mListener;
	
	//Container Activity must implement this interface
		public interface OnlineModeDialogListener {
			public void onOKChooseOnlineSourceDialogButtonClick();
		}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

 		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());		
	           builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   mListener.onOKChooseOnlineSourceDialogButtonClick();  
	               }
	           });
	           builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   // Do nothing, just return.
	               }
	           });      
	    
	    builder.setTitle(getString(R.string.internet));
	    
	    return builder.create();
	
}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnlineModeDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnlineModeDialog Listener");
		}
	}
	
}
