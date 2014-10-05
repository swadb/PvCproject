package com.example.providence;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class GPSLoggerService
extends Service
implements GooglePlayServicesClient.ConnectionCallbacks,
OnConnectionFailedListener,
LocationListener {

	private static float minDistanceMeters = 50;
	private static float minAccuracyMeters = 100;

	private static long requestInterval = 5*60*1000;
	private static long fastestRequestInterval = 60*1000;
	
	private static int locationSource = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

	private static boolean showDebugNotification = true;

	// added by jamarino
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private Location lastSentLocation;
	private String userKey;

	/** Called when the activity is first created. */
	@Override
	public void onCreate() {
		userKey = Providence.getUserKey(this);
		
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(requestInterval);
		mLocationRequest.setFastestInterval(fastestRequestInterval);
		mLocationRequest.setPriority(locationSource);

		mLocationClient = new LocationClient(this,this,this);
		
		if(showDebugNotification) {
			Providence.notify(this, "Service started", "Background service was started", R.string.notification_service_started);
		}
		
		mLocationClient.connect();
	}

	@Override
	public void onLocationChanged(Location loc) {
		if(loc.hasAccuracy() && loc.getAccuracy() <= minAccuracyMeters) { // data is accurate enough
			float distance = 0;
			if(lastSentLocation != null)
				distance = lastSentLocation.distanceTo(loc);
			if(lastSentLocation == null
					|| distance > loc.getAccuracy()
					&& distance > minDistanceMeters) { // moved out of accuracy of last location
				AsyncLocationUpdate asyncTask = new AsyncLocationUpdate();
				asyncTask.execute(loc);
			}
		}
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		GPSLoggerService getService() {
			return GPSLoggerService.this;
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		try {
			lastSentLocation = Providence.getLocation(userKey);
		} catch (IOException e) {
			lastSentLocation = mLocationClient.getLastLocation();
		}
		if(showDebugNotification) {
			Providence.notify(this, "Connected!", "Google API connected", R.string.notification_fubar);
		}
	}

	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
	
	private class AsyncLocationUpdate extends AsyncTask<Location, Void, Void> {

		@Override
		protected Void doInBackground(Location... params) {
			Location loc = params[0];
			try {
				Providence.sendLocation(userKey, loc.getTime(), loc.getLatitude(), loc.getLongitude());
				lastSentLocation = loc;
				if(showDebugNotification) {
					Providence.notify(GPSLoggerService.this, "Location updated", "Location sent to server", R.string.notification_location_sent);
				}
			} catch (IOException e) {
				if(showDebugNotification) {
					Providence.notify(GPSLoggerService.this, "Update failed", "Updating location failed", R.string.notification_location_update_error);
				}
			}
			return null;
		}

	}

}
