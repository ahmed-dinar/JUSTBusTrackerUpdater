package com.ahmeddinar.justbustrackerserver.rest.model;

/**
 * Created by Ahmed Dinar on 6/24/2016.
 */
public class BusLocation {
    String latitude;
    String longitude;
    String busId;

    public BusLocation(String latitude, String longitude, String busId){
        this.latitude = latitude;
        this.longitude = longitude;
        this.busId = busId;
    }
}
