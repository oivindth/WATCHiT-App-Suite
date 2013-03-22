package dialogs;

import no.ntnu.wathcitdatageneratorapp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ShareDialog extends SherlockDialogFragment {
	
	TextView shareTextView;
	ImageView imageView;

	private ShareDialogListener mListener;
	
	//Container Activity must implement this interface
	public interface ShareDialogListener {
		public void onShareDialogButtonClicked();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
		Bundle b = getArguments();
		int imageResource = b.getInt("imageID");
		String text = b.getString("text");
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.share_dialog, null);
	    builder.setView(view);
	    
	    shareTextView = (TextView) view.findViewById(R.id.textViewShareDialog);
	    imageView = (ImageView) view.findViewById(R.id.imageViewShareDialog);
	    
	    imageView.setImageResource(imageResource);
	    shareTextView.setText(text);
	    
	           builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   mListener.onShareDialogButtonClicked();
	               }
	           });
	           builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   // Do nothing...
	               }
	           });      
	    builder.setTitle("Share");
	    return builder.create();
}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (ShareDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ServerSettingsDialog Listener");
		}
	}
}
