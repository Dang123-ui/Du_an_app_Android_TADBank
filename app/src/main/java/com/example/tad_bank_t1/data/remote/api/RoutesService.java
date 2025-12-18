package com.example.tad_bank_t1.data.remote.api;

import com.example.tad_bank_t1.data.remote.dto.ComputeRoutesRequest;
import com.example.tad_bank_t1.data.remote.dto.ComputeRoutesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RoutesService {
    @POST("directions/v2:computeRoutes")
    Call<ComputeRoutesResponse> computeRoutes(
            @Header("X-Goog-Api-Key") String apiKey,
            @Header("X-Goog-FieldMask") String fieldMask,
            @Body ComputeRoutesRequest body
    );
}

