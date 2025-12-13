package com.example.tad_bank_t1.data.repository.callbacks;

public interface ResultCallback <T>{
    void onSucces(T data);
    void onError(String error);
}
