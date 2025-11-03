package com.example.tad_bank_t1.data.session;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;

import java.util.List;

public class SessionManager {
    private static User currentUser;
    private static List<Account> currentAccounts;
    private static Account accountDefault;

    public static void setUser(User user) { currentUser = user; }
    public static User getUser() { return currentUser; }
    public static Account getAccountDefault() { return accountDefault; }
    public static void setAccountDefault(Account account) { accountDefault = account; }

    public static void setAccounts(List<Account> accounts) { currentAccounts = accounts; }
    public static List<Account> getAccounts() { return currentAccounts; }

    public static void clear() {
        currentUser = null;
        currentAccounts = null;
    }
}
