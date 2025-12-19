package com.example.tad_bank_t1.ui.fragment.customer.topup;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.databinding.FragmentMobileTopupTransferBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.payload.transactions.PhoneTopupPayload;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionConfirmFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.CardPaymentFragment;
import com.example.tad_bank_t1.ui.viewmodel.ProviderViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class MobileTopupFragment extends Fragment implements UiConfig, BaseCustomFragment {
    // binding
    private FragmentMobileTopupTransferBinding binding;

    // view model
    private ProviderViewModel providerViewModel;
    private SessionViewModel sessionViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;
    private TransactionViewModel transactionViewModel;

    //
    // declare data
    private Account accountSource;
    private Long selectedAmount = null; // lưu số tiền đã chọn

    @Override
    public String getAppBarTitle() {
        return getString(R.string.nap_tien_dien_thoai);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMobileTopupTransferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_topup,
                false);

        initFragment();
    }


    @Override
    public void initView() {

    }

    @Override
    public void initViewModel() {
        // view model
        providerViewModel = new ViewModelProvider(requireActivity()).get(ProviderViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);


        // observe
        sessionViewModel.payAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                accountSource = account;
            }
        });


    }

    public void setUpEvents(){
        // set click
        binding.cardViewTopUpAmount10.setOnClickListener(v -> selectAmount(10_000L, binding.cardViewTopUpAmount10));
        binding.cardViewTopUpAmount20.setOnClickListener(v -> selectAmount(20_000L, binding.cardViewTopUpAmount20));
        binding.cardViewTopUpAmount50.setOnClickListener(v -> selectAmount(50_000L, binding.cardViewTopUpAmount50));
        binding.cardViewTopUpAmount100.setOnClickListener(v -> selectAmount(100_000L, binding.cardViewTopUpAmount100));

        // nếu đã có selectedAmount trước đó -> apply UI
        if (selectedAmount != null) {
            applySelectedUI();
        }

        binding.edtReceiverPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                clearError();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        binding.btnContinueTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = binding.edtReceiverPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty()){
                    showError("Vui lòng nhập số điện thoại");
                    return;
                }

                if (phoneNumber.length() != 10){
                    showError("Số điện thoại không hợp lệ đủ 10 chữ số");
                    return;
                }

                // kiem tra da chọn số tiền chưa
                if (selectedAmount == null){
                    Toast.makeText(requireContext(), "Vui lòng chọn số tiền", Toast.LENGTH_SHORT).show();
                    return;
                }

                providerViewModel.checkTopup(phoneNumber).observe(getViewLifecycleOwner(), result -> {
                    if (result == null) return;

                    if (result.isLoading()) {
                        showLoading(true);
                        return;
                    }

                    showLoading(false);

                    if (result.getError() != null) {
                        showError(result.getError());
                        return;
                    }

                    if (result.getData() != null && result.getData()) {
                        // Valid phone
                        Toast.makeText(requireContext(), "Số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();

                        // call api thanh toan hoac tao payload
                        if (selectedAmount != null){
                            if (accountSource == null) {
                                showLookupDialog("Thiếu thông tin", "Chưa chọn tài khoản nguồn.", null);
                                return;
                            }

                            if (selectedAmount > accountSource.getBalance()) {
                                showLookupDialog("Không đủ số dư", "Số dư hiện tại không đủ cho giao dịch.", null);
                                return;
                            }

                            // check view model
                            if (transactionPayloadViewModel == null) {
                                Toast.makeText(getContext(), "transactionPayloadViewModel is null", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            // set loading
                            binding.btnContinueTopup.setEnabled(false);
                            binding.btnContinueTopup.setText("Đang chuyển hướng...");

                            // check type
                            TxnType type = TxnType.MOBILE_TOPUP;

                            // Input form
                            String sourceAccNumber = accountSource.getAccountNumber();
                            String targetAccNumber = binding.edtReceiverPhoneNumber.getText().toString().trim();

                            if (targetAccNumber.isEmpty()){
                                showError("Vui lòng nhập số điện thoại");
                                return;
                            }

                            String content = accountSource.getAccountName() + " nạp tiền điện thoại";

                            // id phải độc nhất vô nhị
                            String newId = TransactionUtil.generateTransactionId();
                            // tạo idempotency key
                            // Kiem tra view model co idem chua neu chua co thi phai tao moi
                            boolean hasIdempotencyKey = transactionViewModel.getIdempotencyKey() != null;
                            String idempotencyKey = "";
                            if (!hasIdempotencyKey) {
                                idempotencyKey = TransactionUtil.generateIdempotencyKey(selectedAmount, sourceAccNumber, targetAccNumber, type);
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
                                    .description(content)
                                    .amount(selectedAmount)
                                    .feeAmount(0L)
                                    .status(TnxStatus.PENDING)
                                    .type(type)
                                    .channel(TxnChannel.MOBILE_APP)
                                    .currency("VND")
                                    .idempotencyKey(idempotencyKey)
                                    .transactionReference(transactionRef)
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
                            PhoneTopupPayload payloadTopup = new PhoneTopupPayload(
                                    accountSource,
                                    transaction,
                                    targetAccNumber
                            );


                            transactionPayloadViewModel.setTxnPayload(payloadTopup);

                            // =============================
                            //Chuyển màn hình
                            // =============================
                            binding.btnContinueTopup.setEnabled(true);
                            binding.btnContinueTopup.setText(getString(R.string.tiep_tuc));



                            ((MainActivity) requireActivity()).openFeatureFragment(
                                    new TransactionConfirmFragment(),
                                    getString(R.string.xac_nhan_giao_dich)
                            );
                        }
                    }
                });

            }
        });
    }


    private void clearError(){
        binding.edtReceiverPhoneNumber.setError(null);

        binding.txtErrorPhone.setText("");
//        binding.txtErrorPhone.setVisibility(View.GONE);
    }

    private void selectAmount(Long amount, MaterialCardView selectedCard) {
        selectedAmount = amount;

        // reset tất cả về unselected
        setCardSelected(binding.cardViewTopUpAmount10, false);
        setCardSelected(binding.cardViewTopUpAmount20, false);
        setCardSelected(binding.cardViewTopUpAmount50, false);
        setCardSelected(binding.cardViewTopUpAmount100, false);

        // set selected cho card được bấm
        setCardSelected(selectedCard, true);

        // (tuỳ bạn) enable nút Continue khi đã chọn amount
         binding.btnContinueTopup.setEnabled(true);
    }

    private void applySelectedUI() {
        setCardSelected(binding.cardViewTopUpAmount10, selectedAmount == 10_000);
        setCardSelected(binding.cardViewTopUpAmount20, selectedAmount == 20_000);
        setCardSelected(binding.cardViewTopUpAmount50, selectedAmount == 50_000);
        setCardSelected(binding.cardViewTopUpAmount100, selectedAmount == 100_000);
    }

    private void setCardSelected(MaterialCardView card, boolean selected) {
        if (card == null) return;

        int bg = ContextCompat.getColor(requireContext(),
                selected ? R.color.topup_amount_bg_selected : R.color.topup_amount_bg_default);

        int stroke = ContextCompat.getColor(requireContext(),
                selected ? R.color.topup_amount_stroke_selected : R.color.topup_amount_stroke_default);

        card.setCardBackgroundColor(bg);
        card.setStrokeColor(stroke);
        card.setStrokeWidth(selected ? dp(2) : dp(1));
        //card.setChecked(selected); // vì card checkable="true"
    }


    // hiển thị lỗi khi bấm tiếp tục, trỏ chuột tới nơi lỗi
    private void showLookupDialog(String title, String message, TextInputEditText txtEdit) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true) // người dùng có thể bấm ra ngoài để tắt
                .setPositiveButton("OK", (d, w) -> {
//                    resetReceiverSection(); // reset khi bấm OK
                    d.dismiss();
                })
                .setNegativeButton("Thử lại", (d, w) -> {
                    // chỉ đóng dialog, focus vào ô nhập để người dùng sửa

                    if (txtEdit != null) {
                        txtEdit.requestFocus();
                    }
                    d.dismiss();
                })
                .show();
    }

    private int dp(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }


    private void showLoading(boolean isLoading) {
        binding.btnContinueTopup.setEnabled(!isLoading);
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    public void showError(String error) {
        binding.txtErrorPhone.setText(error);
        binding.txtErrorPhone.setVisibility(View.VISIBLE);

        binding.btnContinueTopup.setEnabled(true);
        ((MainActivity) requireActivity()).showLoadingFeature(false);
        binding.edtReceiverPhoneNumber.setError(error);
        showError(requireContext(), "Lỗi", error);
    }
}