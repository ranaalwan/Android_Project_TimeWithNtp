package com.example.timewithntp;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView systemTimeTextView;
    private TextView networkTimeTextView;
    private Handler handler;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        systemTimeTextView = findViewById(R.id.systemTimeTextView);
        networkTimeTextView = findViewById(R.id.networkTimeTextView);
        handler = new Handler();

        // Uppdatera tiden vid start
        updateSystemTime();
        updateNetworkTime();

        // Uppdatera tid varje sekund
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateSystemTime();
                updateNetworkTime();
                handler.postDelayed(this, 1000); 
            }
        }, 1000);
    }

    private void updateSystemTime() {
        Date date = new Date(System.currentTimeMillis());
        String time = timeFormat.format(date);
        systemTimeTextView.setText("System Time: " + time);

        
        systemTimeTextView.setTextSize(46); 
        systemTimeTextView.setTextColor(Color.RED); 
    }

    private void updateNetworkTime() {
        NTPUDPClient client = new NTPUDPClient();
        Thread networkTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress addr = InetAddress.getByName("time.google.com");
                    client.open();
                    TimeInfo info = client.getTime(addr);
                    client.close();

                    Date networkTime = new Date(info.getReturnTime());
                    String time = timeFormat.format(networkTime);

                    
                    runOnUiThread(() -> {
                        networkTimeTextView.setTextSize(46); 

                        networkTimeTextView.setTextColor(Color.GREEN); 
                        networkTimeTextView.setText("Network Time: " + time);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkTimeThread.start();
    }
}