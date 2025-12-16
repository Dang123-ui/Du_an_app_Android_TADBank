package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.OtpCode;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.otp.FirebaseOtpCodeRepository;
import com.example.tad_bank_t1.data.repository.otp.OtpCodeRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.example.tad_bank_t1.util.EmailSender;
import com.example.tad_bank_t1.util.OtpUtil;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OtpCodeViewModel extends ViewModel {
    private final OtpCodeRepository repository = new FirebaseOtpCodeRepository();

    private final MutableLiveData<ResultWrapper<OtpCode>> _createState = new MutableLiveData<>();
    public LiveData<ResultWrapper<OtpCode>> getCreateState() { return _createState; }

    private final MutableLiveData<ResultWrapper<Boolean>> _verifyState = new MutableLiveData<>();
    public LiveData<ResultWrapper<Boolean>> getVerifyState() { return _verifyState; }

    public void createOtpCode(String purpose, User user) {
        _createState.setValue(ResultWrapper.loading());
        OtpCode otpCode = OtpUtil.generateOtp(user.getUserId(), purpose);


        repository.create(otpCode)
                .addOnSuccessListener(id -> {
                    otpCode.setOtpId(id);
                    _createState.setValue(ResultWrapper.success(otpCode));

                    // GỬI EMAIL Ở BACKGROUND
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            EmailSender.sendEmail(user.getEmail(), otpCode.getCode());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    String msg = (e.getMessage() != null) ? e.getMessage() : "Unknown error";
                    _createState.setValue(ResultWrapper.error(msg));
                });

    }

    public void verifyOtpCode(String userId, String purpose, String code) {
        _verifyState.setValue(ResultWrapper.loading());

        repository.verifyAndConsume(userId, purpose, code)
                .addOnSuccessListener(verified -> {
                    if (verified) _verifyState.setValue(ResultWrapper.success(true));
                    else _verifyState.setValue(ResultWrapper.error("OTP verification failed"));
                })
                .addOnFailureListener(e -> {
                    String msg = (e.getMessage() != null) ? e.getMessage() : "Unknown error";
                    _verifyState.setValue(ResultWrapper.error(msg));
                });
    }
}

