package com.example.tad_bank_t1.ui.fragment.customer.transaction;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.DialogFragment;
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
import com.example.tad_bank_t1.data.model.Payment;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentRes;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.example.tad_bank_t1.databinding.FragmentTransactionComfirmBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.otp.OTPFormFragment;
import com.example.tad_bank_t1.ui.form.otp.PINFormFragment;
import com.example.tad_bank_t1.ui.form.payload.transactions.BaseTransactionPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.BillPaymentPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.PhoneTopupPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.ui.fragment.FaceVerify1Fragment;
import com.example.tad_bank_t1.ui.fragment.customer.payment.VnpayWebViewFragment;
import com.example.tad_bank_t1.ui.viewmodel.OtpCodeViewModel;
import com.example.tad_bank_t1.ui.viewmodel.PaymentReturnViewModel;
import com.example.tad_bank_t1.ui.viewmodel.PaymentViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.TadConstants;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;


public class TransactionConfirmFragment extends Fragment implements UiConfig {
    public interface TransactionConfirmCallback{
        void onTransactionSuccess();
        void onTransactionFailed();
    }
    private TransactionConfirmCallback callback;

    // View binding
    private FragmentTransactionComfirmBinding binding;

    // View model
    private SessionViewModel sessionViewModel;
    private TransactionViewModel transactionViewModel;
    private PaymentViewModel paymentViewModel;
    private OtpCodeViewModel otpCodeViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;
    private PaymentReturnViewModel paymentReturnVM;

    // payload từ fragment trước
    private BaseTransactionPayload transactionPayload;

    // check giao dịch đã xác thực hay chưa
    private boolean otpHandled = false;


    private boolean hasNavigated = false;
    private boolean navigated = false;


    public TransactionConfirmFragment() {
        // Required empty public constructor
    }

    public TransactionConfirmFragment (TransactionConfirmCallback callback){
        this.callback = callback;
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
                // hien view can thiet
                binding.lnloConfirmTrans3TenNguoiNhan.setVisibility(View.VISIBLE);
                binding.lnloConfirmTrans4NganHangNhanCk.setVisibility(View.VISIBLE);

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

            }
            else if(transactionPayload instanceof PhoneTopupPayload) {
                // an view khong can thiet
                binding.lnloConfirmTrans3TenNguoiNhan.setVisibility(View.GONE);
                binding.lnloConfirmTrans4NganHangNhanCk.setVisibility(View.GONE);

                // =========
                // lay payload
                // =========
                Log.d("PAYLOAD : ", ((PhoneTopupPayload) transactionPayload).getTransaction().toString());
                PhoneTopupPayload txnPayload = (PhoneTopupPayload) transactionPayload;
                Transaction txn = txnPayload.getTransaction();

                // bind view chung

                binding.txtConfirmTransactionAccountSource.setText(txn.getAccountNumber());
                binding.txtConfirmTransactionAccountReceiver.setText(txn.getCounterpartyAccount());
                binding.txtConfirmTransactionContent.setText(txn.getDescription());
                binding.txtConfirmTransactionFeeAmount.setText(CurrencyUtil.formatVND(txn.getFeeAmount()));
                binding.txtConfirmTransactionAmount.setText(CurrencyUtil.formatVND(txn.getAmount()));

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
        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
        paymentReturnVM = new ViewModelProvider(requireActivity()).get(PaymentReturnViewModel.class);

        transactionPayload = transactionPayloadViewModel.getTxnPayload();

        transactionViewModel.getResultState().observe(getViewLifecycleOwner(),
                rs -> handleTxnResult(rs, "Lỗi tạo giao dịch"));

        transactionViewModel.getListenerState().observe(getViewLifecycleOwner(),
                rs -> handleTxnResult(rs, "Lỗi xác nhận thanh toán (VNPay)"));

        paymentViewModel.getCreateState().observe(getViewLifecycleOwner(), rs -> {
            if (rs == null) return;

            if (rs.isLoading()) {
                ((MainActivity) requireActivity()).showLoadingFeature(true);
                return;
            }

            ((MainActivity) requireActivity()).showLoadingFeature(false);

            if (rs.getError() != null) {
                showError("Lỗi tạo VNPay", rs.getError());
                cancelPendingTransactionSafely();
                return;
            }

            CreatePaymentRes dataCreatePayment = rs.getData();
            if (dataCreatePayment != null && dataCreatePayment.paymentUrl != null) {
                if (dataCreatePayment.transactionId != null) transactionViewModel.listenTransactionStatus(dataCreatePayment.transactionId);
//                openVnpayCustomTab(dataCreatePayment.paymentUrl);
                openVnpayWebView(dataCreatePayment);
            }
        });

        paymentReturnVM.getReturnUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri == null) return;

