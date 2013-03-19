package asynctasks;

import com.example.watchit_connect.MainApplication;

import activities.BaseActivity;
import android.os.AsyncTask;
import android.util.Log;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;

	/**
	 * AsyncTask used for publishing dataobject to a space.
	 * @author oivindth
	 *
	 */
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
			sApp = MainApplication.getInstance();
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
	    		 Log.d("publish", "datahandler: " + sApp.dataHandler);
	    		 Log.d("publish", "spaceid: " + mSpaceId);
				  sApp.dataHandler.publishDataObject(mDataObject, mSpaceId);
				  Log.d("publish", "dataobject: " + mDataObject.toString());
				  Log.d("publish", "just published dataobject with id: " + mDataObject.getId());
	    	 }
				  catch (NullPointerException e) {
					  e.printStackTrace();
					  Log.d("ERROR:", "trying again...");
					  new PublishDataTask(mDataObject, mSpaceId).execute();
				  
				  
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("ERROR:", "failed to publish dataobject with id: " + mDataObject.getId() );
				return false;
			}
	    	 return true;
		
		}

		protected void onPostExecute(final Boolean success) {
		
			if (success) {
			Log.d("publish", "success");
			} else {
			Log.d("ERROR:", "Something went wrong");
			//mActivity.showToast("Failed to publish dataobject.....");
			}
		}
 	}
	

