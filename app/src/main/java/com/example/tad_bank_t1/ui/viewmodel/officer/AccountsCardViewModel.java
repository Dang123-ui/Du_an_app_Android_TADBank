package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountsCardViewModel extends ViewModel {

    public enum FilterType { ALL, CHECKING, SAVING, MORTGAGE }

    public static class AccountItem {
        public final String id;
        public final String type;               // CHECKING / SAVING(S) / MORTGAGE (normalized)
        public final String accountNumber;
        public final String status;
        public final long amount;              // balance / currentPrincipal / outstandingPrincipal
        public final String currency;

        // Saving extras
        @Nullable public final String savingContractCode;
        @Nullable public final Integer savingTermMonths;
        @Nullable public final Double savingInterestRate;

        // Mortgage extras
        @Nullable public final Long mortgageOverdueAmount; // accruedInterest + accruedFees (tạm tính)
        @Nullable public final Date mortgageNextDueDate;

        public AccountItem(
                String id,
                String type,
                String accountNumber,
                String status,
                long amount,
                String currency,
                @Nullable String savingContractCode,
                @Nullable Integer savingTermMonths,
                @Nullable Double savingInterestRate,
                @Nullable Long mortgageOverdueAmount,
                @Nullable Date mortgageNextDueDate
        ) {
            this.id = id;
            this.type = type;
            this.accountNumber = accountNumber;
            this.status = status;
            this.amount = amount;
            this.currency = currency;

            this.savingContractCode = savingContractCode;
            this.savingTermMonths = savingTermMonths;
            this.savingInterestRate = savingInterestRate;

            this.mortgageOverdueAmount = mortgageOverdueAmount;
            this.mortgageNextDueDate = mortgageNextDueDate;
        }
    }

    public static class AccountsCardUiState {
        public final List<AccountItem> all;
        public final List<AccountItem> visible;

        public final int countChecking;
        public final int countSaving;
        public final int countMortgage;

        public final FilterType filter;

        public AccountsCardUiState(
                List<AccountItem> all,
                List<AccountItem> visible,
                int countChecking,
                int countSaving,
                int countMortgage,
                FilterType filter
        ) {
            this.all = all;
            this.visible = visible;
            this.countChecking = countChecking;
            this.countSaving = countSaving;
            this.countMortgage = countMortgage;
            this.filter = filter;
        }
    }

    private final AccountRepository accountRepo = new FirebaseAccountRepository();
    private ListenerRegistration reg;

    private final MutableLiveData<AccountsCardUiState> state = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private String currentUid;
    private FilterType currentFilter = FilterType.ALL;
    private List<AccountItem> cacheAll = new ArrayList<>();

    public LiveData<AccountsCardUiState> getState() { return state; }
    public LiveData<String> getError() { return error; }

    public void start(@NonNull String uid) {
        stop();
        currentUid = uid;
        error.setValue(null);

        reg = accountRepo.listenAccountsByUserId(uid, new AccountRepository.OnAccountsChanged() {
            @Override
            public void onChanged(List<Account> accounts) {
                if (!uid.equals(currentUid)) return;

                List<AccountItem> items = new ArrayList<>();
                int cChecking = 0, cSaving = 0, cMortgage = 0;

                if (accounts != null) {
                    for (Account acc : accounts) {
                        if (acc == null) continue;

                        String id = nz(acc.getAccountId());
                        String accNo = nz(acc.getAccountNumber());
                        String currency = nz(acc.getCurrency());
                        if (currency.isEmpty()) currency = "VND";

                        AccountType t = acc.getType();
                        String typeNorm = normalizeType(t);

                        String status = acc.getStatus() != null ? acc.getStatus().name() : "";

                        long amount = pickAmountByType(acc, typeNorm);

                        // Saving fields (đúng theo Account.java)
                        String savingContractCode = null;
                        Integer savingTermMonths = null;
                        Double savingInterestRate = null;

                        // Mortgage fields (đúng theo Account.java)
                        Long mortgageOverdueAmount = null;
                        Date mortgageNextDueDate = null;

                        if (isSaving(typeNorm)) {
//                            savingContractCode = acc.savingContractCode;
//                            savingTermMonths = acc.savingTermMonths;
//                            savingInterestRate = acc.savingInterestRate;
                        }

                        if (isMortgage(typeNorm)) {
//                            long accruedInterest = acc.mortgageAccruedInterest != null ? acc.mortgageAccruedInterest : 0L;
//                            long accruedFees = acc.mortgageAccruedFees != null ? acc.mortgageAccruedFees : 0L;
//                            mortgageOverdueAmount = accruedInterest + accruedFees;
//                            mortgageNextDueDate = acc.mortgageNextDueDate;
                        }

                        items.add(new AccountItem(
                                id,
                                typeNorm,
                                accNo,
                                status,
                                amount,
                                currency,
                                savingContractCode,
                                savingTermMonths,
                                savingInterestRate,
                                mortgageOverdueAmount,
                                mortgageNextDueDate
                        ));

                        if (isChecking(typeNorm)) cChecking++;
                        else if (isSaving(typeNorm)) cSaving++;
                        else if (isMortgage(typeNorm)) cMortgage++;
                    }
                }

                cacheAll = items;
                emit(cacheAll, cChecking, cSaving, cMortgage, currentFilter);
            }

            @Override
            public void onError(Exception e) {
                if (!uid.equals(currentUid)) return;
                error.setValue(e != null ? e.getMessage() : "Load accounts failed");
            }
        });
    }

    public void stop() {
        if (reg != null) {
            reg.remove();
            reg = null;
        }
    }

    @Override
    protected void onCleared() {
        stop();
    }

    public void setFilter(@Nullable FilterType filter) {
        currentFilter = filter == null ? FilterType.ALL : filter;

        int cChecking = 0, cSaving = 0, cMortgage = 0;
        for (AccountItem it : cacheAll) {
            if (isChecking(it.type)) cChecking++;
            else if (isSaving(it.type)) cSaving++;
            else if (isMortgage(it.type)) cMortgage++;
        }
        emit(cacheAll, cChecking, cSaving, cMortgage, currentFilter);
    }

    private void emit(
            List<AccountItem> all,
            int cChecking,
            int cSaving,
            int cMortgage,
            FilterType filter
    ) {
        List<AccountItem> visible = new ArrayList<>();
        for (AccountItem it : all) {
            if (filter == FilterType.ALL) visible.add(it);
            else if (filter == FilterType.CHECKING && isChecking(it.type)) visible.add(it);
            else if (filter == FilterType.SAVING && isSaving(it.type)) visible.add(it);
            else if (filter == FilterType.MORTGAGE && isMortgage(it.type)) visible.add(it);
        }

        state.setValue(new AccountsCardUiState(
                all,
                visible,
                cChecking,
                cSaving,
                cMortgage,
                filter
        ));
    }

    // ========= mapping helpers =========

    private static long pickAmountByType(Account acc, String typeNorm) {
        if (isChecking(typeNorm)) {
            return acc.balance;
        }
        if (isSaving(typeNorm)) {
//            if (acc.savingCurrentPrincipal != null) return acc.savingCurrentPrincipal;
//            if (acc.savingPrincipal != null) return acc.savingPrincipal;
//            return acc.balance;
        }
        if (isMortgage(typeNorm)) {
//            if (acc.mortgageOutstandingPrincipal != null) return acc.mortgageOutstandingPrincipal;
//            return acc.balance;
        }
        return acc.balance;
    }

    private static String normalizeType(AccountType t) {
        if (t == null) return "";
        String name = t.name().toUpperCase(Locale.ROOT);
        // hỗ trợ cả SAVING và SAVINGS
        if (name.equals("SAVING") || name.equals("SAVINGS")) return "SAVING";
        if (name.equals("CHECKING")) return "CHECKING";
        if (name.equals("MORTGAGE")) return "MORTGAGE";
        return name;
    }

    private static boolean isChecking(String typeNorm) {
        return "CHECKING".equalsIgnoreCase(nz(typeNorm));
    }

    private static boolean isSaving(String typeNorm) {
        String t = nz(typeNorm).toUpperCase(Locale.ROOT);
        return "SAVING".equals(t) || "SAVINGS".equals(t);
    }

    private static boolean isMortgage(String typeNorm) {
        return "MORTGAGE".equalsIgnoreCase(nz(typeNorm));
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    // ========= UI helpers (adapter dùng) =========

    public static String maskAccount(@NonNull String accNo) {
        String s = accNo.replace(" ", "");
        if (s.length() <= 4) return "****" + s;
        return "****" + s.substring(s.length() - 4);
    }

    public static String formatMoney(long amount, String currency) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String c = (currency == null || currency.trim().isEmpty()) ? "VND" : currency.trim();
        return nf.format(amount) + " " + c;
    }

    public static String formatDate(@Nullable Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")).format(d);
    }
}
