package com.example.providence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void goToMap(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		
		EditText tusername = (EditText) findViewById(R.id.username);
		String username = tusername.getText().toString();
		
		EditText tpassword = (EditText) findViewById(R.id.password);
		String password = tpassword.getText().toString();
		
		// attempt login
		
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
	}
}
