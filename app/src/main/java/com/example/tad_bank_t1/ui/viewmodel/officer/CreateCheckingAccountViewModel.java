package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;

import java.util.Date;

public class CreateCheckingAccountViewModel extends ViewModel {

    public static class UiState {
        public final boolean loading;

        // Errors hiển thị bằng TIL
        public final String accountNameError;
        public final String accountNumberError;
        public final String balanceError;

        // Toast-only
        public final String toastMessage;

        public final String createdAccountId;

        public UiState(boolean loading,
                       String accountNameError,
                       String accountNumberError,
                       String balanceError,
                       String toastMessage,
                       String createdAccountId) {
            this.loading = loading;
            this.accountNameError = accountNameError;
            this.accountNumberError = accountNumberError;
            this.balanceError = balanceError;
            this.toastMessage = toastMessage;
            this.createdAccountId = createdAccountId;
        }

        public static UiState idle() {
            return new UiState(false, null, null, null, null, null);
        }

        public UiState withLoading(boolean v) {
            return new UiState(v, accountNameError, accountNumberError, balanceError, toastMessage, createdAccountId);
        }
    }

    private final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.idle());
    private final AccountRepository accountRepository = new FirebaseAccountRepository();

    public LiveData<UiState> getUiState() {
        return uiState;
    }

    public void clearCreatedResult() {
        UiState cur = uiState.getValue();
        if (cur == null) cur = UiState.idle();
        uiState.setValue(new UiState(cur.loading, cur.accountNameError, cur.accountNumberError, cur.balanceError, cur.toastMessage, null));
    }

    public void clearToast() {
        UiState cur = uiState.getValue();
        if (cur == null) cur = UiState.idle();
        uiState.setValue(new UiState(cur.loading, cur.accountNameError, cur.accountNumberError, cur.balanceError, null, cur.createdAccountId));
    }

    public void submitCreateChecking(
            String uid,
            String accountName,
            String accountNumber,
            String balanceStr
    ) {
        // ====== UID missing => TOAST ======
        if (isBlank(uid)) {
            uiState.setValue(new UiState(false, null, null, null, "UID is missing", null));
            return;
        }

        // ====== Local validate => TIL ======
        String nameErr = null, numErr = null, balErr = null;

        if (isBlank(accountName)) nameErr = "Account name is required";

        if (isBlank(accountNumber)) {
            numErr = "Account number is required";
        } else if (!isValidAccountNumber(accountNumber)) {
            numErr = "Account number must be 12-14 digits (numbers only)";
        }

        long balance = 0L;
        if (!isBlank(balanceStr)) {
            try {
                balance = Long.parseLong(balanceStr.trim());
                if (balance < 0) balErr = "Balance must be >= 0";
            } catch (Exception e) {
                balErr = "Balance is invalid";
            }
        }

        if (nameErr != null || numErr != null || balErr != null) {
            uiState.setValue(new UiState(false, nameErr, numErr, balErr, null, null));
            return;
        }

        uiState.setValue(UiState.idle().withLoading(true));

        final long finalBalance = balance;

        // ====== CHECK TRÙNG accountNumber ======
        // (đây là chỗ check duplicate)
        accountRepository.isAccountNumberAvailable(accountNumber.trim())
                .continueWithTask(t -> {
                    if (!t.isSuccessful()) throw t.getException();

                    Boolean available = t.getResult();
                    if (available == null || !available) {
                        // duplicate => TIL (accountNumber)
                        throw new IllegalStateException("DUPLICATE_ACCOUNT_NUMBER");
                    }

                    Account acc = new Account();
                    acc.userId = uid.trim();
                    acc.accountName = accountName.trim();
                    acc.accountNumber = accountNumber.trim();

                    // defaults theo yêu cầu
                    acc.branchId = "br001";
                    acc.currency = "VND";

                    acc.type = AccountType.CHECKING;
                    acc.balance = finalBalance;
                    acc.status = AccountStatus.OPEN;
                    acc.createdAt = new Date();

                    // pin sẽ set ở màn PIN
                    acc.pinCode = null;

                    return accountRepository.create(acc);
                })
                .addOnSuccessListener(accountId -> {
                    uiState.setValue(new UiState(false, null, null, null, null, accountId));
                })
                .addOnFailureListener(e -> {
                    String msg = (e.getMessage() == null) ? "Create account failed" : e.getMessage();

                    if ("DUPLICATE_ACCOUNT_NUMBER".equals(msg)) {
                        uiState.setValue(new UiState(false, null, "Account number already exists", null, null, null));
                    } else {
                        // Firebase/network => TOAST
                        uiState.setValue(new UiState(false, null, null, null, msg, null));
                    }
                });
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidAccountNumber(String s) {
        if (s == null) return false;
        String x = s.trim();
        if (x.length() < 12 || x.length() > 14) return false;
        for (int i = 0; i < x.length(); i++) {
            if (!Character.isDigit(x.charAt(i))) return false;
        }
        return true;
    }
}
