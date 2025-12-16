package com.example.tad_bank_t1.ui.fragment.customer.transaction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.databinding.FragmentTransactionComfirmBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.otp.OTPFormFragment;
import com.example.tad_bank_t1.ui.form.otp.PINFormFragment;
import com.example.tad_bank_t1.ui.form.payload.transactions.BaseTransactionPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.BillPaymentPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.PhoneTopupPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.ui.viewmodel.OtpCodeViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;


public class TransactionConfirmFragment extends Fragment implements UiConfig {
    // View binding
    private FragmentTransactionComfirmBinding binding;

    // View model
    private SessionViewModel sessionViewModel;
    private TransactionViewModel transactionViewModel;
    private OtpCodeViewModel otpCodeViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;

    // payload từ fragment trước
    private BaseTransactionPayload transactionPayload;

    // check giao dịch đã xác thực hay chưa
    private boolean isVerified = false;

    private boolean hasNavigated = false;

    public TransactionConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public String getAppBarTitle() {
        return getString(R.string.xac_nhan_gd);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        // Transition khi Fragment mới xuất hiện (Enter)
//        setEnterTransition(new Slide(Gravity.RIGHT));
//
//        // Transition khi Fragment hiện tại biến mất (Exit)
//        setExitTransition(new Slide(Gravity.RIGHT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_transaction_confirm, container, false);
        binding = FragmentTransactionComfirmBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initAndObserveViewModel();

        initView(view);

        initEvents();
    }


