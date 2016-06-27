package com.ahmeddinar.JUSTBusTrackerUpdater.rest.service;

import com.ahmeddinar.JUSTBusTrackerUpdater.rest.model.BusLocation;
import com.ahmeddinar.JUSTBusTrackerUpdater.rest.model.PostResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Ahmed Dinar on 6/24/2016.
 */

public interface ApiService {

    @GET("coordinate/all")
    Call<BusLocation> get();

    @POST("coordinate/update")
    Call<PostResponse> postWithJson(@Body BusLocation busLocation);
}
