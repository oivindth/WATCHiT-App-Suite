package asynctasks;

import com.example.watchit_connect.BaseActivity;
import com.example.watchit_connect.MainApplication;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;



	public class PublishDataTask extends AsyncTask<Void, Void, Boolean> {
		  
		
		private BaseActivity mActivity;
		private DataObject mDataObject;
		
		
		public PublishDataTask (BaseActivity activiy, DataObject dataObject) {
			mActivity = activiy;
			mDataObject = dataObject;
		}
		
        @Override
        protected void onPreExecute() {
        	super.onPreExecute();
        }
        
		@Override
		protected Boolean doInBackground(Void... params) {
	    	 try {
				MainApplication.dataHandler.publishDataObject(mDataObject, "team#34");
			} catch (UnknownEntityException e) {
			
				e.printStackTrace();
				return false;
			}
	    	 
	    	 return true;
		}

		protected void onPostExecute(final Boolean success) {
			
			if (success) {
			Log.d("PUBLISHDATATASK", "data object published!");
			} else {
			Log.d("PUBLISHDATATASK", "Something went wrong");
			}
		}
 	}
	

