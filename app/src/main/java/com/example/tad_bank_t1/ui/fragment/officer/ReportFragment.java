package com.example.tad_bank_t1.ui.fragment.officer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.transaction.FirebaseTransactionRepository;
import com.example.tad_bank_t1.data.repository.transaction.TransactionRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {
    private static final SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // ====== Firestore ======
    private FirebaseFirestore db;

    // ====== UI ======
    private MaterialAutoCompleteTextView actCheckingAccount;
    private TextInputLayout tilFromDate, tilToDate;
    private TextInputEditText edtFromDate, edtToDate;

    private ChipGroup chipGroupRange;
    private Chip chip7d, chip30d, chipAll;

    private MaterialButton btnExportExcel, btnExportPdf;
    private LinearProgressIndicator progressExport;
    private android.widget.TextView tvExportStatus;

    // ====== Data ======
    private final List<Account> checkingAccounts = new ArrayList<>();
    private ArrayAdapter<String> accountAdapter;
    private Account selectedChecking;

    private Date startDate;
    private Date endDate;

    // ====== CHANGE THESE if your collection names differ ======
    private static final String COL_ACCOUNTS = "accounts";
    private static final String COL_TRANSACTIONS = "transactions";

    // Transaction fields (must match Firestore)
    private static final String TX_FIELD_ACCOUNT_ID = "accountId";
    private static final String TX_FIELD_CREATED_AT = "createdAt";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        bindViews(view);
        setupDefaultRange30Days();
        bindCalendarPickers();
        bindChips();
        bindButtons();

        loadAllCheckingAccountsDirect();
    }

    private void bindViews(View root) {
        actCheckingAccount = root.findViewById(R.id.actCheckingAccount);

        tilFromDate = root.findViewById(R.id.tilFromDate);
        tilToDate   = root.findViewById(R.id.tilToDate);
        edtFromDate = root.findViewById(R.id.edtFromDate);
        edtToDate   = root.findViewById(R.id.edtToDate);

        chipGroupRange = root.findViewById(R.id.chipGroupRange);
        chip7d = root.findViewById(R.id.chip7d);
        chip30d = root.findViewById(R.id.chip30d);
        chipAll = root.findViewById(R.id.chipAll);

        btnExportExcel = root.findViewById(R.id.btnExportExcel);
        btnExportPdf   = root.findViewById(R.id.btnExportPdf);

        progressExport = root.findViewById(R.id.progressExport);
        tvExportStatus = root.findViewById(R.id.tvExportStatus);

        accountAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        actCheckingAccount.setAdapter(accountAdapter);

        actCheckingAccount.setOnItemClickListener((parent, v, position, id) -> {
            if (position >= 0 && position < checkingAccounts.size()) {
                selectedChecking = checkingAccounts.get(position);
            }
        });
    }

    private void setupDefaultRange30Days() {
        endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_YEAR, -30);
        startDate = cal.getTime();

        edtFromDate.setText(DF.format(startDate));
        edtToDate.setText(DF.format(endDate));
        chip30d.setChecked(true);
    }

    private interface DatePicked { void onPicked(Date d); }

    private void bindCalendarPickers() {
        View.OnClickListener pickFrom = v -> showDatePicker("Chọn ngày bắt đầu", d -> {
            startDate = d;
            edtFromDate.setText(DF.format(d));
            chipGroupRange.clearCheck();
        });

        View.OnClickListener pickTo = v -> showDatePicker("Chọn ngày kết thúc", d -> {
            endDate = d;
            edtToDate.setText(DF.format(d));
            chipGroupRange.clearCheck();
        });

        edtFromDate.setOnClickListener(pickFrom);
        edtToDate.setOnClickListener(pickTo);

        tilFromDate.setStartIconOnClickListener(pickFrom);
        tilFromDate.setEndIconOnClickListener(pickFrom);
        tilToDate.setStartIconOnClickListener(pickTo);
        tilToDate.setEndIconOnClickListener(pickTo);
    }

    private void showDatePicker(String title, DatePicked cb) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .build();
        picker.addOnPositiveButtonClickListener(selection -> cb.onPicked(new Date(selection)));
        picker.show(getParentFragmentManager(), "DATE_PICKER_" + title);
    }

    private void bindChips() {
        chip7d.setOnClickListener(v -> applyRangeDays(7));
        chip30d.setOnClickListener(v -> applyRangeDays(30));
        chipAll.setOnClickListener(v -> applyRangeAll());
    }

    private void applyRangeDays(int days) {
        endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_YEAR, -days);
        startDate = cal.getTime();

        edtFromDate.setText(DF.format(startDate));
        edtToDate.setText(DF.format(endDate));
    }

    private void applyRangeAll() {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDate = cal.getTime();
        endDate = new Date();

        edtFromDate.setText(DF.format(startDate));
        edtToDate.setText(DF.format(endDate));
    }

    private void bindButtons() {
        btnExportExcel.setOnClickListener(v -> exportDirect(true));
        btnExportPdf.setOnClickListener(v -> exportDirect(false));
    }

    /**
     * Load TẤT CẢ accounts từ Firestore rồi filter CHECKING ở client.
     * Nếu bạn muốn filter luôn trên server -> cần field string "type" = "CHECKING"
     * và query whereEqualTo("type","CHECKING") (mình ghi NOTE phía dưới).
     */
    private void loadAllCheckingAccountsDirect() {
        setLoading(true, "Đang tải danh sách checking accounts...");

        db.collection(COL_ACCOUNTS)
                .get()
                .addOnSuccessListener(qs -> {
                    checkingAccounts.clear();
                    accountAdapter.clear();

                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Account a = doc.toObject(Account.class);
                        if (a == null) continue;

                        // Nếu Account có field accountId nhưng không tự map, set theo docId
                        // (nếu model bạn có setter setAccountId)
                        try {
                            Account.class.getMethod("setAccountId", String.class).invoke(a, doc.getId());
                        } catch (Exception ignored) {}

                        if (isCheckingAccount(a)) {
                            checkingAccounts.add(a);
                            accountAdapter.add(buildAccountDisplay(a));
                        }
                    }

                    accountAdapter.notifyDataSetChanged();

                    if (checkingAccounts.isEmpty()) {
                        selectedChecking = null;
                        actCheckingAccount.setText("", false);
                        setLoading(false, "Không có checking account nào.");
                        Toast.makeText(requireContext(), "Không có checking account nào", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedChecking = checkingAccounts.get(0);
                    actCheckingAccount.setText(buildAccountDisplay(selectedChecking), false);

                    setLoading(false, "Sẵn sàng xuất sao kê.");
                })
                .addOnFailureListener(e -> {
                    setLoading(false, "Tải checking accounts lỗi: " + e.getMessage());
                    Toast.makeText(requireContext(), "Tải checking accounts lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean isCheckingAccount(Account a) {
        // Ưu tiên enum (a.getType())
        try {
            if (a.getType() != null) {
                return "CHECKING".equalsIgnoreCase(a.getType().name());
            }
        } catch (Exception ignored) {}

        // Fallback: nếu bạn lưu type dạng string (a.getTypeText() / a.getAccountType()...)
        // -> đổi phần này cho đúng getter của bạn.
        try {
            Object typeText = Account.class.getMethod("getAccountType").invoke(a);
            if (typeText != null) return "CHECKING".equalsIgnoreCase(String.valueOf(typeText));
        } catch (Exception ignored) {}

        return false;
    }

    private String buildAccountDisplay(Account a) {
        String num = safeGetAccountNumber(a);
        String masked = maskAccountNumber(num);

        String name = safeGetAccountName(a);
        if (TextUtils.isEmpty(name)) name = "Checking";

        String uid = safeGetUserId(a); // optional
        if (!TextUtils.isEmpty(uid)) {
            return masked + " - " + name + " (UID: " + uid + ")";
        }
        return masked + " - " + name;
    }

    private String safeGetAccountNumber(Account a) {
        try { return a.getAccountNumber(); } catch (Exception e) { return ""; }
    }

    private String safeGetAccountName(Account a) {
        try { return a.getAccountName(); } catch (Exception e) { return ""; }
    }

    private String safeGetUserId(Account a) {
        // nếu Account bạn có getUserId()
        try {
            Object v = Account.class.getMethod("getUserId").invoke(a);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception ignored) {}
        return "";
    }

    private String maskAccountNumber(String accNum) {
        if (accNum == null) return "****";
        String digits = accNum.replaceAll("\\s+", "");
        if (digits.length() <= 4) return "****" + digits;
        return "****" + digits.substring(digits.length() - 4);
    }

    private void exportDirect(boolean asExcel) {
        if (selectedChecking == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn tài khoản checking", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDate == null || endDate == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn khoảng thời gian", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endDate.before(startDate)) {
            Toast.makeText(requireContext(), "Đến ngày phải >= Từ ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        String accountId = safeGetAccountId(selectedChecking);
        String accountNumber = safeGetAccountNumber(selectedChecking);

        if (TextUtils.isEmpty(accountId)) {
            Toast.makeText(requireContext(), "Account thiếu accountId (không query được transactions)", Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true, "Đang tải giao dịch...");

        // Query theo accountId + createdAt range
        Query q = db.collection(COL_TRANSACTIONS)
                .whereEqualTo(TX_FIELD_ACCOUNT_ID, accountId)
                .whereGreaterThanOrEqualTo(TX_FIELD_CREATED_AT, new Timestamp(startDate))
                .whereLessThanOrEqualTo(TX_FIELD_CREATED_AT, new Timestamp(endDate))
                .orderBy(TX_FIELD_CREATED_AT, Query.Direction.ASCENDING);

        q.get().addOnSuccessListener(qs -> {
            List<Transaction> txns = new ArrayList<>();
            for (DocumentSnapshot doc : qs.getDocuments()) {
                Transaction t = doc.toObject(Transaction.class);
                if (t == null) continue;

                // nếu TransactionId trong model không map docId, set docId
                try {
                    Transaction.class.getMethod("setTransactionId", String.class).invoke(t, doc.getId());
                } catch (Exception ignored) {}

                txns.add(t);
            }

            try {
                setLoading(true, "Đang xuất " + (asExcel ? "Excel" : "PDF") + "...");

                // Không có uid -> pass "(ALL)" để hiện trong file name/header
                File out = asExcel
                        ? StatementExportUtil.exportCheckingExcelToCache(
                        requireContext(), "(ALL)", accountNumber, startDate, endDate, txns)
                        : StatementExportUtil.exportCheckingPdfToCache(
                        requireContext(), "(ALL)", accountNumber, startDate, endDate, txns);

                setLoading(false, "Xuất thành công: " + out.getName());
                shareFile(out, asExcel);

            } catch (Exception e) {
                setLoading(false, "Xuất lỗi: " + e.getMessage());
                Toast.makeText(requireContext(), "Xuất lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }).addOnFailureListener(e -> {
            setLoading(false, "Load giao dịch lỗi: " + e.getMessage());
            Toast.makeText(requireContext(), "Load giao dịch lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private String safeGetAccountId(Account a) {
        // ưu tiên getter getAccountId()
        try {
            String id = a.getAccountId();
            if (!TextUtils.isEmpty(id)) return id;
        } catch (Exception ignored) {}

        // fallback: nếu bạn lưu docId trong field khác
        try {
            Object v = Account.class.getMethod("getId").invoke(a);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception ignored) {}

        return "";
    }

    private void shareFile(File file, boolean isExcel) {
        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
        );

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(isExcel
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(share, "Chia sẻ sao kê"));
    }

    private void setLoading(boolean loading, String status) {
        if (progressExport != null) progressExport.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (tvExportStatus != null) tvExportStatus.setText(status == null ? "" : status);

        if (btnExportExcel != null) btnExportExcel.setEnabled(!loading);
        if (btnExportPdf != null) btnExportPdf.setEnabled(!loading);
        if (actCheckingAccount != null) actCheckingAccount.setEnabled(!loading);
        if (edtFromDate != null) edtFromDate.setEnabled(!loading);
        if (edtToDate != null) edtToDate.setEnabled(!loading);
    }
}