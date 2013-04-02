package fragments;

import parsing.GenericSensorData;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.watchit_connect.MainApplication;

import dialogs.ChooseAvatarDialog;
import enums.SharedPreferencesNames;
import no.ntnu.emergencyreflect.R;
import activities.GatewayActivity;
import activities.MapActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ProfileFragment extends SherlockFragment {


	private MainApplication mApp;
	private TextView textViewUser, textViewBagdes;
	private ImageView imageAvatar, imageMoodBagde, imageMedalBagde;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = MainApplication.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
		textViewUser = (TextView) myFragmentView.findViewById(R.id.textViewUser);
		textViewUser.setText("User:  " + mApp.connectionHandler.getCurrentUser().getUsername());
		imageAvatar = (ImageView) myFragmentView.findViewById(R.id.imageViewAvatar);
		//imageMoodBagde = (ImageView) myFragmentView.findViewById(R.id.imageViewMoodBadge);
		//imageMedalBagde = (ImageView) myFragmentView.findViewById(R.id.imageViewMedalBagde);
		textViewBagdes = (TextView) myFragmentView.findViewById(R.id.textViewBagdes);
		
		return myFragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
	
		SharedPreferences profilePrefs = getActivity().getSharedPreferences(SharedPreferencesNames.PROFILE_PREFERENCES, 0);
		int checkedItem = profilePrefs.getInt("checked", 0);
		
		updateView(checkedItem);
		
		imageAvatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					new ChooseAvatarDialog().show(getFragmentManager(), "profilefrag");
			}
		});
		
		

	}
	
	public void updateView(int which) {
		if (which == 0) imageAvatar.setImageResource(R.drawable.avatar0);
		if (which == 1) imageAvatar.setImageResource(R.drawable.avatarwoman);
		if (which ==2) imageAvatar.setImageResource(R.drawable.jason);
		if (which == 3) imageAvatar.setImageResource(R.drawable.devil);
		if (which == 4) imageAvatar.setImageResource(R.drawable.scream);
	}
}
