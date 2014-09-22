package com.example.providence;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;


public class MainActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationClient = new LocationClient(this, this, this);
		
		startService(new Intent(this,
                GPSLoggerService.class));
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		locationClient.connect();
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		locationClient.disconnect();
		super.onStop();
	}

	public void logout(View view) {
		// clear user key
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

	public void forceGetLocation(View view) {
		String userKey = Providence.getUserKey(this);
		try {
			Location response = Providence.getLocation(userKey);
			Providence.toast(this, "Got Lat: "+response.getLatitude()+" Long: "+response.getLongitude());
		} catch (IOException e) {
			Providence.toast(this, "Connection failed");
		}
	}

	public void forceSetLocation(View view) {
		String userKey = Providence.getUserKey(this);
		Location location = locationClient.getLastLocation();
		try {
			Providence.sendLocation(userKey,
					location.getTime(), 
					location.getLatitude(), 
					location.getLongitude());
			Providence.toast(this, "Sent Lat: "+location.getLatitude()+" Long: "+location.getLongitude());
		} catch (IOException e) {
			Providence.toast(this, "Connection failed");
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
} 
