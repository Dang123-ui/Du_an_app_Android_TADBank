package com.example.tad_bank_t1.ui.fragment.customer.transfer;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.databinding.FragmentBankTransferBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionConfirmFragment;
import com.example.tad_bank_t1.ui.viewmodel.BankViewModel;
import com.example.tad_bank_t1.ui.viewmodel.ExternalAccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.TadConstants;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;


public class BankTransferFragment extends Fragment implements UiConfig {
    // View binding
    private FragmentBankTransferBinding binding;

    // declare view model declare
    private SessionViewModel sessionViewModel;
    private BankViewModel bankViewModel;
    private ExternalAccountViewModel externalAccountViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;
    private TransactionViewModel transactionViewModel;


    // declare data
    private Bank selectedBank;
    private Account accountSource;
    private boolean userTriggeredSearch = false;
    private boolean beneficiaryVerified = false;

    private long transferAmount;
    private boolean isEditingAmount = false;


    @Override
    public String getAppBarTitle() {
        return getString(R.string.chuyen_tien);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.TOP));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBankTransferBinding.inflate(inflater, container, false);

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initViewModel();
    }

    // set up view and handle event
    private void initView(View view) {
        // ----------
        // bind view
        // ----------
        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_transfer,
                false);

        // ------------
        // event handle
        // -----------
        binding.imbtShowListBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fragmentContainerSearch.setVisibility(View.VISIBLE);
                SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                        "Chọn ngân hàng nhận",
                        "Nhập ngân hàng",
                        "Danh sách ngân hàng",
                        TadConstants.SEARCH_BANK
                );
                FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                        getParentFragmentManager(),
                        R.id.fragment_container_search);
            }
        });

        binding.imbtShowListBeneficiaryTransfer.setOnClickListener(v -> {
            SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                    "Chọn danh bạ thụ hưởng",
                    "Nhập người thụ hưởng",
                    "Danh sách thụ hưởng",
                    TadConstants.SEARCH_BENEFICIARY_ACCOUNT
            );
            FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                    getParentFragmentManager(),
                    R.id.fragment_container_search);
        });

        binding.txtReceiverAccNumber.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnterKey = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP;
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || isEnterKey) {
                triggerSearch();
                return true; // đã xử lý
            }
            return false;
        });

        binding.txtReceiverAccNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                beneficiaryVerified = false;
                binding.lnloTransferReceiverName.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        // xử lý UI khi nhập số tiền
        binding.txtTransferAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isEditingAmount) return;
                isEditingAmount = true;

                try {
                    // 1. Lấy chuỗi hiện tại, bỏ dấu chấm
                    String raw = s.toString().replace(".", "").trim();

                    // 2. Nếu chuỗi rỗng, reset
                    if (raw.isEmpty()) {
                        isEditingAmount = false;
                        return;
                    }

                    // 3. Parse sang số
                    long value = Long.parseLong(raw);
                    transferAmount = value;      // <-- GIÁ TRỊ THỰC DÙNG ĐỂ CHUYỂN KHOẢN

                    // 4. Format theo kiểu Việt Nam 1.000.000
                    NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
                    String formatted = nf.format(value);

                    // 5. Gán lại text nhưng giữ con trỏ ở cuối
                    binding.txtTransferAmount.setText(formatted);
                    binding.txtTransferAmount.setSelection(formatted.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                isEditingAmount = false;

                // set idempotency thành null
                if (transactionViewModel != null) {
                    transactionViewModel.setIdempotencyKey(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        // xử lý continue btn
        binding.btnContinueTransfer.setOnClickListener(v -> {
            if (accountSource == null) {
                showLookupDialog("Thiếu thông tin", "Chưa chọn tài khoản nguồn.", null);
                return;
            }
            if (!beneficiaryVerified) {
                showLookupDialog("Chưa xác minh", "Hãy tra cứu người thụ hưởng trước.", binding.txtReceiverAccNumber);
                return;
            }

            if (transferAmount <= 0) {
                binding.txtTransferAmount.setError("Nhập số tiền cần chuyển");
                binding.txtTransferAmount.requestFocus();
                return;
            }

            if (transferAmount > accountSource.getBalance()) {
                showLookupDialog("Không đủ số dư", "Số dư hiện tại không đủ cho giao dịch.", binding.txtTransferAmount);
                return;
            }

            // check account nguồn được chọn để chuyển tiền
            if (accountSource == null) {
                Toast.makeText(getContext(), "accountSource is null", Toast.LENGTH_SHORT).show();
                return;
            }

            // check view model
            if (transactionPayloadViewModel == null) {
                Toast.makeText(getContext(), "transactionPayloadViewModel is null", Toast.LENGTH_SHORT).show();
                return;
            }


            // set loading
            binding.btnContinueTransfer.setEnabled(false);
            binding.btnContinueTransfer.setText("Đang chuyển hướng...");

            // check type
            boolean isInternalTransfer = "TAD".equalsIgnoreCase(selectedBank.getBankCode());
            TxnType type = isInternalTransfer ? TxnType.TRANSFER_INTERNAL : TxnType.TRANSFER_EXTERNAL;

            // Input form
            String sourceAccNumber = accountSource.getAccountNumber();
            String targetAccNumber = binding.txtReceiverAccNumber.getText().toString().trim();

            // id phải độc nhất vô nhị
            String newId = TransactionUtil.generateTransactionId();
            // tạo idempotency key
            // Kiem tra view model co idem chua neu chua co thi phai tao moi
            boolean hasIdempotencyKey = transactionViewModel.getIdempotencyKey() != null;
            String idempotencyKey = "";
            if (!hasIdempotencyKey) {
                idempotencyKey = TransactionUtil.generateIdempotencyKey(transferAmount, sourceAccNumber, targetAccNumber, type);
                transactionViewModel.setIdempotencyKey(idempotencyKey);

                Log.d("TAG CREATE TRANSACTION", "idempotencyKey mới tạo: " + idempotencyKey);
            } else {
                idempotencyKey = transactionViewModel.getIdempotencyKey();

                // log idem
                Log.d("TAG CREATE TRANSACTION", "idempotencyKey đã có: " + idempotencyKey);
            }

            // Tạo ref
            String transactionRef = TransactionUtil.generateRef();


            // ---------------------
            // Tạo đối tượng giao dịch pending
            // ---------------------
            // log acc
            Log.d("TAG CREATE TRANSACTION", "SourceAccNumber: " + accountSource);
            Transaction transaction = Transaction.builder()
                    .transactionId(newId)
                    .accountId(accountSource.getAccountId())
                    .accountNumber(sourceAccNumber)
                    .accountName(accountSource.getAccountName())
                    .counterpartyAccount(targetAccNumber)
                    .counterpartyName(binding.txtReceiverName.getText().toString())
                    .counterpartyBankCode(selectedBank.getBankCode())
                    .counterpartyBankName(selectedBank.getBankLongName())
                    .counterpartyBankLogo(selectedBank.getBankImageUrl())
                    .description(binding.txtTransferDescription.getText().toString())
                    .amount(transferAmount)
                    .feeAmount(0L)
                    .status(TnxStatus.PENDING)
                    .type(type)
                    .channel(TxnChannel.MOBILE_APP)
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
                    accountSource,
                    transaction,
                    selectedBank
            );


            transactionPayloadViewModel.setTxnPayload(payloadTransfer);

            // =============================
            //Chuyển màn hình
            // =============================
            binding.btnContinueTransfer.setEnabled(true);
            binding.btnContinueTransfer.setText(getString(R.string.tiep_tuc));



            ((MainActivity) requireActivity()).openFeatureFragment(
                    new TransactionConfirmFragment(),
                    getString(R.string.xac_nhan_giao_dich)
            );

        });
    }

    // khởi tạo viewmodel và observer
    private void initViewModel() {

        // view model
        bankViewModel = new ViewModelProvider(requireActivity()).get(BankViewModel.class);
        externalAccountViewModel = new ViewModelProvider(requireActivity()).get(ExternalAccountViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);


        // observe
        bankViewModel.getSelectedBank().observe(getViewLifecycleOwner(), bank -> {
            if (bank == null) return;

            boolean changed = (selectedBank == null) || !selectedBank.getBankId().equalsIgnoreCase(bank.getBankId());
            selectedBank = bank;
            binding.fragmentContainerSearch.setVisibility(View.GONE);


            String bankNameUppercase = bank.getBankName().toUpperCase();
            binding.txtReceiverBankName.setText(bankNameUppercase + " - " + bank.getBankLongName());

            if (changed) {
                binding.txtReceiverAccNumber.setText("");
                beneficiaryVerified = false;
                binding.lnloTransferReceiverName.setVisibility(View.GONE);
                externalAccountViewModel.clearError();
                externalAccountViewModel.clearAccounts();
            }
        });

        sessionViewModel.payAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                accountSource = account;
            }
        });

        externalAccountViewModel.getExternalAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                binding.txtReceiverName.setText(account.getAccountName().toUpperCase());
                binding.lnloTransferReceiverName.setVisibility(View.VISIBLE);
                beneficiaryVerified = true;
                userTriggeredSearch = false;
