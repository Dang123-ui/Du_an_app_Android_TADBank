package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountListViewModel extends ViewModel {

    public enum FilterKey { TODAY, D7, D30, ALL }

    private final UserRepository userRepo = new FirebaseUserRepository();
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    private final MutableLiveData<List<AccountItemUIModel>> accounts = new MutableLiveData<>();
    public List<AccountItemUIModel> master = new ArrayList<>();

    private FilterKey currentFilter = FilterKey.ALL;
    private String currentKeyword = "";


    public LiveData<List<AccountItemUIModel>> getAccounts() {
        return accounts;
    }

    /** Mode cũ: load danh sách tổng (nhiều user) */
    public void loadAccount() {

        // ========== MODE 1: danh sách tổng (như bạn đang có) ==========
        userRepo.getCustomerActive().addOnSuccessListener(users -> {
            if (users == null || users.isEmpty()) {
                master = Collections.emptyList();
                accounts.setValue(master);
                return;
            }

            List<Task<List<Account>>> tasks = new ArrayList<>();
            for (User u : users) {
                String uid = u.getUserId();
                tasks.add(accountRepo.getAccountsByUserId(uid));
            }

            Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                List<AccountItemUIModel> ui = new ArrayList<>();

                for (int i = 0; i < users.size(); i++) {
                    User u = users.get(i);
                    String uid = u.getUserId();

                    @SuppressWarnings("unchecked")
                    List<Account> accs = (List<Account>) results.get(i);

                    ui.add(buildUiModel(u, uid, accs));
                }

                master = ui;
                applySearchAndFilter();

            }).addOnFailureListener(e -> {
                master = Collections.emptyList();
                accounts.setValue(master);
            });

        }).addOnFailureListener(e -> {
            master = Collections.emptyList();
            accounts.setValue(master);
        });
    }


    public void applySearch(String keyword) {
        currentKeyword = keyword == null ? "" : keyword.trim();
        applySearchAndFilter();
    }

    public void applyFilter(FilterKey filter) {
        currentFilter = (filter == null) ? FilterKey.ALL : filter; // ✅ fix default
        applySearchAndFilter();
    }

    private void applySearchAndFilter() {
        List<AccountItemUIModel> src = master == null ? Collections.emptyList() : master;

        // 1) filter theo thời gian (TODAY/7D/30D)
        List<AccountItemUIModel> filteredByTime = new ArrayList<>();
        Date start = getStartDateForFilter(currentFilter);

        for (AccountItemUIModel item : src) {
            if (start == null) {
                filteredByTime.add(item);
            } else {
                if (itemHasAnyAccountInRange(item, start)) {
                    filteredByTime.add(item);
                }
            }
        }

        // 2) search keyword (user + accountName + accountNumber)
        String k = normalize(currentKeyword);
        if (k.isEmpty()) {
            accounts.setValue(filteredByTime);
            return;
        }

        List<AccountItemUIModel> out = new ArrayList<>();
        for (AccountItemUIModel item : filteredByTime) {
            StringBuilder hay = new StringBuilder();
            hay.append(normalize(item.userName)).append(" ")
                    .append(normalize(item.phone)).append(" ")
                    .append(normalize(item.email));
            if (hay.toString().contains(k)) out.add(item);
        }

        accounts.setValue(out);
    }

    private @Nullable Date getStartDateForFilter(FilterKey key) {
        if (key == FilterKey.ALL) return null;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (key == FilterKey.TODAY) return cal.getTime();
        if (key == FilterKey.D7) { cal.add(Calendar.DAY_OF_YEAR, -7); return cal.getTime(); }
        if (key == FilterKey.D30) { cal.add(Calendar.DAY_OF_YEAR, -30); return cal.getTime(); }

        return null;
    }

    private boolean itemHasAnyAccountInRange(AccountItemUIModel item, Date start) {
        return listHasAnyAccountInRange(item.checkingAccounts, start)
                || listHasAnyAccountInRange(item.savingsAccounts, start)
                || listHasAnyAccountInRange(item.mortgageAccounts, start);
    }

    private boolean listHasAnyAccountInRange(List<Account> list, Date start) {
        if (list == null || list.isEmpty()) return false;
        for (Account acc : list) {
            if (acc == null) continue;
            Date createdAt = acc.getCreatedAt();
            if (createdAt != null && !createdAt.before(start)) return true;
        }
        return false;
    }

    private String normalize(String s) {
        return (s == null ? "" : s).toLowerCase(Locale.ROOT).trim();
    }

    private AccountItemUIModel keepOnlyChecking(AccountItemUIModel src) {
        if (src == null) return null;
        return new AccountItemUIModel(
                src.userId,
                src.userName,
                src.phone,
                src.email,
                src.checkingAccounts == null ? Collections.emptyList() : src.checkingAccounts,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    // ===== build UI model theo đúng code bạn đang có =====
    private AccountItemUIModel buildUiModel(User user, String userId, List<Account> accounts) {
        String name = user.getFullName();
        String phone = user.getPhone();
        String email = user.getEmail();

        List<Account> checkingList = new ArrayList<>();
        List<Account> savingsList = new ArrayList<>();
        List<Account> mortgageList = new ArrayList<>();

        if (accounts != null) {
            for (Account acc : accounts) {
                if (acc == null || acc.getType() == null) continue;

                String type = safeUpper(acc.getType().name());
                if ("CHECKING".equals(type)) {
                    checkingList.add(acc);
                } else if ("SAVING".equals(type)) {
                    savingsList.add(acc);
                } else if ("MORTGAGE".equals(type)) {
                    mortgageList.add(acc);
                }
            }
        }

        return new AccountItemUIModel(
                userId, name, phone, email,
                checkingList, savingsList, mortgageList
        );
    }

    private String safeUpper(String s) {
        return s == null ? "" : s.toUpperCase(Locale.ROOT);
    }

    // các hàm format bạn giữ lại (không ảnh hưởng)
    private String maskAccountNumber(String accNum) {
        if (accNum == null) return "****";
        String digits = accNum.replaceAll("\\s+", "");
        if (digits.length() <= 4) return "****" + digits;
        return "****" + digits.substring(digits.length() - 4);
    }

    private String formatVnd(double v) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(v) + " VND";
    }
}
