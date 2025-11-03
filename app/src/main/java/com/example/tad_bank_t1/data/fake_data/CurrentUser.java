package com.example.tad_bank_t1.data.fake_data;

import android.util.Log;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.session.SessionManager;

import java.util.List;

public class CurrentUser {
    private static FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
    private static FirebaseAccountRepository firebaseAccountRepository = new FirebaseAccountRepository();
    public static final String CURRENT_USER_ID = "u000001";
    public static void getCurrentUser(){
        firebaseUserRepository.getById(CURRENT_USER_ID)
                .addOnSuccessListener(user -> {
                    SessionManager.setUser(user);
                    Log.d("USER", user.toString());
                })
                .addOnFailureListener(e -> {
                    Log.d("USER", e.getMessage());
                });
    }

    public static void getAccountsOfCurrentUser(User u){
        firebaseAccountRepository.getAccountsByUserId(u.getUserId())
                .addOnSuccessListener(accounts -> {
                    Log.d("ACCOUNTS", accounts.toString());
                    SessionManager.setAccounts(accounts);
                })
                .addOnFailureListener(e -> {
                    Log.d("ACCOUNTS", e.getMessage());
                });
    }

    public static void getCurrentUserAndAccounts() {
        firebaseUserRepository.getById(CURRENT_USER_ID)
                .addOnSuccessListener(user -> {
                    if (user == null) {
                        Log.d("USER", "Không tìm thấy user!");
                        return;
                    }

                    SessionManager.setUser(user);
                    Log.d("USER", "User: " + user.toString());

                    // ✅ Sau khi đã có user, mới gọi tiếp để lấy danh sách account
                    firebaseAccountRepository.getAccountsByUserId(user.getUserId())
                            .addOnSuccessListener(accounts -> {
                                Log.d("ACCOUNTS", "Số tài khoản: " + accounts.size());
                                for (Account a : accounts) {
                                    Log.d("ACCOUNT", a.getAccountName());
                                    if (a.isDefault()) SessionManager.setAccountDefault(a);
                                }
                                SessionManager.setAccounts(accounts);
                            })
                            .addOnFailureListener(e -> Log.e("ACCOUNTS", e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("USER", e.getMessage()));
    }

}
