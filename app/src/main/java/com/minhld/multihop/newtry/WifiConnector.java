package com.minhld.multihop.newtry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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
        IntentFilter filters = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filters.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        c.registerReceiver(mWifiScanReceiver, filters);
    }

    /**
     * receive a wifi network list
     */
    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                if (scanListener != null) {
                    // only keep wifi direct network
                    for (int i = mScanResults.size() - 1; i >= 0; i--) {
                        if (!mScanResults.get(i).SSID.toLowerCase().contains("direct")) {
                            mScanResults.remove(i);
                        }
                    }
                    scanListener.listReceived(mScanResults);
                }
            } else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
                    boolean connected = checkConnectedToDesiredWifi(c);
                }
            }
        }
    };

    public void getWifiConnections() {
        mWifiManager.startScan();
    }

    public void connectWifiNetwork(ScanResult wifiNetwork, String password) {
        writeLog("attempt connecting to " + wifiNetwork.SSID);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + wifiNetwork.SSID + "\"";
        wifiConfiguration.preSharedKey = "\"" + password + "\"";
        int netId = mWifiManager.addNetwork(wifiConfiguration);

        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + wifiNetwork.SSID + "\"")) {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(i.networkId, true);
                mWifiManager.reconnect();
                break;
            }
        }

        writeLog("wait for establishment...");
    }

    /** Detect you are connected to a specific network. */
    private boolean checkConnectedToDesiredWifi(Context c) {
        boolean connected = false;

        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            writeLog("connected to: " + wifi.getSSID() + "; " +
                    "bssid: " + wifi.getBSSID());
        }
        return connected;
    }

    public interface WiFiScanListener {
        public void listReceived(List<ScanResult> mScanResults);
    }

    public void writeLog(final String msg){
        String outMsg = Utils.SDF.format(new Date()) + ": " + msg + "\n";
        logText.append(outMsg);
    }

}
