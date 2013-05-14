package asynctasks;

import java.util.List;

import activities.BaseActivity;
import android.os.AsyncTask;
import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.SpaceConfiguration;
import de.imc.mirror.sdk.android.SpaceMember;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;


/**
 * @deprecated Not been tested, use at own risk!
 * @author oivindth
 *
 */
public class CreateSpaceTask extends AsyncTask<Void, Void, Boolean> {


	private BaseActivity mActivity;
	private String mSpaceName;
	private String mOwnerJID;
	private Space.Type mType;
	private boolean mPersistent;
	private List <String> mUserNames;

	public CreateSpaceTask (BaseActivity activity, String spaceName, Space.Type type, boolean persistent, String ownerJID, List <String> userNames ) {
		mActivity = activity;
		mSpaceName = spaceName;
		mType = type;
		mPersistent = persistent;
		mOwnerJID= ownerJID;
		mUserNames = userNames;

	}
	@Override
	protected Boolean doInBackground(Void... params) {

		// create space configuration with the current user as space moderato
		SpaceConfiguration spaceConfig = new SpaceConfiguration(mType, mSpaceName, mOwnerJID, mPersistent);

		// example of how to add user.
		//String bobsJID = "bob" + "@" + MainApplication.connectionHandler.getConfiguration().getDomain();
		//spaceConfig.addMember(new SpaceMember(bobsJID, SpaceMember.Role.MEMBER));
		for (String name : mUserNames) {
			String JID = name + "@" + MainApplication.getInstance().connectionHandler.getConfiguration().getDomain();
			spaceConfig.addMember(new SpaceMember(JID, SpaceMember.Role.MEMBER));
		}

		try {
			MainApplication.getInstance().spaceHandler.createSpace(spaceConfig);
		} catch (SpaceManagementException e) {
			e.printStackTrace();
			return false;
		} catch (ConnectionStatusException e) {
			e.printStackTrace();
			// add exception handling
			return false;
		}
		return true;
	}

	protected void onPostExecute(final Boolean success) {
		if (success) {
			mActivity.showToast("Space created succsessfully");
		} else {
			mActivity.showToast("Failed to create space..");
		}
	}


}
