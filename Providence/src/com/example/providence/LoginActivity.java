package com.example.providence;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String userKey = sharedPref.getString(getString(R.string.user_key), "");
		if(!userKey.isEmpty()) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
	
	public void onLoginStart() {
		// spinning ring of progress
		progressDialog = ProgressDialog.show(LoginActivity.this, "Login", 
				"Loading. Please wait...", true);
	}
	
	public void onLoginStop() {
		progressDialog.dismiss();
	}

	@SuppressWarnings("unchecked")
	public void attemptLogin(View view) {
		
		EditText tusername = (EditText) findViewById(R.id.username);
		String username = tusername.getText().toString();

		EditText tpassword = (EditText) findViewById(R.id.password);
		String password = tpassword.getText().toString();
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("user", username);
		map.put("password", password);
		
		AsyncLogin loginTask = new AsyncLogin();
		loginTask.execute(map);
	}
	

	private class AsyncLogin extends AsyncTask<Map<String, String>, Void, String> {

		@Override
		protected void onPreExecute() {
			LoginActivity.this.onLoginStart();
		}
		
		@Override
		protected String doInBackground(Map<String, String>... maps) {
			if(maps[0] == null || maps[0].isEmpty())
				return null;

			try {
				URL url = new URL(Providence.loginURL);
				return Providence.serverRequest(url, maps[0]);
			} catch (IOException e) {
				// Auto-generated catch block
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			LoginActivity.this.onLoginStop();
			if(response == null) {
				// TODO connection failed
			}
			else {
				HashMap<String, String> kvps = Providence.urlParamsToKVP(response);
				if(kvps!=null) {
					if(!kvps.isEmpty() 
							&& kvps.get("loggedIn").equals("true")
							&& kvps.get("key") != null) {
						// login successful!
						String userKey = kvps.get("key");

						SharedPreferences sharedPref = Providence.getPreference(LoginActivity.this);
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString(getString(R.string.user_key),userKey);
						editor.commit();

						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
					}
					else {
						// incorrect login information
						// clear password
						((EditText) findViewById(R.id.password)).setText("");

						AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
						alertDialog.setTitle("Login failed");
						alertDialog.setMessage("The provided information is invalid.");
						alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// Add your code for the button here.
								dialog.cancel();
							}
						});
						alertDialog.show();
						// see http://androidsnippets.com/simple-alert-dialog-popup-with-title-message-icon-and-button
					}
				}
			}

		}

	}
}



//TODO: Strings called from Strings.xml?
