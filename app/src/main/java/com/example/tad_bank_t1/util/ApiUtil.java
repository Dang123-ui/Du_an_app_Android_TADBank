package com.example.tad_bank_t1.util;

import com.example.tad_bank_t1.data.remote.dto.ApiResponse;
import com.google.gson.Gson;

import retrofit2.Response;

public class ApiUtil {
    public static String parseErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() == null) return "Unknown error";
            String json = response.errorBody().string(); // đọc 1 lần thôi
            Gson gson = new Gson();
            ApiResponse<?> err = gson.fromJson(json, ApiResponse.class);
            if (err != null && err.message != null) return err.message;
            return "Server error with code " + response.code();
        } catch (Exception e) {
            return "Server error: " + e.getMessage();
        }
    }
}
