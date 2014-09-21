package com.example.providence;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Providence {

	// help methods

	public static boolean hasInternet(Activity a) {
		ConnectivityManager check = (ConnectivityManager) 
				a.getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] info = check.getAllNetworkInfo();

		for (int i = 0; i<info.length; i++){
			if (info[i].getState() == NetworkInfo.State.CONNECTED){
				return true;
			}
		}

		return false;
	}

	public static String readStream(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();  
		BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);  
		for (String line = r.readLine(); line != null; line =r.readLine()){  
			sb.append(line);  
		}  
		is.close();  
		return sb.toString();
	}

	public static HashMap<String, String> urlParamsToKVP(String urlParams) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] pairs = urlParams.split("&");
		try {
			for(int i = 0; i < pairs.length; i++) {
				String[] kvp = pairs[i].split("=");
				map.put(kvp[0],kvp[1]);
			}
		}
		catch(Exception e) {
			return null;
		}
		return map;
	}

	public static void toast(Activity a, String msg) {
		Context context = a.getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, msg, duration);
		toast.show();
	}

	public static Location getLocation(String userKey) throws IOException {
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// push current location to server
		URL url = new URL("http://5.56.151.199:8080/getPos");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setConnectTimeout(10000);
		// add post content
		OutputStream os = urlConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(os, "US-ASCII"));
		writer.write("key="+userKey);
		writer.flush();
		writer.close();
		os.close();

		urlConnection.connect();

		BufferedInputStream in;
		in = new BufferedInputStream(urlConnection.getInputStream());
		String response = readStream(in);
		urlConnection.disconnect();

		HashMap<String,String> responseMap = urlParamsToKVP(response);
		Location loc = new Location("Server");
		try {
			loc.setLatitude(
					Double.parseDouble(
							responseMap.get("lat")
							.replace(",", ".")));
			loc.setLongitude(
					Double.parseDouble(
							responseMap.get("long")
							.replace(",", ".")));
			return loc;
		}
		catch(NullPointerException e) {
			loc = null;
			return loc;
		}
	}

	public static boolean sendLocation(String userKey, long time, double latitude, double longtitude) throws IOException {
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// push current location to server
		URL url = new URL("http://5.56.151.199:8080/setPos");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setConnectTimeout(10000);
		// add post content
		OutputStream os = urlConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(os, "US-ASCII"));
		writer.write("key="+userKey
				+"&time="+time
				+"&lat="+latitude
				+"&long="+longtitude);
		writer.flush();
		writer.close();
		os.close();

		urlConnection.connect();

		BufferedInputStream in;
		in = new BufferedInputStream(urlConnection.getInputStream());
		String response = readStream(in);
		urlConnection.disconnect();
		return response.equals("succes!");
	}

	public static String getUserKey(Context c) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
		String userKey = sharedPref.getString(c.getString(R.string.user_key), "");
		return userKey;
	}
}
