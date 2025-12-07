package com.example.tad_bank_t1.ui.fragment.customer.transfer;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.dto.TransferInfoDTO;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.form.BankTransferForm;
import com.example.tad_bank_t1.ui.viewmodel.BankViewModel;
import com.example.tad_bank_t1.ui.viewmodel.ExternalAccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.util.Constants;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;


public class BankTransferFragment extends Fragment {
    private ImageButton imbtShowListBank, imbtShowListBeneficiaryTransfer;
    private Button btnContinueTransfer;
    private LottieAnimationView lottie_loading_waiting_transfer, lottie_loading_search_beneficiary_account;

    private TextView txtReceiverBankName, txtReceiverName;
    private TextInputEditText txtTransferAmount, txtTransferDescription, txtTransferAccNumber;
    private TextInputEditText txtReceiverAccNumber;
    private LinearLayout lnloTransferReceiverName;

    private SessionViewModel sessionViewModel;
    private BankViewModel bankViewModel;
    private ExternalAccountViewModel externalAccountViewModel;
    private Bank selectedBank;
    private Account accountSource;
    private boolean userTriggeredSearch = false;
    private boolean beneficiaryVerified = false;

    private double transferAmount;
    private boolean isEditingAmount = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank_transfer, container, false);

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

        // bank va nguoi thu huong
        imbtShowListBank = view.findViewById(R.id.imbtShowListBank);
        imbtShowListBeneficiaryTransfer = view.findViewById(R.id.imbtShowListBeneficiaryTransfer);
        // xac nhan chuyen tien
        btnContinueTransfer = view.findViewById(R.id.btnContinueTransfer);
        // loading cho tao giao dich va tiem kien nguoi thu huong
        lottie_loading_waiting_transfer = view.findViewById(R.id.lottie_loading_waiting_transfer);
        lottie_loading_search_beneficiary_account = view.findViewById(R.id.lottie_loading_search_beneficiary_account);
        // nguoi nhan tien
        txtReceiverBankName = view.findViewById(R.id.txtReceiverBankName);
        txtReceiverAccNumber = view.findViewById(R.id.txtReceiverAccNumber);
        txtReceiverName = view.findViewById(R.id.txtReceiverName);
        lnloTransferReceiverName = view.findViewById(R.id.lnloTransferReceiverName);
        // thong tin chuyen tien
        txtTransferAmount = view.findViewById(R.id.txtTransferAmount);
        txtTransferDescription = view.findViewById(R.id.txtTransferDescription);

        // ------------
        // event handle
        // -----------
        imbtShowListBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                        "Chọn ngân hàng nhận",
                        "Nhập ngân hàng",
                        "Danh sách ngân hàng",
                        Constants.SEARCH_BANK
                );
                FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                        getParentFragmentManager(),
                        R.id.fragment_container_search);
            }
        });

        imbtShowListBeneficiaryTransfer.setOnClickListener(v -> {
            SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                    "Chọn danh bạ thụ hưởng",
                    "Nhập người thụ hưởng",
                    "Danh sách thụ hưởng",
                    Constants.SEARCH_BENEFICIARY_ACCOUNT
            );
            FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                    getParentFragmentManager(),
                    R.id.fragment_container_search);
        });

        txtReceiverAccNumber.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnterKey = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP;
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || isEnterKey) {
                triggerSearch();
                return true; // đã xử lý
            }
            return false;
        });

        txtReceiverAccNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                beneficiaryVerified = false;
                lnloTransferReceiverName.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        // xử lý UI khi nhập số tiền
        txtTransferAmount.addTextChangedListener(new TextWatcher() {
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
                    txtTransferAmount.setText(formatted);
                    txtTransferAmount.setSelection(formatted.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                isEditingAmount = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        // xử lý continue btn
        btnContinueTransfer.setOnClickListener(v -> {
            if (accountSource == null) {
                showLookupDialog("Thiếu thông tin", "Chưa chọn tài khoản nguồn.", null);
                return;
            }
            if (!beneficiaryVerified) {
                showLookupDialog("Chưa xác minh", "Hãy tra cứu người thụ hưởng trước.", txtReceiverAccNumber);
                return;
            }

            if (transferAmount <= 0) {
                txtTransferAmount.setError("Nhập số tiền cần chuyển");
                txtTransferAmount.requestFocus();
                return;
            }

            if (transferAmount > accountSource.getBalance()){
                showLookupDialog("Không đủ số dư", "Số dư hiện tại không đủ cho giao dịch.", txtTransferAmount);
                return;
            }

            lottie_loading_waiting_transfer.setVisibility(View.VISIBLE);
            btnContinueTransfer.setEnabled(false);
            btnContinueTransfer.setText("Đang chuyển hướng...");

            BankTransferForm payloadTransfer = new BankTransferForm(
                    accountSource.getAccountId(),
                    accountSource.getAccountNumber(),
                    accountSource.getAccountName(),
                    txtReceiverAccNumber.getText().toString().trim(),
                    txtReceiverName.getText().toString().trim(),
                    selectedBank,
                    transferAmount,
                    txtTransferDescription.getText().toString().trim()
            );

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                lottie_loading_waiting_transfer.setVisibility(View.GONE);
                btnContinueTransfer.setEnabled(true);
                btnContinueTransfer.setText(getString(R.string.tiep_tuc));

                ((MainActivity) requireActivity())
                        .openFeatureFragment(new ConfirmTransactionFragment(),
                                getString(R.string.xac_nhan_giao_dich));
            }, 400);
        });
    }

    // khởi tạo viewmodel và observer
    private void initViewModel() {

        // view model
        bankViewModel = new ViewModelProvider(requireActivity()).get(BankViewModel.class);
        externalAccountViewModel = new ViewModelProvider(requireActivity()).get(ExternalAccountViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);

        // observe
        bankViewModel.getSelectedBank().observe(getViewLifecycleOwner(), bank -> {
            if (bank == null) return;

            boolean changed = (selectedBank == null) || !selectedBank.getBankId().equalsIgnoreCase(bank.getBankId());
            selectedBank = bank;

            String bankNameUppercase = bank.getBankName().toUpperCase();
            txtReceiverBankName.setText(bankNameUppercase + " - " + bank.getBankLongName());

            if (changed) {
                txtReceiverAccNumber.setText("");
                beneficiaryVerified = false;
                lnloTransferReceiverName.setVisibility(View.GONE);
                externalAccountViewModel.clearError();
                externalAccountViewModel.clearAccounts();
            }
        });

        sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                accountSource = account;
            }
        });

        externalAccountViewModel.getExternalAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                txtReceiverName.setText(account.getAccountName().toUpperCase());
                lnloTransferReceiverName.setVisibility(View.VISIBLE);
                beneficiaryVerified = true;
                userTriggeredSearch = false;
