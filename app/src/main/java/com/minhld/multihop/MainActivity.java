package com.minhld.multihop;

import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.minhld.multihop.supports.JobHandler;
import com.minhld.multihop.supports.Utils;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.p2pBtn)
    Button p2pBtn;

    @BindView(R.id.wifiBtn)
    Button wifiBtn;

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.infoText)
    TextView infoText;

    JobHandler jobHandler;
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
                    UITools.writeLog(MainActivity.this, infoText, strMsg);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        // handlers registration
        jobHandler = new JobHandler(this, mainUiHandler);
        jobHandler.setSocketListener(new JobHandler.JobSocketListener() {
            @Override
            public void socketUpdated(final boolean isServer, final boolean isConnected) {
                // enable/disable the "Say Hi" button when its status changed
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // only server can send jobs to client, client cannot send job to server
                        // but client will send job result to server
                    }
                });
            }

            @Override
            public void peerListUpdated(Collection<WifiP2pDevice> deviceList) {
                deviceListAdapter.clear();
                deviceListAdapter.addAll(deviceList);
                deviceListAdapter.notifyDataSetChanged();
            }
        });

        p2pBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLog("attempting discover...");
                jobHandler.discoverPeers();

            }
        });
    }

    /**
     * write to log
     *
     * @param log
     */
    public void writeLog(String log) {
        UITools.writeLog(this, this.infoText, log);
    }

    /**
     * write to log with exception
     *
     * @param prefix
     * @param e
     */
    public void writeLog(String prefix, Exception e) {
        UITools.writeLog(this, this.infoText, prefix, e);
    }

}
