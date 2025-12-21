package com.example.tad_bank_t1.data.remote.api;

import com.example.tad_bank_t1.data.remote.dto.OrsDirectionsRequest;
import com.example.tad_bank_t1.data.remote.dto.OrsDirectionsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OrsDirectionsService {
    @POST("v2/directions/foot-walking/geojson")
    Call<OrsDirectionsResponse> getWalkingRoute(
            @Header("Authorization") String apiKey,
            @Body OrsDirectionsRequest body
    );
}
