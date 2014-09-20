package com.example.providence;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
		
		SharedPreferences sharedPref = this.getSharedPreferences(
		        getString(R.string.preference_key), Context.MODE_PRIVATE);
		String userKey = sharedPref.getString(getString(R.string.user_key), "");
		if(!userKey.isEmpty()) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
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
			// interwebz black maegi
			StrictMode.ThreadPolicy policy = new StrictMode.
					ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);

			String response;
			try {
				// prepare http request
				URL url = new URL("http://5.56.151.199:8080/login");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setConnectTimeout(10000);
				// add post content
				OutputStream os = urlConnection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write("user="+username+"&password="+password);
				writer.flush();
				writer.close();
				os.close();

				urlConnection.connect();

				BufferedInputStream in;
				in = new BufferedInputStream(urlConnection.getInputStream());
				response = Providence.readStream(in);
				urlConnection.disconnect();

				// parse response string
				HashMap<String, String> responseMap = Providence.urlParamsToKVP(response);
				if(responseMap!=null) {
					if(!responseMap.isEmpty() 
							&& responseMap.get("loggedIn").equals("true")
							&& responseMap.get("key") != null) {
						// login successful!
						String userKey = responseMap.get("key");
						
						SharedPreferences sharedPref = this.getSharedPreferences(
						        getString(R.string.preference_key), Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString(getString(R.string.user_key),userKey);
						editor.commit();
						
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
				}
				else { // response didn't parse properly
					Providence.toast(this, "Unexpected response");
				}

			}
			catch(SocketTimeoutException e) {
				// Request timed out
				Providence.toast(this, "Request timed out");
			}
			catch(Exception e) {
				// TODO: Handle login error
				Providence.toast(this, "Something went wrong..");
			}

		}
		else
		{
			Providence.toast(this, "No internet connection!");
		}
	}
}
