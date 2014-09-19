package com.example.providence;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	public void attemptLogin(View view) {
		Intent intent = new Intent(this, MainActivity.class);

		EditText tusername = (EditText) findViewById(R.id.username);
		String username = tusername.getText().toString();

		EditText tpassword = (EditText) findViewById(R.id.password);
		String password = tpassword.getText().toString();

		// check for internet connection

		if(Providence.hasInternet(this))
		{
			//try {
			// send login request
			// for future use: http://5.56.151.199:8080/log

			// interwebz black maegi
			StrictMode.ThreadPolicy policy = new StrictMode.
					ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);

			URL url;
			try {
				url = new URL("http://5.56.151.199:8080/log?user="+username+"&password="+password);
				HttpURLConnection urlConnection;
				urlConnection = (HttpURLConnection) url.openConnection();
				BufferedInputStream in;
				in = new BufferedInputStream(urlConnection.getInputStream());
				String response = Providence.readStream(in);
				urlConnection.disconnect();
				
				//TODO: parse response
			}
			catch(Exception e) {
				// TODO: Handle login error
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, "Runtime exception", duration);
				toast.show();
			}


			//TODO: parse response string


			//TODO: Replace with login result from http request
			if(username.equals("admin") && password.equals("admin")) {
				//TODO: save login credentials

				startActivity(intent);
			}
			else {
				// incorrect login information
				// clear password
				tpassword.setText("");
				password = "";

				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Login failed");
				alertDialog.setMessage("The provided information is invalid.");
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Add your code for the button here.
						dialog.cancel();
					}
				});
				alertDialog.show();
				// see http://androidsnippets.com/simple-alert-dialog-popup-with-title-message-icon-and-button
			}
			//}
			/*
			catch(MalformedURLException e){
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, "Malformed URL", duration);
				toast.show();
			}
			catch(IOException e){
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, "IO Exception", duration);
				toast.show();
			}
			catch(Exception e) {
				// TODO: Handle login error
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, "Uncaught exception", duration);
				toast.show();
			}
			 */

		}
		else
		{
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, "No internet connection!", duration);
			toast.show();
		}

		/*
		if(username.equals("admin") && password.equals("admin")) {
			// successful login
			startActivity(intent);
		} else {
			// unsuccessful login

			// clear password
			tpassword.setText("");
			password = "";

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Login failed");
			alertDialog.setMessage("The provided information is invalid.");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Add your code for the button here.
					dialog.cancel();
				}
			});
			alertDialog.show();
			// see http://androidsnippets.com/simple-alert-dialog-popup-with-title-message-icon-and-button
		}
		 */
	}
}
