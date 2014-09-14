package com.example.providence;

import android.app.Activity;
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
		
		startActivity(intent);
	}
}
