package com.example.providence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
}
