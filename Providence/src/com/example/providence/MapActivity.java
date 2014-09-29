package com.example.providence;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class MapActivity extends Activity {

	private GoogleMap map;
	private Timer updateTimer;
	private Map<String, Marker> markerMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		map.setMyLocationEnabled(true);

		// add marker
		map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.map_pin))
				.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
				.position(new LatLng(56.171973, 10.188218))); // Hopper bygning
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (map == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}
	
	private class AsyncLocationGet extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String userKey = Providence.getUserKey(MapActivity.this);
			
			try {
				Location response = Providence.getLocation(userKey);
			} catch (IOException e) {
				// TODO some error message
			}
			
			return null;
		}
		
	}
}
