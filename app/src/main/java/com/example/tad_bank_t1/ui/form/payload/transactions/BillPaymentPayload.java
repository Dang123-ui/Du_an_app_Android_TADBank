package com.example.tad_bank_t1.ui.form.payload.transactions;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;

public class BillPaymentPayload extends BaseTransactionPayload{
    public String billId;
    public String providerCode;
    public String customerCode;

    public BillPaymentPayload(){}

    public BillPaymentPayload(Account senderAccount, Transaction transaction, String billId, String providerCode, String customerCode) {
        super(senderAccount, transaction);
        this.billId = billId;
        this.providerCode = providerCode;
        this.customerCode = customerCode;
    }

    public BillPaymentPayload(String billId, String providerCode, String customerCode) {
        this.billId = billId;
        this.providerCode = providerCode;
        this.customerCode = customerCode;
    }

    @Override
    public boolean isValid(){
        return billId != null && transaction.getAmount() > 0 && customerCode != null;
    }
}
