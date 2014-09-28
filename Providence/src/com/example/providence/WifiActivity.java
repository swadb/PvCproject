package com.example.providence;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WifiActivity extends Activity {

	private TextView wifiText;
	private WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
    private List<ScanResult> wifiList;
    private StringBuilder sb = new StringBuilder();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);
		
		wifiText = (TextView) findViewById(R.id.text_wifi);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiManager.startScan();
	}
	
	private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
        	Providence.toast(c, "Hurray!");
        	wifiList = wifiManager.getScanResults();
        	boolean first = true;
        	String wifiListString = "";
        	for(ScanResult r : wifiList) {
        		if(!first) {
        			wifiListString += "\n";
        		}
        		else {
        			first = false;
        		}
        		wifiListString += r.SSID;
        	}
        	wifiText.setText(wifiListString);
        		
        	/*
            sb = new StringBuilder();
            wifiList = wifiManager.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){
                sb.append(new Integer(i+1).toString() + ".");
                sb.append((wifiList.get(i)).toString());
                sb.append("\\n");
            }
            wifiText.setText(sb);
            */
        }
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        wifiManager.startScan();
        wifiText.setText("Starting Scan");
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
}
