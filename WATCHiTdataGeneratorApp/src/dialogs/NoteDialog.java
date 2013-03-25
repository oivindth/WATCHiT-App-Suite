package dialogs;

import no.ntnu.wathcitdatageneratorapp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import dialogs.ShareDialog.ShareDialogListener;

public class NoteDialog extends SherlockDialogFragment {
	
	EditText editNotes;
	ImageView image;
	
	private NoteDialogListener mListener;
	
	//Container Activity must implement this interface
	public interface NoteDialogListener {
		public void onShareNoteButtonClicked(String notes);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
		Bundle b = getArguments();
		int imageResource = b.getInt("imageID");
		String text = b.getString("text");
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.note_dialog, null);
	    builder.setView(view);
	    
	    image = (ImageView) view.findViewById(R.id.imageViewNoteDialog);
	    editNotes = (EditText) view.findViewById(R.id.editTextNote);
	    
	    image.setImageResource(imageResource);
	    
		
	           builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   mListener.onShareNoteButtonClicked(" " + editNotes.getText().toString());
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
			mListener = (NoteDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement NoteDialogListener interface");
		}
	}
	
}
