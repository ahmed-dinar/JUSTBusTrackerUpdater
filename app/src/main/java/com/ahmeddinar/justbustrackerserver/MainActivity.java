package com.ahmeddinar.justbustrackerserver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmeddinar.justbustrackerserver.utils.ConnectionUtils;

public class MainActivity extends AppCompatActivity {

    TextView updateInfo;
    TextView textUpdate;

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        updateInfo = (TextView) findViewById(R.id.updateText);


            updateInfo.setText("Starting GPS tracker...");
            gpsTracker = new GPSTracker(this, this);



    }


}
