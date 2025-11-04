package com.example.tad_bank_t1.data.fake_data;

import android.util.Log;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.session.SessionManager;

import java.util.List;

public class CurrentUser {
    private static final FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
    private static final FirebaseAccountRepository firebaseAccountRepository = new FirebaseAccountRepository();
    public static final String CURRENT_USER_ID = "u000001";

    // 🔹 Interface callback
    public interface OnUserAndAccountsLoadedListener {
        void onLoaded(User user, List<Account> accounts);
        void onError(Exception e);
    }

    // ✅ Hàm dùng callback
    public static void getCurrentUserAndAccounts(OnUserAndAccountsLoadedListener listener) {
        firebaseUserRepository.getById(CURRENT_USER_ID)
                .addOnSuccessListener(user -> {
                    if (user == null) {
                        Log.d("USER", "Không tìm thấy user!");
                        listener.onError(new Exception("User not found"));
                        return;
                    }

                    SessionManager.setUser(user);
                    Log.d("USER", "User: " + user);

                    // Sau khi có user, gọi tiếp account
                    firebaseAccountRepository.getAccountsByUserId(user.getUserId())
                            .addOnSuccessListener(accounts -> {
                                Log.d("ACCOUNTS", "Số tài khoản: " + accounts.size());
                                SessionManager.setAccounts(accounts);

                                for (Account a : accounts) {
                                    Log.d("ACCOUNT", a.getAccountName());
                                    if (a.getIsDefault()) {
                                        SessionManager.setDefaultAccount(a);
                                        Log.d("ACCOUNT", "Tài khoản mặc định: " + a.getAccountName());
                                    }
                                }

                                // 🔹 Gọi callback
                                listener.onLoaded(user, accounts);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ACCOUNTS", e.getMessage());
                                listener.onError(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("USER", e.getMessage());
                    listener.onError(e);
                });
    }
}
