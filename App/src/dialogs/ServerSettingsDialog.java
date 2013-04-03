package dialogs;

import activities.LoginActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import no.ntnu.emergencyreflect.R;

public class ServerSettingsDialog extends SherlockDialogFragment {
	
	EditText hostView, domainView, portView;
	String host, domain, applicationId;
	int port;
	ServerSettingsDialogListener mListener;

	//Container Activity must implement this interface
	public interface ServerSettingsDialogListener {
		public void saveServerSettingsButtonClick();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		   // Get host and domain from sharedpreferences or use default values(mirror-server-ntnu) if they are not set.
        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        host = settings.getString("host", getString(R.string.host));
    	domain = settings.getString("domain", getString(R.string.domain));
    	applicationId = getString(R.string.application_id);
    	port = settings.getInt("port", 5222); //5222 is standard port.
		
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.dialog_server_settings, null);
	    builder.setView(view);
	    portView = (EditText) view.findViewById(R.id.port);
		domainView = (EditText) view.findViewById(R.id.domain);
		hostView  = (EditText) view.findViewById(R.id.host);
		
	    portView.setText(String.valueOf(port));
	    domainView.setText(domain);
	    hostView.setText(host);
		
	           builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // Update new server info
	            		String stringport = portView.getText().toString();
	             	   int portint = Integer.parseInt(stringport);
	             	   SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
	            		SharedPreferences.Editor editor = settings.edit();
	            		//Set "hasLoggedIn" to true
	            		editor.putString("domain", domainView.getText().toString());
	            		editor.putString("host", hostView.getText().toString());
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
	    
	    builder.setTitle(getString(R.string.server));
	    
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
