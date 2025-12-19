package com.example.tad_bank_t1.data.repository.bill;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tad_bank_t1.data.model.remote.Bill;
import com.example.tad_bank_t1.data.remote.config.ApiConfig;
import com.example.tad_bank_t1.data.remote.dto.ApiResponse;
import com.example.tad_bank_t1.data.remote.api.ApiService;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillRepository {
    private ApiService api;

    public BillRepository() {
        api = ApiConfig.getClient().create(ApiService.class);
    }


    public LiveData<ResultWrapper<Bill>> getBillByProviderAndCustomer(String providerId, String customerCode) {
        MutableLiveData<ResultWrapper<Bill>> live = new MutableLiveData<>();

        live.setValue(ResultWrapper.loading());

        api.getBillByProviderAndCustomer(providerId, customerCode).enqueue(new Callback<ApiResponse<Bill>>() {
            @Override
            public void onResponse(Call<ApiResponse<Bill>> call, Response<ApiResponse<Bill>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Bill> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.success) {
                        live.setValue(ResultWrapper.success(apiResponse.data));
                    } else {
                        assert apiResponse != null;
                        live.setValue(ResultWrapper.error(apiResponse.message));
                    }
                } else {
                    live.setValue(ResultWrapper.error("Server error with code " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Bill>> call, Throwable t) {
                live.setValue(ResultWrapper.error(t.getMessage()));
            }
        });

        return live;
    }
}
