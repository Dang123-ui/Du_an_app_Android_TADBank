package com.example.tad_bank_t1.data.repository.payment;

import androidx.lifecycle.LiveData;

import com.example.tad_bank_t1.data.remote.dto.ApiResponse;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentReq;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentRes;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.response.ResultWrapper;

public interface PaymentRepository {
     void createPaymentVnpay(CreatePaymentReq req, ResultCallback<CreatePaymentRes> callback);
}
