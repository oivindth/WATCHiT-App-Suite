package fragments;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

		String[] values = new String[] { getString(R.string.checklist_step_1), getString(R.string.checklist_step_2), getString(R.string.checklist_step_3), getString(R.string.checklist_step_4)};
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
		checkList = (ListView) myFragmentView.findViewById(R.id.listViewCheckList);
		//checkList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		//checkList.setItemChecked(0, true);
		checkList.setAdapter(adapter);

		checkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if (position == 0) Toast.makeText(getActivity(), getString(R.string.checklist_step_1_elaborated), Toast.LENGTH_LONG).show();
				if (position == 1) Toast.makeText(getActivity(), getString(R.string.checklist_step_2_elaborated), Toast.LENGTH_LONG).show();
				if (position == 2) Toast.makeText(getActivity(), getString(R.string.checklist_step_3_elaborated), Toast.LENGTH_LONG).show();
				if (position == 3) Toast.makeText(getActivity(), getString(R.string.checklist_step_4_elaborated), Toast.LENGTH_LONG).show();

			}
		});

		return myFragmentView;
	}


	@Override
	public void onResume() {
		super.onResume();
	}


}
