package com.example.tad_bank_t1.ui.fragment.customer.savings;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.SavingsAccount;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.databinding.FragmentSavingAccountDetailBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionConfirmFragment;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.SavingViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.MockAccountFactory;
import com.example.tad_bank_t1.util.SavingUtil;
import com.example.tad_bank_t1.util.TransactionUtil;

import java.util.Date;

public class SavingAccountDetailFragment extends Fragment implements UiConfig, BaseCustomFragment {
    // truyen vao id tai khoan
    private static final String ACCOUNT_ID = "accountId";
    private String accountId;
    private Account currentAccount;

    private FragmentSavingAccountDetailBinding binding;

    // view model
    private AccountViewModel accountViewModel;
    private SavingViewModel savingViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;
    private TransactionViewModel transactionViewModel;

    private AccountType accountType = AccountType.SAVING;

    // boolena check
    private boolean isRequestPayoutAcc = false;
    private long payoutAmount;


    public SavingAccountDetailFragment() {
        // Required empty public constructor
    }

    
    public static SavingAccountDetailFragment newInstance(String accountId) {
        SavingAccountDetailFragment fragment = new SavingAccountDetailFragment();
        Bundle args = new Bundle();
        args.putString(ACCOUNT_ID, accountId); 
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountId = getArguments().getString(ACCOUNT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavingAccountDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFragment();
    }

    @Override
    public void initView() {
        if (currentAccount == null) return;

        renderAccount(currentAccount);
    }

    @Override
    public void initViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        savingViewModel = new ViewModelProvider(requireActivity()).get(SavingViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);


        if(accountId == null){
            showError(requireContext(), "Lỗi", "Không tìm thấy tài khoản");
            return;
        }
        // load account theo id
        accountViewModel.getAccountById(accountId);

        // mock
        if ("demo".equals(accountId)) {
            Account mock = MockAccountFactory.createMockSavingAccount();
            renderAccount(mock); // hoặc gọi initView() nếu bạn bind ở initView
            return;
        }

        // quan sat nếu có dữ liệu
        accountViewModel.getAccountState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isLoading()) {
                toggleLoading(true);
            }
            toggleLoading(false);

            if (result.getData() != null) {
                if (result.getData().getType() == AccountType.SAVING){
                    currentAccount = result.getData();
                    renderAccount(currentAccount);
                } else {
                    showError(requireContext(), "Lỗi", "Không phải tài khoản tiết kiệm");
                }
            }

