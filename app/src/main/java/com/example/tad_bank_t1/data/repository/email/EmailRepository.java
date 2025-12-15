package com.example.tad_bank_t1.data.repository.email;

import com.example.tad_bank_t1.BuildConfig;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.util.email.EmailContent;
import com.example.tad_bank_t1.util.email.SmtpEmailSender;
import com.example.tad_bank_t1.util.email.TransactionEmailTemplate;
import com.example.tad_bank_t1.util.email.TransactionEmailTemplateFactory;

public class EmailRepository {

    private final SmtpEmailSender sender = new SmtpEmailSender();

    public void sendTxnReceipt(User user, Account account, Transaction txn, SmtpEmailSender.Callback cb) {
        TransactionEmailTemplate tpl = TransactionEmailTemplateFactory.getEmailTemplate(txn.getType());
        EmailContent content = tpl.build(user, account, txn);

        sender.sendHtmlAsync(
                BuildConfig.SMTP_HOST,
                Integer.parseInt(BuildConfig.SMTP_PORT),
                BuildConfig.SMTP_USER,
                BuildConfig.SMTP_PASS,
                BuildConfig.SMTP_FROM_NAME,
                user.getEmail(),
                content.subject,
                content.html,
                cb
        );
    }
}
