package com.example.tad_bank_t1.data.remote.api;

import com.example.tad_bank_t1.data.model.remote.Bill;
import com.example.tad_bank_t1.data.model.remote.Flight;
import com.example.tad_bank_t1.data.model.remote.Movie;
import com.example.tad_bank_t1.data.model.remote.Provider;
import com.example.tad_bank_t1.data.remote.dto.ApiResponse;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentReq;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("providers")
    Call<List<Provider>> getProviders(@Query("type") String type);


    @GET("providers/topup/{phoneNumber}")
    Call<ApiResponse<Boolean>> checkTopup(@Path("phoneNumber") String phoneNumber);

    @GET("bills")
    Call<ApiResponse<Bill>> getBillByProviderAndCustomer(
            @Query("providerId") String providerId,
            @Query("customerCode") String customerCode
    );

//    @POST("bill/pay")
//    Call<ApiResponse<Bill>> payBill(
//            @Body PayBillRequest request
//    );

    @GET("tickets/flights")
    Call<ApiResponse<List<Flight>>> getFlights();

    @GET("tickets/movies")
    Call<ApiResponse<List<Movie>>> getMovies();


    // payment with vn pay
    @POST("payments/create_vnpay_url")
    Call<ApiResponse<CreatePaymentRes>> createPaymentVnpay(@Body CreatePaymentReq req);
}

