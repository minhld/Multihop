package com.minhld.multihop.newtry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.TextView;

import com.minhld.multihop.supports.ClientSocketHandler;
import com.minhld.multihop.supports.ServerSocketHandler;
import com.minhld.multihop.supports.Utils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Date;

/**
 * Created by minhld on 7/25/2016.
 */

public class WifiBroader extends BroadcastReceiver {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    IntentFilter mIntentFilter;

    TextView logText;

    public WifiBroader(Activity c, TextView logText){
        this.logText = logText;

        this.mManager = (WifiP2pManager)c.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(c, c.getMainLooper(), null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if (info.groupFormed && info.isGroupOwner) {

                    } else if (info.groupFormed) {

                    } else {

                    }
                }
            });
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }

    public void createGroup() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                writeLog("group created successfully");
            }

            @Override
            public void onFailure(int reason) {
                switch (reason){
                    case 0: {
                        writeLog("group create failed: operation failed due to an internal error");
                        break;
                    }
                    case 1: {
                        writeLog("group create failed: p2p is unsupported on the device");
                        break;
                    }
                    case 2: {
                        writeLog("group create failed: framework is busy and unable to service the request");
                        break;
                    }
                }
            }
        });
    }

    public IntentFilter getSingleIntentFilter() {
        if (mIntentFilter == null) {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }

        return mIntentFilter;
    }

    public void writeLog(final String msg){
        String outMsg = Utils.SDF.format(new Date()) + ": " + msg;
        logText.setText(outMsg);
    }
}
