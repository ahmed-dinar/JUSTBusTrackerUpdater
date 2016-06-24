package com.ahmeddinar.justbustrackerserver;

/**
 * Created by Ahmed Dinar on 6/22/2016.
 */
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmeddinar.justbustrackerserver.Interfaces.AsyncResponse;
import com.ahmeddinar.justbustrackerserver.rest.RestCient;
import com.ahmeddinar.justbustrackerserver.rest.model.BusLocation;
import com.ahmeddinar.justbustrackerserver.rest.model.PostResponse;
import com.ahmeddinar.justbustrackerserver.rest.service.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GPSTracker extends Service implements LocationListener {

    private final String sendUrl = "http://sheambd.com/bus/update.php";
    private final Context mContext;


    private SendLocation sendLocation;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;
    TextView updateInfo;
    TextView updateLog;
    Activity mActivity;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private String provider_info;

    public GPSTracker(Context context,Activity _activity) {
        this.mContext = context;
        this.mActivity = _activity;
        updateLog = (TextView) this.mActivity.findViewById(R.id.updateText);
        updateInfo = (TextView) this.mActivity.findViewById(R.id.infoText);
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {

                this.canGetLocation = true;
                updateLog.setText("Application use GPS Service");
                provider_info = LocationManager.GPS_PROVIDER;

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled

                this.canGetLocation = true;
                updateLog.setText("Application use Network State to get GPS coordinates");
                provider_info = LocationManager.NETWORK_PROVIDER;

            }

            if (!provider_info.isEmpty()) {

                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    updateLog.setText("Permission denied!");
                    return null;
                }

                locationManager.requestLocationUpdates(
                        provider_info,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                Log.d("Network", "Network");

                if (locationManager != null) {

                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location != null) {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        updateGPSCoordinates();

                    }else{
                        updateLog.setText("location not loaded");
                    }
                }else{
                    updateLog.setText("locationManager not working");
                }
            }

        } catch (Exception e) {
            updateLog.setText(e.getMessage().toString());
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }


    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {

        if(location!=null){
            this.location = location;
            updateGPSCoordinates();
        }else{
            updateLog.setText("location changed but location not loaded!");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /*public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            updateInfo.setText("Location Updating: " + String.valueOf(latitude) + " "  +  String.valueOf(longitude));

            //new postData().execute("hi there");




            sendLocation = new SendLocation();
            sendLocation.delegate = this;
            sendLocation.execute(sendUrl,String.valueOf(latitude),String.valueOf(longitude),"1");
        }
    }*/


    private void updateGPSCoordinates() {

        if(location == null){
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        updateInfo.setText("Latitude    :  " + String.valueOf(latitude) + "\nLongitude :  " + String.valueOf(longitude));
        updateLog.setText("Start Updating..");


        RestCient client = new RestCient();
        ApiService service = client.getApiService();

        // Prepare the HTTP request
        BusLocation busLocation = new BusLocation(String.valueOf(latitude), String.valueOf(longitude), "1");
        Call<PostResponse> call = service.postWithJson(busLocation);


        // Asynchronously execute HTTP request
        call.enqueue(new Callback<PostResponse>() {


            @Override
            public void onResponse(Response<PostResponse> response, Retrofit retrofit) {

                // http response status code + headers
                updateLog.setText(updateLog.getText() + "\nResponse status code: " + response.code());


                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccess()) {
                    // print response body if unsuccessful
                    try {
                        updateLog.setText(updateLog.getText() + " unsuccess1\n" + response.errorBody().string());
                    } catch (IOException e) {
                        updateLog.setText(updateLog.getText() + " unsuccess\n" + e.getMessage());
                    }
                    return;
                }

                // if parsing the JSON body failed, `response.body()` returns null
                PostResponse busLocationres = response.body();
                if (busLocationres == null) {
                    updateLog.setText(updateLog.getText() + "\nbus Locationres null");
                    return;
                }

                updateLog.setText(updateLog.getText() + "\nStatus: " + busLocationres.status + "\nmessage: " + busLocationres.message);
            }



            @Override
            public void onFailure(Throwable t) {
                updateLog.setText(updateLog.getText()  + "\nonFailure " + t.getMessage());
            }

        });

    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



}
