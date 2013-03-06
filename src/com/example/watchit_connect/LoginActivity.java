package com.example.watchit_connect;



import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfiguration;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import Utilities.UtilityClass;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well. Source: Android
 */
public class LoginActivity extends Activity {
	/**
	 * The default username to populate the username field with.
	 */
	public static final String EXTRA_USERNAME = "username";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	
	private MainApplication app;

	// Values for email and password at the time of the login attempt.
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	
	
	
	ConnectionConfigurationBuilder connectionConfigurationBuilder;
	ConnectionConfiguration connectionConfig;
	ConnectionHandler connectionHandler;
	

	// A special thanks to Telmo Marques from stackoverflow for how to display login activity only once.
	// source:  http://stackoverflow.com/questions/9964480/how-to-display-login-screen-only-one-time
	
	// We have a shared preference file for keeping track of the user has logged in before. 
	public static final String PREFS_NAME = "preferences";
	
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        app = MainApplication.getInstance();
	        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
	      //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
	      boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
	      if(hasLoggedIn) {
	    	  
	    	  String username = settings.getString("username", "");
	    	  String password = settings.getString("password", "");
	    	  //Configure connection
				connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
		        connectionConfigurationBuilder.setHost(getString(R.string.host));
		        connectionConfig = connectionConfigurationBuilder.build();
		        connectionHandler = new ConnectionHandler(username, password, connectionConfig);
			
		        updateGlobalConnectionVariables();
	    	  startMainActivity();
	      }
	    	  
	    	 
	  
	      setContentView(R.layout.activity_login);
	      
	   // Set up the login form.
			mUserName = getIntent().getStringExtra(EXTRA_USERNAME);
			mUserNameView = (EditText) findViewById(R.id.username);
			mUserNameView.setText("admin");

			//TODO: Only for debugging
			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView.setText("mirror");
			
