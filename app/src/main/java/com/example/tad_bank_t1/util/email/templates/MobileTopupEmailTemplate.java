package com.example.tad_bank_t1.util.email.templates;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.util.email.EmailContent;
import com.example.tad_bank_t1.util.email.TransactionEmailHtml;
import com.example.tad_bank_t1.util.email.TransactionEmailTemplate;

public class MobileTopupEmailTemplate implements TransactionEmailTemplate {
    @Override
    public EmailContent build(User user, Account account, Transaction transaction) {
        String title = "Nạp thẻ thành công";
        String subtitle = "Giao dịch của bạn đã được xử lý thành công.";
        String statusText = "THÀNH CÔNG";
        String statusColor = "#16a34a";

        String amountText = TransactionEmailHtml.money(transaction.getAmount(), transaction.getCurrency());
        String amountSub = "Phí: " + TransactionEmailHtml.money(transaction.getFeeAmount(), transaction.getCurrency());


        String rows =
                TransactionEmailHtml.row("Thời gian", TransactionEmailHtml.dt(transaction.getCreatedAt())) +
                        TransactionEmailHtml.row("Từ tài khoản", TransactionEmailHtml.maskLast4(transaction.getAccountNumber()) + " • " + TransactionEmailHtml.safe(transaction.getAccountName())) +
                        TransactionEmailHtml.row("Đến tài khoản", TransactionEmailHtml.maskLast4(transaction.getCounterpartyAccount()) + " • " + TransactionEmailHtml.safe(transaction.getCounterpartyName())) +
                        TransactionEmailHtml.row("Nội dung", String.valueOf(transaction.getDescription())) +
                        TransactionEmailHtml.row("Reference", String.valueOf(transaction.getTransactionReference()));

        return TransactionEmailHtml.buildReceipt(title, subtitle, statusText, statusColor, amountText, amountSub, rows,
                "Lưu ý: thời gian nhận tiền có thể phụ thuộc sim nhận tiền.", transaction);
    }
}
