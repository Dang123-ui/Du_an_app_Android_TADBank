package com.example.tad_bank_t1.data.repository.mortgagePaymentSchedule;

import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;

import java.util.List;

public interface MortgagePaymentScheduleRepository {
    // create
    void create(MortgagePaymentSchedule mortgagePaymentSchedule, ResultCallback<MortgagePaymentSchedule> callback);

    // get all
    void getAll(ResultCallback<List<MortgagePaymentSchedule>> callback);

    // get by id
    void getById(String mortgagePaymentScheduleId, ResultCallback<MortgagePaymentSchedule> callback);

    // update
    void update(MortgagePaymentSchedule mortgagePaymentSchedule, ResultCallback<MortgagePaymentSchedule> callback);

//    // delete
//    void delete(int id);
}
