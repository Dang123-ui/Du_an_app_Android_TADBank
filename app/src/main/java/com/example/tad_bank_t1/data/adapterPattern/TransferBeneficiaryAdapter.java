package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.TransferBeneficiary;

public class TransferBeneficiaryAdapter extends AbstractFirestoreAdapter<TransferBeneficiary> {
    public TransferBeneficiaryAdapter() {super(FirestorePaths.TRANSFER_BENEFICIARIES);}
}
