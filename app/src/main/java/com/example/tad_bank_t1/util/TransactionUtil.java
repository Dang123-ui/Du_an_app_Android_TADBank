package com.example.tad_bank_t1.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TransactionUtil {
    // false nếu là giao dịch gửi tiền, true nếu là giao dịch nhận tiền
    public static boolean isIncoming(Transaction txn){
        if (txn.getType() == TxnType.TRANSFER_INTERNAL_INCOMING || txn.getType() == TxnType.INTEREST
                || txn.getType() == TxnType.SAVING_DEPOSIT
                || txn.getType() == TxnType.REFUND || txn.getType() == TxnType.SALARY)
            return true;
        return false;
    }

    @ColorInt
    public static int getColorTransactionStatus(@NonNull Context context, TnxStatus status) {

        int colorRes;
        switch (status) {
            case COMPLETED:
                colorRes = R.color.tnx_completed;
                break;
            case FAILED:
                colorRes = R.color.tnx_failed;
                break;
            case CANCELLED:
                colorRes = R.color.tnx_cancelled;
                break;
            case REQUIRES_2FA:
                colorRes = R.color.tnx_requires_2fa;
                break;
            case PENDING:
            default:
                colorRes = R.color.tnx_pending;
                break;
        }

        return ContextCompat.getColor(context, colorRes);
    }

    @ColorInt
    public static int text(Context c, TnxStatus status) {
        // đa số nền đậm => chữ trắng dễ đọc
        return ContextCompat.getColor(c, android.R.color.white);
    }

    public static String label(TnxStatus status) {
        switch (status) {
            case COMPLETED:    return "THÀNH CÔNG";
            case FAILED:       return "THẤT BẠI";
            case CANCELLED:    return "ĐÃ HUỶ";
            case REQUIRES_2FA: return "XÁC THỰC";
            case PENDING:
            default:           return "ĐANG CHỜ";
        }
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
