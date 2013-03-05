package com.example.watchit_connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.watchit_connect.R;

import de.imc.mirror.sdk.SpaceMember;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SpaceMembersFragment extends ListFragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.space_members, container, false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		Set<SpaceMember> mebers = ((SpacesActivity) getActivity()).getCurrentSpaceMembers();
			//mySpace.
		UpdateMemberInfo(mebers);
	}
	
	//Update ze fragment
		public void UpdateMemberInfo (Set<SpaceMember> members) {
			List<String> members2 = new ArrayList<String>();
			for (SpaceMember spaceMember : members) {
				members2.add(spaceMember.getJID());
			}
			   ArrayAdapter<String> arrayAdapter2 =      
			   new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, members2);
		       setListAdapter(arrayAdapter2);
		}

	
	
}

