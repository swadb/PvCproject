package com.example.providence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

}
