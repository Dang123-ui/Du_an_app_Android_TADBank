package com.example.tad_bank_t1.util;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.NotificationType;

import java.util.UUID;

public class NotificationUtil {
    public static String generateNotificationId() {
        String time = String.valueOf(System.currentTimeMillis());
        return "NOTI-" + time + "-" + UUID.randomUUID().toString();
    }

    // tạo thông báo khi giao dịch thành công, gửi email, thông báo qua app == 3 hàm riêng biệt
    public static Notification createNotificationTxn(User user, Account acc, Transaction transaction){
        StringBuilder msg = new StringBuilder();
        boolean isInComing = TransactionUtil.isIncoming(transaction); // nếu nhận tiền thì +, nếu gửi tiền thì -

        msg.append("Số dư TK " + acc.getAccountNumber() + ": "
                + (isInComing ? "+" : "-") + CurrencyUtil.formatVND(transaction.getAmount())
                + " lúc " + DateTimeUtil.formatDateToVNTime(transaction.getCreatedAt()) + "."
                + " Số dư hiện tại: " + CurrencyUtil.formatVND(acc.getBalance())
        );
        msg.append(
                ". Ref " + transaction.getTransactionReference()
                + " CT tu " + transaction.getAccountNumber() +  " " + transaction.getAccountName()
                + " toi " + transaction.getCounterpartyAccount()
        );

        if (transaction.getCounterpartyBankName() != null){
            msg.append(
                " " + transaction.getCounterpartyName()
            );
        }

        if (transaction.getCounterpartyBankCode() != null){
            msg.append(
               " tai " + transaction.getCounterpartyBankCode()
            );
        }

        Notification noti = new Notification.Builder()
                .notificationId(generateNotificationId())
                .userId(user.getUserId())
                .accountId(transaction.getAccountId())
                .title("Giao dịch thành công")
                .message(new String(msg))
                .type(NotificationType.TRANSACTION)
                .isRead(false)
                .relatedId(transaction.getTransactionId())
                .build();
        return noti;
    }

}
