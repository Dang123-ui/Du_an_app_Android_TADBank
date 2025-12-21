package com.example.tad_bank_t1.ui.fragment.customer.mortgage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.MortgageAccount;
import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.model.enums.mortgage.MortgagePaymentFrequency;
import com.example.tad_bank_t1.data.model.enums.mortgage.MortgageInstallmentStatus;
import com.example.tad_bank_t1.databinding.FragmentMortgageAccountDetailBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewadapter.MortgageScheduleAdapter;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.MortgageScheduleViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionConfirmFragment;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.MortgageCalculator;
import com.example.tad_bank_t1.util.TransactionUtil;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;

import java.util.List;
/**
 * Detail fragment for displaying mortgage account information.  This class
 * follows the same structure as the saving account detail fragment: it
 * retrieves an account by id via the AccountViewModel, validates that
 * it is of type MORTGAGE, then populates the UI with mortgage-specific
 * information.  A simple pay button is provided for demonstration; you
 * can wire this up to your payment logic or navigate to a payment
 * screen as required.
 */
public class MortgageAccountDetailFragment extends Fragment implements UiConfig, BaseCustomFragment {
    private static final String ACCOUNT_ID = "accountId";

    private String accountId;
    private Account currentAccount;

    private FragmentMortgageAccountDetailBinding binding;

    // ViewModels
    private AccountViewModel accountViewModel;

    /** ViewModel lấy danh sách schedule theo account */
    private MortgageScheduleViewModel scheduleViewModel;
    /** ViewModel chứa payload giao dịch để truyền giữa màn hình */
    private TransactionPayloadViewModel transactionPayloadViewModel;
    /** ViewModel xử lý giao dịch: tạo idempotencyKey, ... */
    private TransactionViewModel transactionViewModel;

    /** Adapter hiển thị danh sách các kỳ thanh toán của khoản vay */
    private MortgageScheduleAdapter scheduleAdapter;

    /** Biến cờ đánh dấu khi đang chọn tài khoản nguồn để thanh toán */
    private boolean isSelectingSourceAccount = false;

    /** Kỳ thanh toán đang được chọn để thanh toán */
    private MortgagePaymentSchedule pendingSchedule;

    public MortgageAccountDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the
     * provided account id.
     *
     * @param accountId the id of the account to display
     * @return A new instance of fragment MortgageAccountDetailFragment
     */
    public static MortgageAccountDetailFragment newInstance(String accountId) {
        MortgageAccountDetailFragment fragment = new MortgageAccountDetailFragment();
        Bundle args = new Bundle();
        args.putString(ACCOUNT_ID, accountId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountId = getArguments().getString(ACCOUNT_ID);
        }
    }