            String transactionId = uri.getQueryParameter("transactionId");
            String code = uri.getQueryParameter("code");

            if ("00".equals(code) && transactionId != null && !transactionId.isEmpty()) {
                // ✅ backend đã update transaction/payment rồi -> app chỉ listen
                transactionViewModel.listenTransactionStatus(transactionId);
            } else {
                showError("Thanh toán thất bại", "VNPAY code=" + code);
            }
        });

    }

    private void initEvents(){
        binding.btnConfirmTransfer.setOnClickListener(v -> {
            openPinDialog();
        });
    }

    private void openVnpayWebView(CreatePaymentRes createPaymentRes){
        // ✅ mở WebView Fragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_customer,
                        VnpayWebViewFragment.newInstance(createPaymentRes.paymentUrl, createPaymentRes.transactionId))
                .addToBackStack("VNPAY_WEBVIEW")
                .commit();
    }
    private void openVnpayCustomTab(String url) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        intent.launchUrl(requireContext(), Uri.parse(url));
    }


    // nhap ma PIN de xac thuc chu tai khoan
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
                // tạo và gửi OTP qua email
                otpCodeViewModel.createOtpCode(purpose, Objects.requireNonNull(sessionViewModel.user.getValue()));

                // tạo giao dịch
                transactionViewModel.createTransaction(transactionPayload.getTransaction());


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


    // mo OTP dialog khi PIN thanh cong
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
                // 1️⃣ tắt loading ngay
                if (isAdded()) {
                    ((MainActivity) requireActivity()).showLoadingFeature(false);
                }

                // 2️⃣ reset verify state để observer không bắn lại
                otpCodeViewModel.clearVerifyState();

                // 3️⃣ huỷ giao dịch
                cancelPendingTransactionSafely();
            }


            @Override
            public void onOTPInvalid(String message) {
                dialogHolder[0].showOTPError(message);
            }

            @Override
            public void onOTPResend() {
                otpCodeViewModel.createOtpCode(purpose, Objects.requireNonNull(sessionViewModel.user.getValue()));
            }
        });

        dialogHolder[0].setCancelable(true);
        dialogHolder[0].show(getParentFragmentManager(), "OTPDialog");

        otpCodeViewModel.getVerifyState().observe(getViewLifecycleOwner(), state -> {
            if (state == null || otpHandled) return;

            if (state.isLoading()) return;

            if (state.getError() != null) {
                dialogHolder[0].showOTPError(state.getError());
                return;
            }

            if (Boolean.TRUE.equals(state.getData())) {
                otpHandled = true; // 🔥 chặn xử lý lặp

                dialogHolder[0].setVerified(true);
                dialogHolder[0].dismissAllowingStateLoss();

                handleAfterOtpSuccess();
            }
        });

    }

    // dong otp dialog
    private void closeOtpDialog(){
        // 🔥 ÉP đóng OTP dialog nếu còn
        Fragment otpDialog =
                getParentFragmentManager().findFragmentByTag("OTPDialog");
        if (otpDialog instanceof DialogFragment) {
            ((DialogFragment) otpDialog).dismissAllowingStateLoss();
        }
    }

    // xu ky sau khi OTP thanh cong
    private void handleAfterOtpSuccess() {
        Transaction txn = transactionPayload.getTransaction();

        Runnable afterAllVerified = () -> {
            // ✅ Nếu là VNPay -> tạo payment + mở cổng
            if (txn.getChannel() == TxnChannel.VN_PAY) {
                paymentViewModel.createVnpayPayment(txn, Objects.requireNonNull(sessionViewModel.user.getValue()), null);
                return;
            }

            // ✅ Còn lại -> chạy flow cũ (trực tiếp trong app)
            transactionViewModel.executeTransaction(
                    txn,
                    transactionPayload.getSenderAccount(),
                    sessionViewModel.user.getValue()
            );
        };

        if (txn.getAmount() >= TadConstants.LIMIT_NEED_VERIFY_AMOUNT) {
            closeOtpDialog();

            FaceVerify1Fragment faceFragment =
                    FaceVerify1Fragment.newForTransaction(
                            sessionViewModel.user.getValue().getUserId(),
                            new FaceVerify1Fragment.FaceVerifyCallback() {
                                @Override public void onFaceVerified() {
                                    afterAllVerified.run();
                                }

                                @Override public void onFaceFailed(String reason) {
                                    cancelPendingTransactionSafely();
                                }
                            }
                    );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_customer, faceFragment)
                    .addToBackStack("FACE_VERIFY_TXN")
                    .commit();
        } else {
            afterAllVerified.run();
        }
    }


    // huy giao dich
    private void cancelPendingTransactionSafely() {
        otpHandled = false;                 // ✅ reset OTP flow

        if (isAdded()) {
            ((MainActivity) requireActivity()).showLoadingFeature(false);
        }

        transactionViewModel.clearResultState();  // ✅

        Transaction txn = transactionPayload.getTransaction();
        if (txn != null && txn.getTransactionId() != null) {
            transactionViewModel.updateTransactionStatus(txn.getTransactionId(), TnxStatus.CANCELLED);
        }
    }



    private void handleTxnResult(ResultWrapper<Transaction> result, String contextMsg) {
        if (result == null) return;

        if (result.isLoading()) {
            binding.btnConfirmTransfer.setEnabled(false);
            ((MainActivity) requireActivity()).showLoadingFeature(true);
            return;
        }

        binding.btnConfirmTransfer.setEnabled(true);
        ((MainActivity) requireActivity()).showLoadingFeature(false);

        if (result.getError() != null) {
            showError(contextMsg, result.getError());
            return;
        }

        Transaction txn = result.getData();
        if (txn == null) return;

        if (txn.getStatus() == TnxStatus.COMPLETED && !navigated) {
            if (txn.getChannel() == TxnChannel.VN_PAY){
                // ✅ chạy side-effects (KHÔNG executeTransaction)
                transactionViewModel.runPostSuccessActionsOnce(
                        txn,
                        transactionPayload.getSenderAccount(),
                        sessionViewModel.user.getValue()
                );
            }
            // than cong gọi cal back
            if (callback != null) {
                callback.onTransactionSuccess();
            }
            navigated = true;
            safeNavigateToResult();
        } else if (txn.getStatus() == TnxStatus.FAILED) {
            showError(contextMsg, "Giao dịch thất bại");
            if (callback != null) {
                callback.onTransactionFailed();
            }
        }
    }


    // chuyen doi khi trang thai giao dich thanh cong
    private void safeNavigateToResult() {
        if (hasNavigated || !isAdded()) return;

        hasNavigated = true;

        // dong top
        closeOtpDialog();

        // clear state
        otpCodeViewModel.clearVerifyState();

        // reset send mail va noti app khi vn pay
        transactionViewModel.resetPostSuccessFlag();


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
        transactionViewModel.stopListenTransactionStatus();
    }


    // util observer
    private <T> void observeOnce(
            androidx.lifecycle.LiveData<T> liveData,
            androidx.lifecycle.LifecycleOwner owner,
            androidx.lifecycle.Observer<T> observer
    ) {
        liveData.observe(owner, new androidx.lifecycle.Observer<T>() {
            @Override
            public void onChanged(T t) {
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }

    private interface FaceVerifyCallback {
        void onSuccess();
    }
}