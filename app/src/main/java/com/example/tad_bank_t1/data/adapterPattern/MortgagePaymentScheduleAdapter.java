package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;

public class MortgagePaymentScheduleAdapter extends AbstractFirestoreAdapter<MortgagePaymentSchedule> {
    public MortgagePaymentScheduleAdapter() {super(FirestorePaths.MORTGAGE_PAYMENT_SCHEDULE);}
}