            if (result.getError() != null) {
                showError(requireContext(), "Lỗi", result.getError());
            }
        });

        // quan sat da dong tai khoan thi chuyen sang man hinh confirm
        accountViewModel.getUpdateState().observe(getViewLifecycleOwner(), result -> {
            // neu dat colse thi chuyen qua man hinh confirm thôi
            if (result == null) return;

            if (result.isLoading()) {
                toggleLoading(true);
            }

            toggleLoading(false);


        });

        savingViewModel.getPayoutAccState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isLoading()) {
                toggleLoading(true);
            }
            toggleLoading(false);

            if (result.getData() != null) {
                buildTransaction(payoutAmount, currentAccount, result.getData());

                // chuyên màn hình
                ((MainActivity) requireActivity()).openFeatureFragment(
                        new TransactionConfirmFragment(new TransactionConfirmFragment.TransactionConfirmCallback() {
                            @Override
                            public void onTransactionSuccess() {
                                currentAccount.setStatus(AccountStatus.CLOSED);
                                accountViewModel.updateAccount(currentAccount);
                            }

                            @Override
                            public void onTransactionFailed() {
//                                showError(requireContext(), "Lỗi", "Gửi tiền thất bại");
                                currentAccount.setStatus(AccountStatus.OPEN);
                                accountViewModel.updateAccount(currentAccount);
                            }
                        }),
                        getString(R.string.xac_nhan_giao_dich)
                );
            }

            if (result.getError() != null) {
                showError(requireContext(), "Lỗi", result.getError());
            }
        });
    }

    @Override
    public void setUpEvents() {
        binding.btnCloseSavingAcc.setOnClickListener(v -> {
            if (currentAccount == null) return;

            if (currentAccount.getStatus() != AccountStatus.OPEN) {
                showError(requireContext(), "Thông báo", "Sổ tiết kiệm đã đóng rồi.");
                return;
            }

            if (currentAccount.getSaving() == null) {
                showError(requireContext(), "Lỗi", "Không tìm thấy thông tin sổ tiết kiệm.");
                return;
            }

            showConfirmCloseDialog();
        });
    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }


    @Override
    public String getAppBarTitle() {
        return getString(R.string.chi_tiet_tai_khoan);
    }

    // render account
    private void renderAccount(Account account){
        if (account != null){
            binding.txtSavingDetailAccountNumber.setText(account.getAccountNumber());
            binding.txtSavingDetailStatus.setText(account.getStatus() == AccountStatus.OPEN ? "Đang hoạt động" : "Đã đóng");
            binding.txtSavingDetailBalance.setText(CurrencyUtil.formatVND(account.getBalance()));


            if (account.getSaving() != null){
                SavingsAccount savingsAccount = account.getSaving();
                binding.txtSavingDetailStartDate.setText(DateTimeUtil.formatDateToVNTime(savingsAccount.getStartDate()));
                binding.txtSavingDetailMaturityDate.setText(DateTimeUtil.formatDateToVNTime(savingsAccount.getMaturityDate()));

                binding.txtSavingDetailPolicyName.setText(savingsAccount.getPolicyName());
                binding.txtSavingDetailInterestRate.setText(savingsAccount.getAprAtOpen() + "%");
                binding.txtSavingDetailInterestPaymentMethod.setText(savingsAccount.getInterestPaymentMethod().toString());
                binding.txtSavingDetailTermLength.setText(savingsAccount.getCapitalization().toString());

                double rate = savingsAccount.getAprAtOpen();
                Long principal = account.getBalance();
                long profitPerMonth = SavingUtil.calProfitPerMonth(rate, principal);

                int termMonths = SavingUtil.calculateTermMonths(savingsAccount.getStartDate(), savingsAccount.getMaturityDate());
                binding.txtSavingDetailTermLength.setText(termMonths + " month(s)");

                binding.txtSavingDetailProfitMonthInterest.setText(CurrencyUtil.formatVND(profitPerMonth));
                binding.txtSavingDetailTotalMaturity.setText(CurrencyUtil.formatVND(profitPerMonth * termMonths));
            }
        }
    }

    /**
     * Hiển thị dialog xác nhận đóng sổ và rút tiền về tài khoản nhận
     */
    private void showConfirmCloseDialog() {
        SavingsAccount saving = currentAccount.getSaving();

        long principal = currentAccount.getBalance() == null ? 0L : currentAccount.getBalance();
        double rate = saving.getAprAtOpen();
        boolean isMatured = SavingUtil.isMatured(saving.getMaturityDate());

        long interest = SavingUtil.calculateInterestUntilClose(
                principal,
                rate,
                saving.getStartDate(),
                saving.getMaturityDate(),
                new java.util.Date(),
                isMatured
        );

        payoutAmount = principal + interest;

        String payoutAccountId = saving.getPayoutAccountId();
        String note = isMatured
                ? "Sổ đã đáo hạn. Bạn sẽ nhận gốc + lãi."
                : "Sổ chưa đáo hạn. Lãi có thể bị giảm/0 tùy quy định.";

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đóng sổ tiết kiệm")
                .setMessage(
                        "Rút về tài khoản nhận: " + payoutAccountId +
                                "\nGốc: " + CurrencyUtil.formatVND(principal) +
                                "\nLãi tạm tính: " + CurrencyUtil.formatVND(interest) +
                                "\nTổng nhận: " + CurrencyUtil.formatVND(payoutAmount) +
                                "\n\n" + note
                )
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .setPositiveButton("Xác nhận đóng & rút", (d, w) -> {
                    d.dismiss();
                    closeSavingAccount();
                })
                .show();
    }

    /**
     * Gọi ViewModel xử lý đóng sổ
     */
    private void closeSavingAccount() {
        toggleLoading(true);
//        accountViewModel.closeSavingAccount(accountId);

        isRequestPayoutAcc = true;
        // tim payout account
        savingViewModel.getPayoutAccountById(currentAccount.getSaving().getPayoutAccountId());
    }

    /**
     * Build giao dịch khi có source và target
     */
    private void buildTransaction(long amount, Account sourceAccount, Account targetAccount){
        // tạo giao dịch
        Bank bankTad = TransactionUtil.getTADBank();
        // id phải độc nhất vô nhị
        String newId = TransactionUtil.generateTransactionId();
        // tạo idempotency key
        // Kiem tra view model co idem chua neu chua co thi phai tao moi
        boolean hasIdempotencyKey = transactionViewModel.getIdempotencyKey() != null;
        String idempotencyKey = "";
        if (!hasIdempotencyKey) {
            idempotencyKey = TransactionUtil.generateIdempotencyKey(amount, sourceAccount.getAccountNumber(), targetAccount.getAccountNumber(), TxnType.SAVING_DEPOSIT);
            transactionViewModel.setIdempotencyKey(idempotencyKey);

            Log.d("TAG CREATE TRANSACTION", "idempotencyKey mới tạo: " + idempotencyKey);
        } else {
            idempotencyKey = transactionViewModel.getIdempotencyKey();

            // log idem
            Log.d("TAG CREATE TRANSACTION", "idempotencyKey đã có: " + idempotencyKey);
        }

        // Tạo ref
        String transactionRef = TransactionUtil.generateRef();

        // tao giao dich rut tien tu checking sang saving
        Transaction transaction = Transaction.builder()
                .transactionId(newId)
                .accountId(sourceAccount.getAccountId())
                .accountNumber(sourceAccount.getAccountNumber())
                .accountName(sourceAccount.getAccountName())
                .amount(amount)
                .channel(TxnChannel.MOBILE_APP)
                .type(TxnType.SAVING_WITHDRAW)
                .status(TnxStatus.PENDING)
                .counterpartyAccount(targetAccount.getAccountNumber())
                .counterpartyName(targetAccount.getAccountName())
                .counterpartyBankCode(bankTad.getBankCode())
                .counterpartyBankName(bankTad.getBankLongName())
                .counterpartyBankLogo(bankTad.getBankImageUrl())
                .description(sourceAccount.getAccountName() + " đóng sổ tiết kiệm")
                .feeAmount(0L)
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .transactionReference(transactionRef)
                .createdAt(new Date())
                .build();


        // ------------------
        // Tạo transaction lên server
        // ------------------
        Log.d("TAG TRANSACTION", transaction.toString());

        // chỉ cần preview chưa cần tạo giao dịch thật trên server ở bước này
        //transactionViewModel.createTransaction(transaction);

        // ---------------------
        // Lưu transactionId, transactionRef, idempotencyKey,... vào payload
        // ---------------------


        // =============================
        // save payload và chuyển màn hình
        // =============================
        TransferPayload payloadTransfer = new TransferPayload(
                sourceAccount,
                transaction,
                bankTad
        );

        transactionPayloadViewModel.setTxnPayload(payloadTransfer);
    }

}