package asynctasks;

import com.example.watchit_connect.MainApplication;

import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;

	public class PublishDataTask extends AsyncTask<Void, Void, Boolean> {
		  
		private BaseActivity mActivity;
		private DataObject mDataObject;
		private String mSpaceId;
		private MainApplication sApp;
		
		public PublishDataTask (BaseActivity activiy, DataObject dataObject, String spaceId) {
			mActivity = activiy;
			mDataObject = dataObject;
			mSpaceId = spaceId;
			sApp = MainApplication.getInstance();
		}
		public PublishDataTask (DataObject dataObject, String spaceId) {
			mDataObject = dataObject;
			mSpaceId = spaceId;
		}
		
        @Override
        protected void onPreExecute() {
        	super.onPreExecute();
        }
		@Override
		protected Boolean doInBackground(Void... params) {
	    	 try {
	    		 
	    		 // sApp.spaceHandler.setMode(Mode.ONLINE);
	      		 // sApp.dataHandler.setMode(Mode.ONLINE);
	      		 // sApp.dataHandler.registerSpace("team#42");
				  sApp.dataHandler.publishDataObject(mDataObject, mSpaceId);
			} catch (UnknownEntityException e) {
				e.printStackTrace();
				return false;
			}
	    	 return true;
		}

		protected void onPostExecute(final Boolean success) {
			if (success) {
			Log.d("PUBLISHDATATASK", "data object published! ");
			mActivity.showToast("dataobject published to space with id: " + mSpaceId);
			} else {
			Log.d("PUBLISHDATATASK", "Something went wrong");
			mActivity.showToast("Failed to publish dataobject.....");
			}
		}
 	}
	

