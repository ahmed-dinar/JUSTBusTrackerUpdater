package com.ahmeddinar.JUSTBusTrackerUpdater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
