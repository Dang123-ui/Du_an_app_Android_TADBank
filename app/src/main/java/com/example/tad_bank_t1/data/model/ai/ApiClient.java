package com.example.tad_bank_t1.data.model.ai;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Nếu chạy điện thoại thật: phải đổi BASE_URL sang IP máy tính (vd http://192.168.1.5:8000)
    private static final String BASE_URL = "http://10.0.2.2:8000";
    private static Retrofit retrofit;
    public static AiMarketApi api(){
        if (retrofit == null) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(log)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AiMarketApi.class);
    }
}
