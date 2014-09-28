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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Providence {
	
	public static final int requestTimeout = 10000;
	public static final String serverURL = "http://5.56.151.199:8080/";
	public static final String loginURL = "http://5.56.151.199:8080/login";
	public static final String getLocationURL = "http://5.56.151.199:8080/getPos";
	public static final String setLocationURL = "http://5.56.151.199:8080/setPos";

	// help methods

	public static boolean hasInternet(Context c) {
		ConnectivityManager check = (ConnectivityManager) 
				c.getSystemService(Context.CONNECTIVITY_SERVICE);

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

	public static void toast(Context c, String msg) {
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(c, msg, duration);
		toast.show();
	}

	public static Location getLocation(String userKey) throws IOException {
		URL url = new URL(getLocationURL);
		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("key", userKey);
		
		String response = serverRequest(url, postParams);
		
		HashMap<String, String> kvps = urlParamsToKVP(response);
		Location loc = new Location("Server");
		try {
			loc.setLatitude(
					Double.parseDouble(
							kvps.get("lat")
							.replace(",", ".")));
			loc.setLongitude(
					Double.parseDouble(
							kvps.get("long")
							.replace(",", ".")));
			return loc;
		}
		catch(NullPointerException e) {
			loc = null;
		}
		return loc;
		/*
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
		*/
	}

	public static boolean sendLocation(String userKey, long time, double latitude, double longtitude) throws IOException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key",userKey);
		params.put("time", Long.toString(time));
		params.put("lat", Double.toString(latitude));
		params.put("long", Double.toString(longtitude));
		
		// black maegi
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		String response = serverRequest(new URL(Providence.setLocationURL), params);
		
		return response.equals("succes!");
	}
	
	public static String serverRequest(URL url, Map<String, String> params) throws IOException {
		// black maegi
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setConnectTimeout(Providence.requestTimeout);
		// add post content
		OutputStream os = urlConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(os, "US-ASCII"));
		// prepare post params
		boolean first = true;
		String postParams = "";
		for(String key : params.keySet())
		{
			if(!first) {
				postParams += "&";
			}
			else {
				first = false;
			}
			postParams += key+"="+params.get(key);
		}
		// write post params
		writer.write(postParams);
		writer.flush();
		writer.close();
		os.close();
		
		urlConnection.connect();

		BufferedInputStream in;
		in = new BufferedInputStream(urlConnection.getInputStream());
		String response = readStream(in);
		urlConnection.disconnect();
		return response;
	}

	public static String getUserKey(Context c) {
		SharedPreferences sharedPref = Providence.getPreference(c);
		String userKey = sharedPref.getString(c.getString(R.string.user_key), "");
		return userKey;
	}
	
	public static SharedPreferences getPreference(Context c) {
		return PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	public static void notify(Context c, String title, String msg, int notificationID) {
		NotificationManager locMsg = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification.Builder(c).setContentTitle(title)
				.setContentText(msg)
				.setSmallIcon(R.drawable.ic_notification)
				.build();

		locMsg.notify(notificationID, notification);
	}
}