			mPasswordView
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView textView, int id,
								KeyEvent keyEvent) {
							if (id == R.id.login || id == EditorInfo.IME_NULL) {
								attemptLogin();
								return true;
							}
							return false;
						}
					});

			mLoginFormView = findViewById(R.id.login_form);
			mLoginStatusView = findViewById(R.id.login_status);
			mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

			findViewById(R.id.sign_in_button).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							attemptLogin();
						}
					});
	      
	  }
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			super.onCreateOptionsMenu(menu);
			getMenuInflater().inflate(R.menu.activity_login, menu);
			return true;
		}
		/**
		 * Attempts to sign in or register the account specified by the login form.
		 * If there are form errors (invalid email, missing fields, etc.), the
		 * errors are presented and no actual login attempt is made.
		 */
		public void attemptLogin() {
			if (mAuthTask != null) {
				return;
			}

			// Reset errors.
			mUserNameView.setError(null);
			mPasswordView.setError(null);

			// Store values at the time of the login attempt.
			mUserName = mUserNameView.getText().toString();
			mPassword = mPasswordView.getText().toString();

			boolean cancel = false;
			View focusView = null;

			// Check for a valid password.
			if (TextUtils.isEmpty(mPassword)) {
				mPasswordView.setError(getString(R.string.error_field_required));
				focusView = mPasswordView;
				cancel = true;
			} else if (mPassword.length() < 4) {
				mPasswordView.setError(getString(R.string.error_invalid_password));
				focusView = mPasswordView;
				cancel = true;
			}

			// Check for a valid email address.
			if (TextUtils.isEmpty(mUserName)) {
				mUserNameView.setError(getString(R.string.error_field_required));
				focusView = mUserNameView;
				cancel = true;
			} else if (mUserName.contains("@")) {
				mUserNameView.setError(getString(R.string.error_invalid_email));
				focusView = mUserNameView;
				cancel = true;
			}

			if (cancel) {
				// There was an error; don't attempt login and focus the first
				// form field with an error.
				focusView.requestFocus();
			} else {
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new UserLoginTask();
				mAuthTask.execute((Void) null);
			}
		}
	  
		/**
		 * Shows the progress UI and hides the login form.
		 */
		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		private void showProgress(final boolean show) {
			// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
			// for very easy animations. If available, use these APIs to fade-in
			// the progress spinner.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				int shortAnimTime = getResources().getInteger(
						android.R.integer.config_shortAnimTime);

				mLoginStatusView.setVisibility(View.VISIBLE);
				mLoginStatusView.animate().setDuration(shortAnimTime)
						.alpha(show ? 1 : 0)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								mLoginStatusView.setVisibility(show ? View.VISIBLE
										: View.GONE);
							}
						});

				mLoginFormView.setVisibility(View.VISIBLE);
				mLoginFormView.animate().setDuration(shortAnimTime)
						.alpha(show ? 0 : 1)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								mLoginFormView.setVisibility(show ? View.GONE
										: View.VISIBLE);
							}
						});
			} else {
				// The ViewPropertyAnimator APIs are not available, so simply show
				// and hide the relevant UI components.
				mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			}
		}
	  
		/**
		 * Represents an asynchronous login/registration task used to authenticate
		 * the user.
		 */
		public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
			@Override
			protected Boolean doInBackground(Void... params) {

		        //Configure connection
				connectionConfigurationBuilder = new ConnectionConfigurationBuilder(getString(R.string.domain), getString(R.string.application_id));
		        connectionConfigurationBuilder.setHost(getString(R.string.host));
		        connectionConfig = connectionConfigurationBuilder.build();
		        connectionHandler = new ConnectionHandler(mUserName, mPassword, connectionConfig);
		      try {
		    	  connectionHandler.connect();
		      } catch (ConnectionStatusException e) {
		    	  e.printStackTrace();
		    	  return false;
		      }

				return true;
			}

			@Override
			protected void onPostExecute(final Boolean success) {
				mAuthTask = null;
				showProgress(false);

				if (success) {
					updateSharedPreferences();
					updateGlobalConnectionVariables();
					startMainActivity();
				} else {
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
					mUserNameView.setError(getString(R.string.error_incorrect_user));
					//mUserNameView.requestFocus();
				}
			}
			@Override
			protected void onCancelled() {
				mAuthTask = null;
				showProgress(false);
			}
		}
		
		
		/**
		 * Update the username and password for this cell phone to the shared preferences.
		 * Also sets that the user has logged in previously so he doesen't have to log in again.
		 */
	  public void updateSharedPreferences () {
		//User has successfully logged in, save this information
		// We need an Editor object to make preference changes.
		SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		//Set "hasLoggedIn" to true
		editor.putBoolean("hasLoggedIn", true);
		editor.putString("username", mUserName);
		editor.putString("password", mPassword);
		// Commit the edits!
		editor.commit();
	  }
	  
	  public void updateGlobalConnectionVariables() {
		  app.connectionConfigurationBuilder = connectionConfigurationBuilder;
		  app.connectionConfig = connectionConfig;
		  app.connectionHandler = connectionHandler;
		  app.dbName ="sdkcache";
		  SpaceHandler spaceHandler = new SpaceHandler(getBaseContext(), app.connectionHandler, app.dbName);
		  if (UtilityClass.isConnectedToInternet(getApplicationContext())) {
			  spaceHandler.setMode(Mode.ONLINE);
		  } else {
			  spaceHandler.setMode(Mode.OFFLINE);
		  }
		  

	    	
	    	app.spaceHandler = spaceHandler;
		  
	    	 app.setPassword(mPassword);
			  app.setUserName(mUserName);
	  }
	  
	  private void startMainActivity() {
		  Intent intent = new Intent(this, MainActivity.class);
          //intent.setClass(LoginActivity.this, SpacesActivity.class);
          startActivity(intent);
          finish();
	  }	  
}