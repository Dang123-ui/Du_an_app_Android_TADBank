package com.example.tad_bank_t1.util.email.templates;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.util.email.EmailContent;
import com.example.tad_bank_t1.util.email.TransactionEmailHtml;
import com.example.tad_bank_t1.util.email.TransactionEmailTemplate;

public class TransferInternalEmailTemplate implements TransactionEmailTemplate {
    @Override
    public EmailContent build(User user, Account sender, Transaction txn) {
        String title = "Chuyển khoản nội bộ thành công";
        String subtitle = "Giao dịch của bạn đã được xử lý thành công.";
        String statusText = "THÀNH CÔNG";
        String statusColor = "#16a34a";

        String amountText = TransactionEmailHtml.money(txn.getAmount(), txn.getCurrency());
        String amountSub = "Phí: " + TransactionEmailHtml.money(txn.getFeeAmount(), txn.getCurrency());

        String rows =
                TransactionEmailHtml.row("Thời gian", TransactionEmailHtml.dt(txn.getCreatedAt())) +
                        TransactionEmailHtml.row("Từ tài khoản", TransactionEmailHtml.maskLast4(txn.getAccountNumber()) + " • " + TransactionEmailHtml.safe(txn.getAccountName())) +
                        TransactionEmailHtml.row("Đến tài khoản", TransactionEmailHtml.maskLast4(txn.getCounterpartyAccount()) + " • " + TransactionEmailHtml.safe(txn.getCounterpartyName())) +
                        TransactionEmailHtml.row("Kênh", String.valueOf(txn.getChannel())) +
                        TransactionEmailHtml.row("Nội dung", String.valueOf(txn.getDescription())) +
                        TransactionEmailHtml.row("Reference", String.valueOf(txn.getTransactionReference()));

        String footer = "Cảm ơn bạn đã sử dụng TAD Bank.";

        return TransactionEmailHtml.buildReceipt(title, subtitle, statusText, statusColor, amountText, amountSub, rows, footer, txn);
    }
}

