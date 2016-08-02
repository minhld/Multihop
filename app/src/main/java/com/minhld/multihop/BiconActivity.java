package com.minhld.multihop;

import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.minhld.multihop.newtry.WifiBroader;
import com.minhld.multihop.newtry.WifiConnector;
import com.minhld.multihop.newtry.WifiNetworkListAdapter;
import com.minhld.multihop.newtry.WifiPeerListAdapter;
import com.minhld.multihop.supports.Utils;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BiconActivity extends AppCompatActivity {
    @BindView(R.id.createGroupBtn)
    Button createGroupBtn;

    @BindView(R.id.discoverBtn)
    Button discoverBtn;

    @BindView(R.id.connectGroupBtn)
    Button connectGroupBtn;

    @BindView(R.id.searchWiFiBtn)
    Button searchWiFiBtn;

    @BindView(R.id.connectWiFiBtn)
    Button connectWiFiBtn;

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.wifiList)
    ListView wifiList;

    @BindView(R.id.infoText)
    TextView infoText;

    WifiBroader wifiBroader;
    WifiConnector orgWifiBroader;
    IntentFilter mIntentFilter;

    WifiPeerListAdapter deviceListAdapter;
    WifiNetworkListAdapter networkListAdapter;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.MESSAGE_READ_SERVER: {
                    String strMsg = msg.obj.toString();
                    UITools.writeLog(BiconActivity.this, infoText, strMsg);
                    break;
                }
                case Utils.MESSAGE_READ_CLIENT: {
                    String strMsg = msg.obj.toString();
                    UITools.writeLog(BiconActivity.this, infoText, strMsg);
                    break;
                }
                case Utils.MAIN_JOB_DONE: {

                    break;
                }
                case Utils.MAIN_INFO: {
                    String strMsg = (String) msg.obj;
                    UITools.writeLog(BiconActivity.this, infoText, strMsg);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicon);

        ButterKnife.bind(this);
        infoText.setMovementMethod(new ScrollingMovementMethod());

        // ------ Prepare for WiFi Direct ------
        wifiBroader = new WifiBroader(this, infoText);
        wifiBroader.setSocketHandler(mainUiHandler);
        wifiBroader.setBroadCastListener(new WifiBroader.BroadCastListener() {
            @Override
            public void peerDeviceListUpdated(Collection<WifiP2pDevice> deviceList) {
                deviceListAdapter.clear();
                deviceListAdapter.addAll(deviceList);
                deviceListAdapter.notifyDataSetChanged();
            }

            @Override
            public void socketUpdated(Utils.SocketType socketType, boolean connected) {

            }
        });
        mIntentFilter = wifiBroader.getSingleIntentFilter();

        // device list
        deviceListAdapter = new WifiPeerListAdapter(this, R.layout.row_devices, wifiBroader);
        deviceList.setAdapter(deviceListAdapter);

        // ------ Prepared for Original WiFi ------
        orgWifiBroader = new WifiConnector(this, infoText);
        orgWifiBroader.setmWifiScanListener(new WifiConnector.WiFiScanListener() {
            @Override
            public void listReceived(List<ScanResult> mScanResults) {
                networkListAdapter.clear();
                networkListAdapter.addAll(mScanResults);
                networkListAdapter.notifyDataSetChanged();
            }
        });

        // WiFi network list
        networkListAdapter = new WifiNetworkListAdapter(this, R.layout.row_wifi, orgWifiBroader);
        wifiList.setAdapter(networkListAdapter);

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.createGroup();
            }
        });

        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.discoverPeers();
            }
        });

        connectGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.writeString("sent a ACK :)");
            }
        });

        searchWiFiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search for Wifi network list
                orgWifiBroader.getWifiConnections();
            }
        });

        connectWiFiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // connect to one of the Wifi networks
                wifiBroader.requestGroupInfo();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiBroader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(wifiBroader, mIntentFilter);
    }
}
