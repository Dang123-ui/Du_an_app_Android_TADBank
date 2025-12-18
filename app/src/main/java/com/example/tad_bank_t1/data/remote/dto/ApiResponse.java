package com.example.tad_bank_t1.data.remote.dto;

public class ApiResponse<T>{
    public boolean success;
    public String message;
    public T data;
}
