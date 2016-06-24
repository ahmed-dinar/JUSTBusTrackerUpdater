package com.ahmeddinar.justbustrackerserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    TextView updateInfo;
    TextView textUpdate;
    Button button;

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        updateInfo = (TextView)findViewById(R.id.updateText);
        textUpdate = (TextView)findViewById(R.id.infoText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(gpsTracker != null){
            updateInfo.setText("Already Running");
            return;
        }
        updateInfo.setText("Starting GPS tracker...");
        gpsTracker = new GPSTracker(this,this);
    }
}
