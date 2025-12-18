package com.example.tad_bank_t1.data.repository.ticket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tad_bank_t1.data.model.remote.Flight;
import com.example.tad_bank_t1.data.model.remote.Movie;
import com.example.tad_bank_t1.data.remote.config.ApiConfig;
import com.example.tad_bank_t1.data.remote.dto.ApiResponse;
import com.example.tad_bank_t1.data.remote.api.ApiService;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketRepository {
    private ApiService api;

    public TicketRepository() {
        api = ApiConfig.getClient().create(ApiService.class);
    }

    public LiveData<ResultWrapper<List<Flight>>> getFlights() {
        MutableLiveData<ResultWrapper<List<Flight>>> live = new MutableLiveData<>();

        live.setValue(ResultWrapper.loading());

        api.getFlights().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Flight>>> call, Response<ApiResponse<List<Flight>>> response) {
                if (response.isSuccessful()) {
                    live.setValue(ResultWrapper.success(response.body().data));
                } else {
                    live.setValue(ResultWrapper.error("Server error"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Flight>>> call, Throwable t) {
                live.setValue(ResultWrapper.error(t.getMessage()));
            }
        });

        return live;
    }

    public LiveData<ResultWrapper<List<Movie>>> getMovies() {
        MutableLiveData<ResultWrapper<List<Movie>>> live = new MutableLiveData<>();

        live.setValue(ResultWrapper.loading());

        api.getMovies().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Movie>>> call, Response<ApiResponse<List<Movie>>> response) {
                if (response.isSuccessful()) {
                    live.setValue(ResultWrapper.success(response.body().data));
                } else {
                    live.setValue(ResultWrapper.error("Server error"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Movie>>> call, Throwable t) {
                live.setValue(ResultWrapper.error(t.getMessage()));
            }
        });

        return live;
    }
}
