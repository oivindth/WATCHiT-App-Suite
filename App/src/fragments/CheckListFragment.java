package fragments;

import no.ntnu.emergencyreflect.R;
import activities.QuizActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class CheckListFragment extends SherlockFragment {
	
	ListView checkList;
	ListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.fragment_checklist, container, false);
		
		String[] values = new String[] { "Safe the area", "Get help from others", "Check pulse and awarness", "Put in recovery position"};
		
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, values);
		
		
		
		checkList = (ListView) myFragmentView.findViewById(R.id.listViewCheckList);
		checkList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		checkList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// TODO Auto-generated method stub
				
			}
		});
		//checkList.setItemChecked(0, true);
		checkList.setAdapter(adapter);
		
		return myFragmentView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
}
