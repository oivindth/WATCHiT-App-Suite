package dialogs;

import activities.LoginActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.watchit_connect.R;

import fragments.ApplicationsSettingsFragment.ApplicationsSettingsFfragmentListener;

public class ServerSettingsDialog extends SherlockDialogFragment {
	
	
	EditText host, domain, port;

	ServerSettingsDialogListener mListener;
	

	//Container Activity must implement this interface
	public interface ServerSettingsDialogListener {
		public void saveServerSettingsButtonClick();
			
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.dialog_server_settings, null);
	    builder.setView(view);
	    port = (EditText) view.findViewById(R.id.port);
		domain = (EditText) view.findViewById(R.id.domain);
		host  = (EditText) view.findViewById(R.id.host);
	    
	           builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // Update new server info
	            	   
	            	   Log.d("dialog", port.getText().toString());
	            		String stringport = port.getText().toString();
	             	   int portint = Integer.parseInt(stringport);
	             	   SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
	            		SharedPreferences.Editor editor = settings.edit();
	            		//Set "hasLoggedIn" to true
	            		editor.putString("domain", domain.getText().toString());
	            		editor.putString("host", host.getText().toString());
	            		editor.putInt("port", portint);
	            		editor.commit();
	            	mListener.saveServerSettingsButtonClick();
	            	
	               }
	           });
	           builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   // Do nothing...
	               }
	           });      
	    
	    builder.setTitle("Server");
	    
	
	    
	    return builder.create();
	
}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (ServerSettingsDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ServerSettingsDialog Listener");
		}
	}
	


	
	
}
