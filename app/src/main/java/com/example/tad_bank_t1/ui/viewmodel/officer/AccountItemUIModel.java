package com.example.tad_bank_t1.ui.viewmodel.officer;

import com.example.tad_bank_t1.data.model.Account;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class AccountItemUIModel {
    public final String userId;
    public final String userName;
    public final String phone;
    public final String email;
    public final List<Account> checkingAccounts;
    public final List<Account> savingsAccounts;
    public final List<Account> mortgageAccounts;
    public final int checkingCount;
    public final int savingsCount;
    public final int mortgageCount;
    public AccountItemUIModel(
            String userId, String userName, String phone, String email,
            @Nullable List<Account> checkingAccounts,
            @Nullable List<Account> savingsAccounts,
            @Nullable List<Account> mortgageAccounts
    ) {
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.email = email;

        this.checkingAccounts = checkingAccounts != null ? checkingAccounts : Collections.emptyList();
        this.savingsAccounts  = savingsAccounts  != null ? savingsAccounts  : Collections.emptyList();
        this.mortgageAccounts = mortgageAccounts != null ? mortgageAccounts : Collections.emptyList();

        this.checkingCount = this.checkingAccounts.size();
        this.savingsCount  = this.savingsAccounts.size();
        this.mortgageCount = this.mortgageAccounts.size();
    }
    public boolean hasChecking() { return checkingCount > 0; }
    public boolean hasSavings()  { return savingsCount > 0; }
    public boolean hasMortgage() { return mortgageCount > 0; }
}
