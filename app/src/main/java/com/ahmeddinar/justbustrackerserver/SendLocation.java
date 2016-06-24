package com.ahmeddinar.justbustrackerserver;

import android.os.AsyncTask;

import com.ahmeddinar.justbustrackerserver.Interfaces.AsyncResponse;

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

/**
 * Created by Ahmed Dinar on 6/22/2016.
 */
public class SendLocation extends AsyncTask<String, String, String> {

    public AsyncResponse delegate=null;

    @Override
    protected String doInBackground(String... args) {

        String urlName = args[0];
        String latitude = args[1];
        String longitude = args[2];
        String busId = args[3];

        URL url = null;
        HttpURLConnection conn = null;
        OutputStream os = null;
        DataInputStream input;

        String error = "No error";

        try {

            url = new URL(urlName);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", busId);
            jsonParam.put("lat", latitude);
            jsonParam.put("long", longitude);

            String str = jsonParam.toString();


            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000 * 10); /*milliseconds*/
            conn.setConnectTimeout(1000 *15 ); /*milliseconds*/
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(str.getBytes().length);

            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            conn.connect();

            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(str.getBytes());
            os.flush();

            //Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            error =  response.toString();

        } catch (MalformedURLException e) {
            error = "MalformedURLException";
            e.printStackTrace();
        } catch (IOException e) {
            error = "IOException";
            e.printStackTrace();
        } catch (JSONException e) {
            error = "JSONException";
            e.printStackTrace();
        }


        return (error);
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.asyncFinish(result);
    }
}