//                Toast.makeText(getContext(), "External account: " + account.getAccountName(), Toast.LENGTH_SHORT).show();
            }
        });
        externalAccountViewModel.getInternalAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                binding.txtReceiverName.setText(account.getAccountName().toUpperCase());
                binding.lnloTransferReceiverName.setVisibility(View.VISIBLE);
                beneficiaryVerified = true;
                userTriggeredSearch = false;
//                Toast.makeText(getContext(), "Internal account: " + account.getAccountName(), Toast.LENGTH_SHORT).show();
            }
        });
        externalAccountViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            ((MainActivity)requireActivity()).showLoadingFeature(isLoading);
            if (isLoading) {
                binding.lnloTransferReceiverName.setVisibility(View.GONE);
                beneficiaryVerified = false; // đang tra lại → coi như chưa xác minh
            }
        });
        // 1) Clear trạng thái cũ trước khi đăng ký observer
        externalAccountViewModel.clearError(); // _error.setValue(null);

        // 2) Observe error: chỉ show dialog nếu là search do user kích hoạt
        externalAccountViewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (!userTriggeredSearch || err == null) return;
            userTriggeredSearch = false; // reset cờ
            showLookupDialog("Tra cứu người thụ hưởng",
                    "NOT_FOUND".equals(err) ? "Không tìm thấy tài khoản." : "Lỗi tra cứu: " + err
                    , binding.txtReceiverAccNumber);
            resetReceiverSection();
        });


    }


    // trigger bắt lỗi tìm kiếm tài khoản nhận
    private void triggerSearch() {
        String accNumber = binding.txtReceiverAccNumber.getText().toString().trim();
        if (selectedBank == null) {
            Toast.makeText(getContext(), "Hãy chọn ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isTadBank = "TAD".equalsIgnoreCase(selectedBank.getBankCode());

        if (accNumber.isEmpty()) {
            binding.txtReceiverAccNumber.setError("Nhập số tài khoản");
            binding.txtReceiverAccNumber.requestFocus();
            return;
        }

        // không cho chọn chính mình
        if (accNumber.equalsIgnoreCase(accountSource.getAccountNumber()) && isTadBank){
            binding.txtReceiverAccNumber.setError("Không được chọn chính mình");
            binding.txtReceiverAccNumber.requestFocus();
            return;
        }

        // Tránh bấm liên tục khi đang loading
        Boolean isLoading = externalAccountViewModel.getLoading().getValue();
        if (Boolean.TRUE.equals(isLoading)) return;

        userTriggeredSearch = true;
        externalAccountViewModel.clearError();
//        externalAccountViewModel.clearExternalAccount(); // clear kết quả cũ trước khi tra

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.txtReceiverAccNumber.getWindowToken(), 0);

        externalAccountViewModel.searchAccounts(isTadBank, selectedBank.getBankId(), accNumber);
    }

    // clear kết quả lỗi
    private void resetReceiverSection() {
        // Ẩn card + xoá nội dung
        binding.lnloTransferReceiverName.setVisibility(View.GONE);
        binding.txtReceiverName.setText("");
        // Tắt loading nếu còn
//        binding.lottieLoadingSearchBeneficiaryAccount.setVisibility(View.GONE);
        ((MainActivity)requireActivity()).showLoadingFeature(false);
        // Clear error để không hiện lại dialog
        externalAccountViewModel.clearError();
        externalAccountViewModel.clearAccounts();
        // (tuỳ chọn) xoá số tài khoản & focus lại
        // txtReceiverAccNumber.setText("");
        binding.txtReceiverAccNumber.requestFocus();
        // Ẩn bàn phím -> hiện lại nếu muốn gõ tiếp
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.txtReceiverAccNumber, InputMethodManager.SHOW_IMPLICIT);
    }


    // hiển thị lỗi khi bấm tiếp tục, trỏ chuột tới nơi lỗi
    private void showLookupDialog(String title, String message, TextInputEditText txtEdit) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true) // người dùng có thể bấm ra ngoài để tắt
                .setPositiveButton("OK", (d, w) -> {
                    resetReceiverSection(); // reset khi bấm OK
                    d.dismiss();
                })
                .setNegativeButton("Thử lại", (d, w) -> {
                    // chỉ đóng dialog, focus vào ô nhập để người dùng sửa
                    externalAccountViewModel.clearError();
                    if (txtEdit != null) {
                        txtEdit.requestFocus();
                    }
                    d.dismiss();
                })
                .show();
    }

    // hiển thị lỗi khi tạo giao dịch PENDING ở firebase
    private void showError(String title, String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true) // người dùng có thể bấm ra ngoài để tắt
                .setPositiveButton("OK", (d, w) -> {
                    d.dismiss();
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
//        Log.d("TAG", "BANK TRANSFER onstart");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("TAG FRAGMENT", "BANK TRANSFER onStop");

        // Người dùng BACK về Home → fragment bị dừng vì rời screen
        if (!requireActivity().getSupportFragmentManager().getFragments()
                .contains(this)) {
            Log.d("TAG FRAGMENT", "BANK TRANSFER onStop and clear view model");


            selectedBank = null;

            binding.txtReceiverBankName.setText("");

            // Clear UI state (Transfer-specific ViewModel)
            bankViewModel.clearBankSelected();

            // Clear UI account state (Transfer-specific ViewModel)
            externalAccountViewModel.clearAccounts();

            // Clear payload nếu user thực sự rời flow chuyển tiền
//            transactionPayloadViewModel.clearPayload();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}