    @Override
    public @NonNull View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                      @Nullable Bundle savedInstanceState) {
        binding = FragmentMortgageAccountDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment();
    }

    @Override
    public void initView() {
        if (currentAccount != null) {
            renderAccount(currentAccount);
            // Khi đã có account, thiết lập RecyclerView cho schedule
            setupScheduleRecyclerView();
        }
    }

    @Override
    public void initViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(MortgageScheduleViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        if (accountId == null) {
            showError(requireContext(), "Error", "Account id not found");
            return;
        }
        // Request the account by id
        accountViewModel.getAccountById(accountId);

        // Observe account state
        accountViewModel.getAccountState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isLoading()) {
                toggleLoading(true);
                return;
            }
            toggleLoading(false);
            if (result.getData() != null) {
                Account account = result.getData();
                if (account.getType() == AccountType.MORTGAGE) {
                    currentAccount = account;
                    renderAccount(account);
                    // Khi có account, load danh sách schedule tương ứng
                    loadSchedulesForAccount(account.getAccountId());
                } else {
                    showError(requireContext(), "Error", "Not a mortgage account");
                }
            }
            if (result.getError() != null) {
                showError(requireContext(), "Error", result.getError());
            }
        });

        // Observe list of accounts (used when lựa chọn tài khoản nguồn thanh toán)
        accountViewModel.getListState().observe(getViewLifecycleOwner(), result -> {
            if (!isSelectingSourceAccount) return;
            if (result == null) return;
            if (result.isLoading()) {
                toggleLoading(true);
                return;
            }
            toggleLoading(false);
            if (result.getData() != null && result.getData().size() > 0) {
                // Hiển thị dialog chọn tài khoản nguồn
                showSelectSourceAccountDialog(result.getData());
            } else if (result.getError() != null) {
                showError(requireContext(), "Error", result.getError());
            } else {
                showError(requireContext(), "Thông báo", "Không tìm thấy tài khoản thanh toán khả dụng");
            }
            // reset flag để không xử lý lại danh sách ngoài lúc chọn
            isSelectingSourceAccount = false;
        });

        // Observe schedule list state
        scheduleViewModel.getListState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isLoading()) {
                toggleLoading(true);
                return;
            }
            toggleLoading(false);
            if (result.getData() != null) {
                // Lọc danh sách theo account hiện tại
                List<MortgagePaymentSchedule> all = result.getData();
                List<MortgagePaymentSchedule> filtered = new java.util.ArrayList<>();
                if (currentAccount != null) {
                    String accId = currentAccount.getAccountId();
                    for (MortgagePaymentSchedule s : all) {
                        if (accId != null && accId.equals(String.valueOf(s.accountId))) {
                            filtered.add(s);
                        }
                    }
                    // sắp xếp theo ngày đến hạn tăng dần
                    filtered.sort((a, b) -> {
                        return a.getDueDate().compareTo(b.getDueDate());
                    });

                    if (scheduleAdapter == null){
                        setupScheduleRecyclerView();
                    }
                    scheduleAdapter.setSchedules(filtered);
                }
            }
            if (result.getError() != null) {
                showError(requireContext(), "Error", result.getError());
            }
        });
    }

    @Override
    public void setUpEvents() {
        // Click nút thanh toán kỳ. Khi bấm sẽ chọn tài khoản nguồn (checking)
        binding.btnMortgageDetailPay.setOnClickListener(v -> {
            onPayInstallment();
        });
    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    @Override
    public String getAppBarTitle() {
        // You can provide a string resource here if one exists
        return "Mortgage account details";
    }

    /**
     * Binds account data to the UI elements.  Includes computation of
     * periodic payment amounts and total periods using the
     * MortgageCalculator.
     *
     * @param account the account to render
     */
    private void renderAccount(Account account) {
        // Basic account fields
        binding.txtMortgageDetailAccountNumber.setText(account.getAccountNumber());
        binding.txtMortgageDetailStatus.setText(account.getStatus() == null ? "" : account.getStatus().toString());
        // Mortgage-specific fields
        MortgageAccount mortgage = account.getMortgage();
        if (mortgage == null) return;
        // Principal amount
        binding.txtMortgageDetailPrincipalAmount.setText(CurrencyUtil.formatVND(mortgage.getPrincipalAmount()));
        // Interest rate
        binding.txtMortgageDetailInterestRate.setText(String.valueOf(mortgage.getInterestRateAnnual()));
        // Term
        binding.txtMortgageDetailTerm.setText(mortgage.getTermMonths() + " months");
        // Payment frequency
        binding.txtMortgageDetailPaymentFrequency.setText(
                mortgage.getPaymentFrequency() != null ? mortgage.getPaymentFrequency().name() : "");
        // Start and next due dates
        if (mortgage.getStartDate() != null) {
            binding.txtMortgageDetailStartDate.setText(DateTimeUtil.formatDateToVNDate(mortgage.getStartDate()));
        }
        if (mortgage.getNextDueDate() != null) {
            binding.txtMortgageDetailNextDueDate.setText(DateTimeUtil.formatDateToVNDate(mortgage.getNextDueDate()));
        }

        // Calculate payment details on the fly
        long amountDue = MortgageCalculator.calculateAmountDuePerPeriod(
                mortgage.getPrincipalAmount(),
                mortgage.getInterestRateAnnual(),
                mortgage.getTermMonths(),
                mortgage.getPaymentFrequency() != null ? mortgage.getPaymentFrequency() : MortgagePaymentFrequency.MONTHLY
        );
        binding.txtMortgageDetailAmountDue.setText(CurrencyUtil.formatVND(amountDue));

        int totalPeriods = MortgageCalculator.calculateTotalPeriods(
                mortgage.getTermMonths(),
                mortgage.getPaymentFrequency() != null ? mortgage.getPaymentFrequency() : MortgagePaymentFrequency.MONTHLY
        );
        binding.txtMortgageDetailTotalPeriods.setText(String.valueOf(totalPeriods));

        // Paid periods and outstanding principal are not stored in this demo
        // If you extend MortgageAccount to include these, populate them here
        binding.txtMortgageDetailPaidPeriods.setText("0");
        binding.txtMortgageDetailOutstanding.setText(CurrencyUtil.formatVND(mortgage.getPrincipalAmount()));

        // Sau khi render, đảm bảo lịch trả nợ được hiển thị (nếu đã có)
        if (scheduleAdapter != null) {
            scheduleAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Thiết lập RecyclerView hiển thị lịch trả nợ.
     */
    private void setupScheduleRecyclerView() {
        if (scheduleAdapter == null) {
            scheduleAdapter = new MortgageScheduleAdapter(schedule -> {
                // Callback khi bấm vào một kỳ thanh toán, có thể dùng để hiển thị chi tiết
                // Hiện tại chưa triển khai thêm action
            });
            binding.rvMortgageSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvMortgageSchedule.setAdapter(scheduleAdapter);
        }
    }

    /**
     * Tải danh sách schedule cho tài khoản thế chấp hiện tại.
     * @param accountId id tài khoản thế chấp
     */
    private void loadSchedulesForAccount(String accountId) {
        if (accountId == null) return;
        // Gọi ViewModel để lấy tất cả schedule và filter ở observer
        scheduleViewModel.getAll();
    }

    /**
     * Hàm xử lý khi người dùng bấm nút thanh toán kỳ.
     * Kiểm tra kỳ cần thanh toán, lấy danh sách tài khoản checking của user,
     * và hiển thị dialog chọn nguồn thanh toán.
     */
    private void onPayInstallment() {
        if (currentAccount == null || currentAccount.getMortgage() == null) {
            showError(requireContext(), "Error", "Không tìm thấy khoản vay.");
            return;
        }
        // Xác định kỳ cần thanh toán: kỳ đầu tiên có trạng thái PENDING hoặc OVERDUE
        MortgagePaymentSchedule target = null;
        if (scheduleAdapter != null) {
            List<MortgagePaymentSchedule> list = scheduleAdapter.getSchedules();
            if (list != null) {
                for (MortgagePaymentSchedule s : list) {
                    if (s.getStatus() == MortgageInstallmentStatus.PENDING || s.getStatus() == MortgageInstallmentStatus.OVERDUE) {
                        target = s;
                        break;
                    }
                }
            }
        }
        if (target == null) {
            showError(requireContext(), "Thông báo", "Không có kỳ thanh toán nào cần trả.");
            return;
        }
        this.pendingSchedule = target;

        // Gọi lấy danh sách tài khoản checking của user
        String userId = currentAccount.getUserId();
        if (userId == null) {
            showError(requireContext(), "Error", "Không tìm thấy userId của tài khoản.");
            return;
        }
        isSelectingSourceAccount = true;
        accountViewModel.getAccountsByUserIdAndType(userId, AccountType.CHECKING.name());
    }

    /**
     * Hiển thị dialog cho phép người dùng chọn tài khoản checking để làm nguồn thanh toán.
     * @param accounts danh sách tài khoản checking của user
     */
    private void showSelectSourceAccountDialog(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty() || pendingSchedule == null) {
            showError(requireContext(), "Thông báo", "Không tìm thấy tài khoản thanh toán phù hợp.");
            return;
        }
        // Chuẩn bị danh sách hiển thị: dùng accountNumber và balance
        String[] items = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            String balanceStr = CurrencyUtil.formatVND(acc.getBalance());
            items[i] = acc.getAccountName() + " (" + acc.getAccountNumber() + ") - " + balanceStr;
        }
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Chọn tài khoản nguồn")
                .setItems(items, (dialog, which) -> {
                    Account selected = accounts.get(which);
                    dialog.dismiss();
                    buildAndConfirmTransaction(selected);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Xây dựng giao dịch và chuyển sang màn hình xác nhận thanh toán.
     * @param sourceAccount tài khoản checking đã chọn
     */
    private void buildAndConfirmTransaction(Account sourceAccount) {
        if (pendingSchedule == null || sourceAccount == null) return;
        long amount = pendingSchedule.getAmountDue() == null ? 0L : pendingSchedule.getAmountDue().longValue();
        // Lấy bank TAD
        Bank bankTad = TransactionUtil.getTADBank();

        // Tạo transactionId và idempotencyKey
        String newId = TransactionUtil.generateTransactionId();
        boolean hasIdempotencyKey = transactionViewModel.getIdempotencyKey() != null;
        String idempotencyKey;
        if (!hasIdempotencyKey) {
            idempotencyKey = TransactionUtil.generateIdempotencyKey(
                    amount,
                    sourceAccount.getAccountNumber(),
                    currentAccount.getAccountNumber(),
                    TxnType.MORTGAGE_PAYMENT);
            transactionViewModel.setIdempotencyKey(idempotencyKey);
        } else {
            idempotencyKey = transactionViewModel.getIdempotencyKey();
        }
        // Tạo ref
        String transactionRef = TransactionUtil.generateRef();

        // Mô tả giao dịch
        String description = sourceAccount.getAccountName() + " thanh toán kỳ vay thế chấp";

        // Xây dựng transaction (sử dụng builder pattern giống Saving)
        Transaction transaction = Transaction.builder()
                .transactionId(newId)
                .accountId(sourceAccount.getAccountId())
                .accountNumber(sourceAccount.getAccountNumber())
                .accountName(sourceAccount.getAccountName())
                .amount(amount)
                .channel(TxnChannel.MOBILE_APP)
                .type(TxnType.MORTGAGE_PAYMENT)
                .status(TnxStatus.PENDING)
                .counterpartyAccount(currentAccount.getAccountNumber())
                .counterpartyName(currentAccount.getAccountName())
                .counterpartyBankCode(bankTad.getBankCode())
                .counterpartyBankName(bankTad.getBankLongName())
                .counterpartyBankLogo(bankTad.getBankImageUrl())
                .description(description)
                .feeAmount(0L)
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .transactionReference(transactionRef)
                .createdAt(new java.util.Date())
                .build();

        // Tạo payload
        TransferPayload payload = new TransferPayload(
                sourceAccount,
                transaction,
                bankTad
        );
        transactionPayloadViewModel.setTxnPayload(payload);

        // Mở màn hình xác nhận giao dịch với callback
        ((MainActivity) requireActivity()).openFeatureFragment(
                new TransactionConfirmFragment(new TransactionConfirmFragment.TransactionConfirmCallback() {
                    @Override
                    public void onTransactionSuccess() {
                        handlePaymentSuccess();
                    }

                    @Override
                    public void onTransactionFailed() {
                        // Không làm gì hoặc hiển thị thông báo
                        showError(requireContext(), "Thông báo", "Thanh toán thất bại");
                    }
                }),
                getString(R.string.xac_nhan_giao_dich)
        );
    }

    /**
     * Thực hiện cập nhật dữ liệu sau khi giao dịch thanh toán kỳ thành công.
     * Cập nhật status của kỳ trả, số tiền đã trả và ngày đến hạn kế tiếp của khoản vay.
     */
    private void handlePaymentSuccess() {
        if (pendingSchedule == null || currentAccount == null) return;
        // Cập nhật schedule: gán PAID và amountPaid
        pendingSchedule.status = MortgageInstallmentStatus.PAID;
        pendingSchedule.amountPaid = pendingSchedule.getAmountDue();
        scheduleViewModel.update(pendingSchedule);

        // Cập nhật nextDueDate trên MortgageAccount
        MortgageAccount mortgage = currentAccount.getMortgage();
        if (mortgage != null && pendingSchedule.getDueDate() != null) {
            try {
                java.util.Date oldDue = pendingSchedule.getDueDate();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(oldDue);
                // Cộng thêm 1 kỳ
                if (mortgage.getPaymentFrequency() == MortgagePaymentFrequency.BIWEEKLY) {
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 14);
                } else {
                    cal.add(java.util.Calendar.MONTH, 1);
                }
                mortgage.setNextDueDate(cal.getTime());
                // Bạn có thể cập nhật outstanding principal và paidPeriods nếu có trong model
            } catch (Exception ignored) {
            }
        }
        accountViewModel.updateAccount(currentAccount);
        // Sau khi cập nhật, refresh lại danh sách schedule
        loadSchedulesForAccount(currentAccount.getAccountId());
    }
}