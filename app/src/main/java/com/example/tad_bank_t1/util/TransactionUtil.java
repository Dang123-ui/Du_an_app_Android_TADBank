package com.example.tad_bank_t1.util;

import android.util.Log;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TransactionUtil {
    // false nếu là giao dịch gửi tiền, true nếu là giao dịch nhận tiền
    public static boolean isIncoming(Transaction txn){
        if (txn.getType() == TxnType.TRANSFER_RECEIVE || txn.getType() == TxnType.INTEREST
                || txn.getType() == TxnType.SAVING_DEPOSIT
                || txn.getType() == TxnType.REFUND || txn.getType() == TxnType.SALARY)
            return true;
        return false;
    }

    public static String generateIdWithTime(){
        String time = String.valueOf(System.currentTimeMillis());
        String randomPart = UUID.randomUUID().toString().toUpperCase();
        return time + "-" + randomPart;
    }

    public static String generateTransactionId() {
        String time = String.valueOf(System.currentTimeMillis());
        String randomPart = UUID.randomUUID().toString().toUpperCase();
        return "TAD-" + time + "-" + randomPart;
    }

    public static String generateIdempotencyKey(Long amount, String sourceAccNumber, String targetAccNumber, TxnType type){
        return amount + "-" + sourceAccNumber + "-" + targetAccNumber + "-" + type + "-" + System.currentTimeMillis();
    }

    public static String generateRef() {
        LocalDateTime now = LocalDateTime.now(); // Get current date and time

        // Define a custom format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Format the date
        String formattedDate = now.format(formatter);

        Log.d("TAG", "generateRef: " + formattedDate + "-" + UUID.randomUUID().toString().substring(0, 3).toUpperCase() + "");

        return formattedDate + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }


}
