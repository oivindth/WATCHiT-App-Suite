package fragments;

import no.ntnu.emergencyreflect.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import enums.ValueType;


public class MoodDetailsFragment extends SherlockFragment {
	
	private String user;
	private String time;
	private String value;
	
	private TextView userNameTextView, timeTextView, valueTextView, textViewHeader;
	private ImageView moodImage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = this.getArguments();
		user = b.getString("user");
		time = b.getString("time");
		value = b.getString("value");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.mood_details_fragment, container, false);
		userNameTextView = (TextView) myFragmentView.findViewById(R.id.textViewUserActual);
		userNameTextView.setText(" :" + user);
		timeTextView= (TextView) myFragmentView.findViewById(R.id.textViewTimeActual);
		timeTextView.setText(" :" +time);
		valueTextView = (TextView) myFragmentView.findViewById(R.id.textViewMoodActual);
		valueTextView.setText(" :" + value);
		
		moodImage = (ImageView) myFragmentView.findViewById(R.id.imageViewMood);
		moodImage.setImageResource(getImageResource(value));
		
		return myFragmentView;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	public int getImageResource(String value) {
		ValueType valueType = ValueType.getValue(value);
		if (valueType.MOOD_NEUTRAL == valueType) return R.drawable.neutral_tag;
		if (valueType.MOOD_SAD == valueType) return R.drawable.sad_tag;
		else return R.drawable.happy_tag;
	}
}
