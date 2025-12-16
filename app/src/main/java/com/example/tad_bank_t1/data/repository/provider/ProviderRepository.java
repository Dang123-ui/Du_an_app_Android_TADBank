package com.example.tad_bank_t1.data.repository.provider;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tad_bank_t1.data.model.remote.Provider;
import com.example.tad_bank_t1.data.network.ApiConfig;
import com.example.tad_bank_t1.data.network.ApiResponse;
import com.example.tad_bank_t1.data.network.ApiService;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderRepository {

    private ApiService api;

    public ProviderRepository() {
        api = ApiConfig.getClient().create(ApiService.class);
    }

    public LiveData<ResultWrapper<List<Provider>>> getProviders(String type) {
        MutableLiveData<ResultWrapper<List<Provider>>> live = new MutableLiveData<>();

        live.setValue(ResultWrapper.loading());

        api.getProviders(type).enqueue(new Callback<List<Provider>>() {
            @Override
            public void onResponse(Call<List<Provider>> call, Response<List<Provider>> response) {
                if (response.isSuccessful()) {
                    live.setValue(ResultWrapper.success(response.body()));
                } else {
                    live.setValue(ResultWrapper.error("Server error"));
                }
            }

            @Override
            public void onFailure(Call<List<Provider>> call, Throwable t) {
                live.setValue(ResultWrapper.error(t.getMessage()));
            }
        });

        return live;
    }

    // tim kiếm mobile
    public LiveData<ResultWrapper<Boolean>> checkTopup(String phoneNumber) {
        MutableLiveData<ResultWrapper<Boolean>> live = new MutableLiveData<>();

        live.setValue(ResultWrapper.loading());

        api.checkTopup(phoneNumber).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Boolean> apiResponse = response.body();
                    live.setValue(ResultWrapper.success(apiResponse.data));
                } else if (response.code() == 404) {
                    live.setValue(ResultWrapper.error("Không tìm thấy số điện thoại: " + response.body().message));
                }

                Log.d("checkTopup", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                live.setValue(ResultWrapper.error(t.getMessage()));
                Log.d("checkTopup", "onFailure: " + t.getMessage());
            }
        });


        return live;
    }
}
