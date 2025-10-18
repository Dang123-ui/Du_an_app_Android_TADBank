package com.example.tad_bank_t1.util;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;

public class TransactionUtil {
    public static boolean isIncoming(Transaction txn){
        if (txn.getType() == TxnType.RECEIVE_TRANSFER || txn.getType() == TxnType.INTEREST || txn.getType() == TxnType.REFUND || txn.getType() == TxnType.SALARY)
            return true;
        return false;
    }
}
