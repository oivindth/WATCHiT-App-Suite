package dialogs;

import no.ntnu.wathcitdatageneratorapp.R;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SettingsDialog extends SherlockDialogFragment {

	private SettingsDialogListener mListener;
	private String host ,domain, applicationId;
	int port;
	
	private String username, password;
	private EditText portView, domainView, hostView, userNameView, passwordView;
	
	//Container Activity must implement this interface
	public interface SettingsDialogListener {
		public void saveSettingsButtonClick();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		   // Get host and domain from sharedpreferences or use default values(mirror-server-ntnu) if they are not set.
        SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
        host = settings.getString("host", getString(R.string.host));
    	domain = settings.getString("domain", getString(R.string.domain));
    	applicationId = getString(R.string.application_id);
    	port = settings.getInt("port", 5222); //5222 is standard port.
		username = settings.getString("username", "");
		password = settings.getString("password", "");
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.settings_dialog, null);
	    builder.setView(view);
	    portView = (EditText) view.findViewById(R.id.port);
		domainView = (EditText) view.findViewById(R.id.domain);
		hostView  = (EditText) view.findViewById(R.id.host);
		userNameView = (EditText) view.findViewById(R.id.editTextUsername);
		passwordView = (EditText) view.findViewById(R.id.editTextPassword);
		
		
	    portView.setText(String.valueOf(port));
	    domainView.setText(domain);
	    hostView.setText(host);
	    userNameView.setText(username);
	    passwordView.setText(password);
		
	           builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // Update new server info
	            	   
	            		String stringport = portView.getText().toString();
	             	   int portint = Integer.parseInt(stringport);
	             	   SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
	            		SharedPreferences.Editor editor = settings.edit();
	            		//Set "hasLoggedIn" to true
	            		editor.putString("domain", domainView.getText().toString());
	            		editor.putString("host", hostView.getText().toString());
	            		editor.putInt("port", portint);
	            		editor.putString("username", userNameView.getText().toString());
	            		editor.putString("password", passwordView.getText().toString());
	            		editor.commit();
	            		mListener.saveSettingsButtonClick();
	            	
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
			mListener = (SettingsDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener interface");
		}
	}
	


	
}
