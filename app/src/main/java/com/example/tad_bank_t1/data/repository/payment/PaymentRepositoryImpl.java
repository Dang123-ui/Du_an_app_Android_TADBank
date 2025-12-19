package com.example.tad_bank_t1.data.repository.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tad_bank_t1.data.remote.api.ApiService;
import com.example.tad_bank_t1.data.remote.config.ApiConfig;
import com.example.tad_bank_t1.data.remote.dto.ApiResponse;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentReq;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentRes;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepositoryImpl implements PaymentRepository {
    private ApiService api;

    public PaymentRepositoryImpl() {
        api = ApiConfig.getClient().create(ApiService.class);
    }

    @Override
    public void createPaymentVnpay(CreatePaymentReq req, ResultCallback<CreatePaymentRes> callback) {
        callback.onLoading();

        api.createPaymentVnpay(req).enqueue(new Callback<ApiResponse<CreatePaymentRes>>() {
            @Override
            public void onResponse(Call<ApiResponse<CreatePaymentRes>> call, Response<ApiResponse<CreatePaymentRes>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<CreatePaymentRes> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.success) {
                        callback.onSuccess(apiResponse.data);
                    } else {
                        assert apiResponse != null;
                        callback.onError("Server error with code " + response.code());
                    }
                } else {
                    callback.onError("Server error with code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CreatePaymentRes>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
