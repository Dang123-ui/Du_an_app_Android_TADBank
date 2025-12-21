package com.example.tad_bank_t1.data.model.ai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiMarketApi {
    @POST("/predict_auto")
    Call<PredictAutoResponse> predictAuto(@Body PredictAutoRequest req);
}
