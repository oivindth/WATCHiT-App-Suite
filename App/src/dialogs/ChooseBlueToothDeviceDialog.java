package dialogs;

import java.util.ArrayList;

import no.ntnu.emergencyreflect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.watchit_connect.MainApplication;

public class ChooseBlueToothDeviceDialog extends SherlockDialogFragment {
	
	private ListAdapter adapter;
	private ArrayList<String> arrayAdapter;
	private MainApplication mApp;
	BlueToothSelectListener mListener;

	//Container Activity must implement this interface
	public interface BlueToothSelectListener {
		public void blueToothDeviceSelected(int which);
		public void cancelBluetoothPairedDialog();
		public void startBlueToothDiscovery();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mApp = MainApplication.getInstance();
		
		
		Bundle b = getArguments();
		arrayAdapter = b.getStringArrayList("adapter");
		adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, arrayAdapter);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.blueToothDeviceSelected(which);
			}
		});
	    
	    builder.setTitle(getString(R.string.bluetooth_choose_paired_devices));
	  
	           builder.setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // Update new server info
	            	    mListener.startBlueToothDiscovery();
	            	
	               }
	           });
	           builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   mApp.isWATChiTOn = false;
	                   mListener.cancelBluetoothPairedDialog();
	               }
	           });      
	    
	    builder.setTitle(getString(R.string.bluetooth));
	    
	    return builder.create();
	
}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (BlueToothSelectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement BlueToothSelectListener Listener");
		}
	}
	
}
