package com.minhld.multihop;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.minhld.multihop.newtry.WifiBroader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BiconActivity extends AppCompatActivity {
    @BindView(R.id.p2pBtn)
    Button p2pBtn;

    @BindView(R.id.wifiBtn)
    Button wifiBtn;

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.infoText)
    TextView infoText;

    WifiBroader wifiBroader;
    IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicon);

        ButterKnife.bind(this);
        infoText.setMovementMethod(new ScrollingMovementMethod());

        wifiBroader = new WifiBroader(this, infoText);
        mIntentFilter = wifiBroader.getSingleIntentFilter();

        p2pBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.createGroup();
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
