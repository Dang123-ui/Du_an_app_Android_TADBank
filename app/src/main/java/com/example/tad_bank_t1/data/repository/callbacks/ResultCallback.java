package com.example.tad_bank_t1.data.repository.callbacks;

public interface ResultCallback <T>{
    void onSuccess(T data);
    void onError(String error);
    default void onLoading(){

    };
}
