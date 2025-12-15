package com.example.tad_bank_t1.util.email.templates;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.util.email.EmailContent;
import com.example.tad_bank_t1.util.email.TransactionEmailHtml;
import com.example.tad_bank_t1.util.email.TransactionEmailTemplate;

public class TransferExternalEmailTemplate implements TransactionEmailTemplate {
    @Override
    public EmailContent build(User user, Account sender, Transaction txn) {
        String title = "Chuyển khoản liên ngân hàng thành công";
        String subtitle = "Tiền đã được ghi nhận để chuyển đến ngân hàng thụ hưởng.";
        String statusText = "THÀNH CÔNG";
        String statusColor = "#16a34a";

        String amountText = TransactionEmailHtml.money(txn.getAmount(), txn.getCurrency());
        String amountSub = "Phí: " + TransactionEmailHtml.money(txn.getFeeAmount(), txn.getCurrency());

        String bankLine = (txn.getCounterpartyBankName() != null && !txn.getCounterpartyBankName().isEmpty())
                ? txn.getCounterpartyBankName() + " (" + TransactionEmailHtml.safe(txn.getCounterpartyBankCode()) + ")"
                : TransactionEmailHtml.safe(txn.getCounterpartyBankCode());

        String rows =
                TransactionEmailHtml.row("Thời gian", TransactionEmailHtml.dt(txn.getCreatedAt())) +
                        TransactionEmailHtml.row("Từ tài khoản", TransactionEmailHtml.maskLast4(txn.getAccountNumber()) + " • " + TransactionEmailHtml.safe(txn.getAccountName())) +
                        TransactionEmailHtml.row("Ngân hàng thụ hưởng", bankLine) +
                        TransactionEmailHtml.row("Đến tài khoản", TransactionEmailHtml.maskLast4(txn.getCounterpartyAccount()) + " • " + TransactionEmailHtml.safe(txn.getCounterpartyName())) +
                        TransactionEmailHtml.row("Nội dung", String.valueOf(txn.getDescription())) +
                        TransactionEmailHtml.row("Reference", String.valueOf(txn.getTransactionReference()));

        return TransactionEmailHtml.buildReceipt(title, subtitle, statusText, statusColor, amountText, amountSub, rows,
                "Lưu ý: thời gian nhận tiền có thể phụ thuộc vào hệ thống liên ngân hàng.", txn);
    }
}

