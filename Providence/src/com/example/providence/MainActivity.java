package com.example.providence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void logout(View view) {
		// clear user key
		SharedPreferences sharedPref = this.getSharedPreferences(
		        getString(R.string.preference_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.user_key),"");
		editor.commit();
		// go to login screen
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	public void goToMap(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
} 
