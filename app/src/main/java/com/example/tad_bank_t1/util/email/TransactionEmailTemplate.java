package com.example.tad_bank_t1.util.email;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;

public interface TransactionEmailTemplate {
    EmailContent build(User user, Account account, Transaction transaction);
}
