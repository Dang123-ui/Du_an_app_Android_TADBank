package com.example.tad_bank_t1.data.response;

public class ResultWrapper<T> {
    private T data;
    private String error;
    private boolean loading;

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isLoading() {
        return loading;
    }


    // static helper
    public static <T> ResultWrapper<T> loading() {
        ResultWrapper<T> r = new ResultWrapper<>();
        r.loading = true;
        return r;
    }

    public static <T> ResultWrapper<T> success(T data) {
        ResultWrapper<T> r = new ResultWrapper<>();
        r.data = data;
        return r;
    }

    public static <T> ResultWrapper<T> error(String msg) {
        ResultWrapper<T> r = new ResultWrapper<>();
        r.error = msg;
        return r;
    }
}