    private void initView(View view){
        if (transactionPayload != null) {
            if (transactionPayload instanceof TransferPayload){
                Log.d("PAYLOAD : ", ((TransferPayload) transactionPayload).getTransaction().toString());
                TransferPayload txnPayload = (TransferPayload) transactionPayload;
                Transaction txn = txnPayload.getTransaction();

                binding.txtConfirmTransactionAccountSource.setText(txn.getAccountNumber());
                binding.txtConfirmTransactionAccountReceiver.setText(txn.getCounterpartyAccount());
                binding.txtConfirmTransactionNameReceiver.setText(txn.getCounterpartyName());
                binding.txtConfirmTransactionBankReceiver.setText(
                        txnPayload.receiverBank.getBankCode()
                        + "\n" + txnPayload.receiverBank.getBankLongName()
                );
                binding.txtConfirmTransactionContent.setText(txn.getDescription());
                binding.txtConfirmTransactionFeeAmount.setText(CurrencyUtil.formatVND(txn.getFeeAmount()));
                binding.txtConfirmTransactionAmount.setText(CurrencyUtil.formatVND(txn.getAmount()));

            } else if(transactionPayload instanceof PhoneTopupPayload) {

            } else if (transactionPayload instanceof BillPaymentPayload) {

            } else {
                Toast.makeText(getContext(), "Không tìm thấy TYPE payload", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Chua khoi tao payload hoac view model", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void initAndObserveViewModel(){
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        otpCodeViewModel = new ViewModelProvider(requireActivity()).get(OtpCodeViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);


        transactionPayload = transactionPayloadViewModel.getTxnPayload();

        // observe state transaction view model
        transactionViewModel.getResultState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                return;
            }
            if (result.getData() != null){
//                binding.lottieLoadingWaitingConfirm.setVisibility(View.GONE);
                binding.btnConfirmTransfer.setEnabled(true);

                // log transaction
                Log.d("TAG TRANSACTION", "Transaction created:" + result.getData().toString());

                // nếu đã thành công thì chuyên màn hình
                if (result.getData().getStatus() == TnxStatus.COMPLETED) {
                    safeNavigateToResult();
                } else if (result.getData().getStatus() == TnxStatus.FAILED){ // neu failed thi thong bao loi
                    showError("Lỗi tạo giao dịch", "Giao dịch thất bại");
                }
                return;
            }
            if (result.getError() != null) {
//                binding.lottieLoadingWaitingConfirm.setVisibility(View.GONE);
                binding.btnConfirmTransfer.setEnabled(true);

                showError("Lỗi tạo giao dịch", result.getError());
                return;
            }

            if (result.isLoading()) {
                binding.btnConfirmTransfer.setEnabled(false);

                ((MainActivity) requireActivity()).showLoadingFeature(true);
//                binding.lottieLoadingWaitingConfirm.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initEvents(){
        binding.btnConfirmTransfer.setOnClickListener(v -> {
            openPinDialog();
            // =========
            // Tạo giao dịch pending
            // =========
            //transactionViewModel.createTransaction(transactionPayload.getTransaction());


            // =========
            // Mở Dialog xac thuc: OTP, MFA, bio,... nếu thành công mới update status chuyển màn hình
            // =========

            // cu cho la da xac thuc giao dich
            //isVerified = true;
        });
    }

    private void openPinDialog() {
        String purpose = "TRANSACTION_" + transactionPayload.getTransaction().getType().name();
        User user;
        if (sessionViewModel != null && sessionViewModel.user.getValue() != null){
            user = sessionViewModel.user.getValue();
        }


        final PINFormFragment[] dialogHolder = new PINFormFragment[1];

        dialogHolder[0] = new PINFormFragment(new PINFormFragment.OnPinSubmitListener() {
            @Override
            public void onPinSubmit(String pin) {
                if (!pin.equals(transactionPayload.getSenderAccount().getPinCode())) {
                    dialogHolder[0].showPinError("Sai mã PIN!");
                    return;
                }

                // tạo giao dịch
                transactionViewModel.createTransaction(transactionPayload.getTransaction());

                // tạo và gửi OTP qua email
                otpCodeViewModel.createOtpCode(purpose, Objects.requireNonNull(sessionViewModel.user.getValue()));

                // mở modal OTP
                openOtpDialog();

                dialogHolder[0].dismiss();    // ✔ GIỜ DÙNG ĐƯỢC
            }

            @Override
            public void onPinCancel() {
                dialogHolder[0].dismiss();        // ✔ DÙNG ĐƯỢC
            }

            @Override
            public void onPinInvalid(String message) {
                dialogHolder[0].showPinError(message);
            }
        });

        dialogHolder[0].setCancelable(true);
        dialogHolder[0].show(getParentFragmentManager(), "PINDialog");
    }

    private void openOtpDialog() {
        String purpose = "TRANSACTION_" + transactionPayload.getTransaction().getType().name();
        String userId;
        if (sessionViewModel != null && sessionViewModel.user.getValue() != null){
            userId = sessionViewModel.user.getValue().getUserId();
        } else {
            userId = "unknown";
        }

        final OTPFormFragment[] dialogHolder = new OTPFormFragment[1];

        dialogHolder[0] = new OTPFormFragment(new OTPFormFragment.OnOtpSubmitListener() {
            @Override
            public void onOTPSubmit(String otp) {
                // call verify OTP
                if (otp.length() < 6) {
                    dialogHolder[0].showOTPError("OTP không hợp lệ");
                    return;
                }


                otpCodeViewModel.verifyOtpCode(userId, purpose, otp);
            }

            @Override
            public void onOTPCancel() {
                dialogHolder[0].dismiss();
            }

            @Override
            public void onOTPInvalid(String message) {
                dialogHolder[0].showOTPError(message);
            }
        });

        dialogHolder[0].setCancelable(true);
        dialogHolder[0].show(getParentFragmentManager(), "PINDialog");

        // OBSERVE OTP VERIFY — chỉ observe một lần
        otpCodeViewModel.getVerifyState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                ((MainActivity) requireActivity()).showLoadingFeature(true);
            }

            if (state.getError() != null) {
                dialogHolder[0].showOTPError(state.getError());
            }

            if (state.getData() != null && state.getData()) {
                dialogHolder[0].dismiss();

                // OTP đúng → Execute transaction
                transactionViewModel.executeTransaction(
                        transactionPayload.getTransaction(),
                        transactionPayload.getSenderAccount(),
                        sessionViewModel.user.getValue()
                );
            }
        });
    }


    // chuyen doi khi trang thai giao dich thanh cong
    private void safeNavigateToResult() {
        if (hasNavigated || !isAdded()) return;

        hasNavigated = true;

        // xóa state transaction VM
        transactionViewModel.clearResultState();

        new Handler(Looper.getMainLooper()).post(() -> {
            if (!isAdded()) return;

            // clear stack
            ((MainActivity) requireActivity()).clearBackStack();

            // chuyển sang màn hình kết quả
            ((MainActivity) requireActivity()).openFeatureFragment(
                    new TransactionResultFragment(),
                    getString(R.string.ket_qua_giao_dich)
            );
        });
    }


    // hiển thị lỗi khi tạo giao dịch
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}