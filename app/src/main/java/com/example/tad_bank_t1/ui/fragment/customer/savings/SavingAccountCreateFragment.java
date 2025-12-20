package com.example.tad_bank_t1.ui.fragment.customer.savings;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.SavingsAccount;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.data.model.enums.saving.InterestPaymentMethod;
import com.example.tad_bank_t1.data.model.enums.saving.SavingCapitalization;
import com.example.tad_bank_t1.databinding.FragmentSavingAccountCreateBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionConfirmFragment;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.SavingPolicyViewModel;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.FormatUtils;
import com.example.tad_bank_t1.util.SavingUtil;
import com.example.tad_bank_t1.util.TransactionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavingAccountCreateFragment extends Fragment implements UiConfig, BaseCustomFragment {
    private static final String USER_ID = "userId";

    // TODO: Rename and change types of parameters
    private String userId;

    private FragmentSavingAccountCreateBinding binding;

    // view model
    private AccountViewModel accountViewModel;
    private SavingPolicyViewModel savingPolicyViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;
    private TransactionViewModel transactionViewModel;
    
    private List<SavingsRatePolicy> policies;
    private SavingsRatePolicy selectedPolicy;
    private Date selectedDate;
    private List<Account> checkingAccounts;
    private Account selectedCheckingAccount;
    private boolean awaitingCheckingAccounts = false;
    private String newAccNumber;
    // Chu kỳ nhập lãi mặc định
    private SavingCapitalization selectedCapitalization = SavingCapitalization.MONTHLY;



    public SavingAccountCreateFragment() {
        // Required empty public constructor
    }
 
    public static SavingAccountCreateFragment newInstance(String userId, String param2) {
        SavingAccountCreateFragment fragment = new SavingAccountCreateFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);

            Toast.makeText(requireContext(), "userId: " + userId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSavingAccountCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFragment();
    }

    private void initViews(View view) { 
    }

    private void setupListeners() {
//        btnBack.setOnClickListener(v -> finish());

        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.btnConfirmCreate.setOnClickListener(v -> handleSubmit());

        binding.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateEstimation();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // =======================
        // Xử lý chọn chu kỳ nhập lãi
        // =======================
        binding.rgCapitalization.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMonthly) {
                selectedCapitalization = SavingCapitalization.MONTHLY;
            } else if (checkedId == R.id.rbQuarterly) {
                selectedCapitalization = SavingCapitalization.QUARTERLY;
            }
        });
    }


    private void setupPolicySpinner() {
        if (policies == null) return;
        List<String> policyNames = new ArrayList<>();
        for (SavingsRatePolicy policy : policies) {
            policyNames.add(policy.getPolicyName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                policyNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPolicy.setAdapter(adapter);

        binding.spinnerPolicy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPolicy = policies.get(position);
                updatePolicyInfo();
                updateEstimation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updatePolicyInfo() {
        if (selectedPolicy != null) {
            binding.layoutPolicyInfo.setVisibility(View.VISIBLE);
            binding.tvPolicyName.setText(selectedPolicy.getPolicyName());
            binding.tvPolicyDetail.setText(getString(
                    R.string.policy_detail_format,
                    selectedPolicy.getTermMonths(),
                    selectedPolicy.getInterestRate()
            ));
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
                    binding.btnSelectDate.setText(sdf.format(selectedDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateEstimation() {
        String amountStr = binding.etAmount.getText().toString();

        if (selectedPolicy != null && !amountStr.isEmpty()) {
            try {
                long amount = Long.parseLong(amountStr);
                if (amount > 0) {
                    double ratePerYear = selectedPolicy.getInterestRate() / 100.0;
                    int months = selectedPolicy.getTermMonths();

                    long profitPerMonth = SavingUtil.calProfitPerMonth(ratePerYear, amount);
                    long totalInterest = profitPerMonth * months;
                    long totalReceive = amount + totalInterest;

                    // yêu cầu đề: profits per month
                    binding.tvEstimatedInterest.setText("+" + FormatUtils.formatCurrency(profitPerMonth, "VND") + " / tháng");
                    binding.tvTotalInterest.setText("+" + FormatUtils.formatCurrency(totalInterest, "VND"));
                    binding.tvTotalReceive.setText(FormatUtils.formatCurrency(totalReceive, "VND"));


                    binding.cardEstimation.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid number
            }
        }

        binding.cardEstimation.setVisibility(View.GONE);
    }

    private void handleSubmit() {
        if (!isValidInput(selectedCheckingAccount)) return;

        // tạo saving
        Account targetSavingAcc = buildSavingAccount(selectedCheckingAccount.getAccountId(),
                selectedCheckingAccount.getAccountName(), newAccNumber, selectedCheckingAccount.getPinCode()
        );

        // tạo saving
        accountViewModel.createAccount(targetSavingAcc);


        String amountStr = binding.etAmount.getText().toString();
        long amount = 0L;
        try {
            amount = Long.parseLong(amountStr);
            if (amount < 1000000) {
                Toast.makeText(requireActivity(), "Số tiền tối thiểu là 1.000.000 ₫", Toast.LENGTH_SHORT).show();
                return;
            }
            buildTransaction(amount, selectedCheckingAccount, targetSavingAcc);
        } catch (NumberFormatException e) {
            Toast.makeText(requireActivity(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private boolean isValidInput(Account sourceAccount){
        // Validate
        if (selectedPolicy == null) {
            Toast.makeText(requireActivity(), "Vui lòng chọn chính sách", Toast.LENGTH_SHORT).show();
            return false;
        }

        String amountStr = binding.etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(requireActivity(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        long amount = 0L;
        try {
            amount = Long.parseLong(amountStr);
            if (amount < 1000000) {
                Toast.makeText(requireActivity(), "Số tiền tối thiểu là 1.000.000 ₫", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireActivity(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDate == null) {
            Toast.makeText(requireActivity(), "Vui lòng chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (DateTimeUtil.isBeforeToday(selectedDate)) {
            Toast.makeText(requireActivity(), "Ngày bắt đầu phải sau ngày hiện tại trở đi", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sourceAccount == null) {
            Toast.makeText(requireActivity(), "Vui lòng chọn tài khoản thanh toán nguồn", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sourceAccount.getBalance() < amount) {
            Toast.makeText(requireActivity(), "Số dư tài khoản thanh toán không đủ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private Account buildSavingAccount(String payoutAccId, String accName, String accNumber, String pinCode){
        // Tạo saving payload
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setPolicyId(selectedPolicy.getSavingPolicyId());
        savingsAccount.setPolicyName(selectedPolicy.getPolicyName());
        savingsAccount.setAprAtOpen(selectedPolicy.getInterestRate());
        savingsAccount.setStartDate(selectedDate);
        savingsAccount.setMaturityDate(SavingUtil.addMonths(selectedDate, selectedPolicy.getTermMonths()));
        savingsAccount.setCapitalization(selectedCapitalization);
        savingsAccount.setInterestPaymentMethod(InterestPaymentMethod.TO_CHECKING);
        savingsAccount.setPayoutAccountId(payoutAccId);

        Account account = new Account();
        account.setUserId(userId);
        account.setType(AccountType.SAVING);
        account.setBalance(0L); // chưa set số dư
        account.setAccountName(accName);
        account.setAccountNumber(accNumber);
        account.setPinCode(pinCode);
        account.setCreatedAt(new Date());
        account.setUpdatedAt(new Date());
        account.setCurrency("VND");
        account.setDefault(false);
        account.setStatus(AccountStatus.OPEN);
        account.setSaving(savingsAccount);

        return account;
    }
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
                .type(TxnType.SAVING_DEPOSIT)
                .status(TnxStatus.PENDING)
                .counterpartyAccount(targetAccount.getAccountNumber())
                .counterpartyName(targetAccount.getAccountName())
                .counterpartyBankCode(bankTad.getBankCode())
                .counterpartyBankName(bankTad.getBankLongName())
                .counterpartyBankLogo(bankTad.getBankImageUrl())
                .description(sourceAccount.getAccountName() + " nạp tiết kiệm")
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

    private void renderSelectedCheckingInfo(Account checking) {
        if (checking == null) {
            binding.txtSavingCreateAccName.setText("--");
            binding.txtSavingCreateAccNumber.setText("--");
            return;
        }

        // Tên tài khoản: ưu tiên accountName, không có thì lấy userId/username (tuỳ bạn)
        String accountName = checking.getAccountName();
        if (accountName == null || accountName.trim().isEmpty()) {
            accountName = "Chủ tài khoản";
        }

        binding.txtSavingCreateAccName.setText(accountName);

//        if (newAccNumber != null) return;
        newAccNumber = SavingUtil.generateAccountNumber();
        binding.txtSavingCreateAccNumber.setText(newAccNumber);

    }

    private void setupSourceCheckingDropdown() {
        if (checkingAccounts == null || checkingAccounts.isEmpty()) {
            binding.tilSourceChecking.setError("Bạn chưa có tài khoản thanh toán");
            binding.actvSourceChecking.setEnabled(false);
            binding.txtSourceCheckingBalance.setText("Số dư khả dụng: --");
            return;
        }

        binding.tilSourceChecking.setError(null);
        binding.actvSourceChecking.setEnabled(true);

        List<String> displayList = new ArrayList<>();
        for (Account account : checkingAccounts) {
            String display = account.getAccountNumber()
                    + " • " + FormatUtils.formatCurrency(account.getBalance(), "VND");
            displayList.add(display);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayList
        );
        binding.actvSourceChecking.setAdapter(adapter);

        // Default chọn account đầu tiên
        selectedCheckingAccount = checkingAccounts.get(0);
        renderSelectedCheckingInfo(selectedCheckingAccount);

        binding.actvSourceChecking.setText(displayList.get(0), false);
        binding.txtSourceCheckingBalance.setText(
                "Số dư khả dụng: " + FormatUtils.formatCurrency(selectedCheckingAccount.getBalance(), "VND")
        );

        binding.actvSourceChecking.setOnItemClickListener((parent, view, position, id) -> {
            selectedCheckingAccount = checkingAccounts.get(position);
            renderSelectedCheckingInfo(selectedCheckingAccount);
            binding.txtSourceCheckingBalance.setText(
                    "Số dư khả dụng: " + FormatUtils.formatCurrency(selectedCheckingAccount.getBalance(), "VND")
            );
        });

    }



    @Override
    public void initView() {
        initViews(binding.getRoot());
//        setupPolicySpinner();
    }

    @Override
    public void initViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        savingPolicyViewModel = new ViewModelProvider(requireActivity()).get(SavingPolicyViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // 1) Load danh sách policy
        savingPolicyViewModel.getAll();
        savingPolicyViewModel.getListState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isLoading()) {
                toggleLoading(true);
                return;
            }
            toggleLoading(false);

            if (result.getError() != null) {
                showError(requireContext(), "Lỗi", result.getError());
                return;
            }

            if (result.getData() != null) {
                policies = result.getData();
                setupPolicySpinner();
            }
        });

        // 2) Load danh sách checking để chọn nguồn trừ tiền
        awaitingCheckingAccounts = true;
//        accountViewModel.resetState();
        accountViewModel.getAccountsByUserIdAndType(userId, AccountType.CHECKING.name());

//        showError(requireContext(), "Lỗi", "Lỗi tải danh sách tài khoản");
        accountViewModel.getListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                Log.d("DEBUG_CHECKING", "State nhận được: " + state);

                return;
            }
            Log.d("DEBUG_CHECKING", "State nhận được: " + state.toString());


            // Chỉ xử lý listState cho request checking này (để khỏi bị dính khi listState dùng việc khác)
//            if (!awaitingCheckingAccounts) return;

            if (state.isLoading()) {
                toggleLoading(true);
                return;
            }
            toggleLoading(false);

            if (state.getError() != null) {
                awaitingCheckingAccounts = false;
                showError(requireContext(), "Lỗi", state.getError());
                return;
            }

            Log.d("DEBUG_CHECKING", "State nhận được: " + state.toString());

            if (state.getData() != null) {
                awaitingCheckingAccounts = false;
                checkingAccounts = state.getData();
                Log.d("DEBUG_CHECKING", "Số lượng tài khoản nhận được: " + checkingAccounts.size());
                if (checkingAccounts.isEmpty()) {
                    Log.w("DEBUG_CHECKING", "Danh sách rỗng!");
                }
                setupSourceCheckingDropdown();
            }
        });

        // createState bạn có thể xử lý sau (khi implement createSavingWithDeposit)
        accountViewModel.getCreateState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                toggleLoading(true);
                return;
            }
            toggleLoading(false);

            if (state.getError() != null) {
                showError(requireContext(), "Lỗi", state.getError());
                return;
            }

            if (state.getData() != null) {
               // chuyên màn hình


                ((MainActivity) requireActivity()).openFeatureFragment(
                        new TransactionConfirmFragment(),
                        getString(R.string.xac_nhan_giao_dich)
                );

            }
        });
    }


    @Override
    public void setUpEvents() {
        setupListeners();
    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    @Override
    public String getAppBarTitle() {
        return getString(R.string.tao_moi_tai_khoan);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}