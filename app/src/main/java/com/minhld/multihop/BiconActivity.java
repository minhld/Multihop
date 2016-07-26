package com.minhld.multihop;

import android.content.IntentFilter;
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
import com.minhld.multihop.newtry.WifiPeerListAdapter;
import com.minhld.multihop.supports.Utils;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BiconActivity extends AppCompatActivity {
    @BindView(R.id.createGroupBtn)
    Button createGroupBtn;

    @BindView(R.id.discoverBtn)
    Button discoverBtn;

    @BindView(R.id.connectGroupBtn)
    Button connectGroupBtn;

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.infoText)
    TextView infoText;

    WifiBroader wifiBroader;
    IntentFilter mIntentFilter;

    WifiPeerListAdapter deviceListAdapter;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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

        deviceListAdapter = new WifiPeerListAdapter(this, R.layout.row_devices, wifiBroader);
        deviceList.setAdapter(deviceListAdapter);

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
//                wifiBroader.connect("7a:f8:82:9e:e0:e9", "minh-owner");
                wifiBroader.connect("d2:17:c2:76:0b:e7", "Android_f9d4");
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