//                Toast.makeText(getContext(), "External account: " + account.getAccountName(), Toast.LENGTH_SHORT).show();
            }
        });
        externalAccountViewModel.getInternalAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                txtReceiverName.setText(account.getAccountName().toUpperCase());
                lnloTransferReceiverName.setVisibility(View.VISIBLE);
                beneficiaryVerified = true;
                userTriggeredSearch = false;
//                Toast.makeText(getContext(), "Internal account: " + account.getAccountName(), Toast.LENGTH_SHORT).show();
            }
        });
        externalAccountViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            lottie_loading_search_beneficiary_account.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            lottie_loading_search_beneficiary_account.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                lnloTransferReceiverName.setVisibility(View.GONE);
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
                    , txtReceiverAccNumber);
            resetReceiverSection();
        });


    }


    // trigger bắt lỗi tìm kiếm tài khoản nhận
    private void triggerSearch() {
        String accNumber = txtReceiverAccNumber.getText().toString().trim();
        if (selectedBank == null) {
            Toast.makeText(getContext(), "Hãy chọn ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (accNumber.isEmpty()) {
            txtReceiverAccNumber.setError("Nhập số tài khoản");
            txtReceiverAccNumber.requestFocus();
            return;
        }

        // Tránh bấm liên tục khi đang loading
        Boolean isLoading = externalAccountViewModel.getLoading().getValue();
        if (Boolean.TRUE.equals(isLoading)) return;

        userTriggeredSearch = true;
        externalAccountViewModel.clearError();
//        externalAccountViewModel.clearExternalAccount(); // clear kết quả cũ trước khi tra

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtReceiverAccNumber.getWindowToken(), 0);

        boolean isTadBank = "TAD".equalsIgnoreCase(selectedBank.getBankCode());
        externalAccountViewModel.searchAccounts(isTadBank, selectedBank.getBankId(), accNumber);
    }

    // clear kết quả lỗi
    private void resetReceiverSection() {
        // Ẩn card + xoá nội dung
        lnloTransferReceiverName.setVisibility(View.GONE);
        txtReceiverName.setText("");
        // Tắt loading nếu còn
        lottie_loading_search_beneficiary_account.setVisibility(View.GONE);
        // Clear error để không hiện lại dialog
        externalAccountViewModel.clearError();
        externalAccountViewModel.clearAccounts();
        // (tuỳ chọn) xoá số tài khoản & focus lại
        // txtReceiverAccNumber.setText("");
        txtReceiverAccNumber.requestFocus();
        // Ẩn bàn phím -> hiện lại nếu muốn gõ tiếp
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtReceiverAccNumber, InputMethodManager.SHOW_IMPLICIT);
    }


    // hiển thị lỗi khi bấm tiếp tục, trỏ chuột tới nơi lỗi
    private void showLookupDialog(String title, String message, TextInputEditText txtEdit) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
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
        // Show the bottom navigation bar when the user leaves this fragment
//        Log.d("TAG", "BANK TRANSFER onstop");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }
}