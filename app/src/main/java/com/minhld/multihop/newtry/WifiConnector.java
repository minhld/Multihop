package com.minhld.multihop.newtry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.TextView;

import com.minhld.multihop.supports.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by minhld on 7/26/2016.
 */

public class WifiConnector {

    WifiManager mWifiManager;
    WiFiScanListener scanListener;

    TextView logText;

    public void setmWifiScanListener(WiFiScanListener scanListener) {
        this.scanListener = scanListener;
    }

    public WifiConnector(Activity c, TextView logText) {
        this.logText = logText;
        mWifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        c.registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                if (scanListener != null) {
                    scanListener.listReceived(mScanResults);
                }
            }
        }
    };

    public void getWifiConnections() {
        mWifiManager.startScan();
    }

    public interface WiFiScanListener {
        public void listReceived(List<ScanResult> mScanResults);
    }

    public void writeLog(final String msg){
        String outMsg = Utils.SDF.format(new Date()) + ": " + msg + "\n";
        logText.append(outMsg);
    }

}
