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
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.databinding.FragmentTransactionComfirmBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.payload.transactions.BaseTransactionPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.BillPaymentPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.PhoneTopupPayload;
import com.example.tad_bank_t1.ui.form.payload.transactions.TransferPayload;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class TransactionConfirmFragment extends Fragment implements UiConfig {
    // View binding
    private FragmentTransactionComfirmBinding binding;

    // View model
    private SessionViewModel sessionViewModel;
    private TransactionViewModel transactionViewModel;
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
                } else if (result.getData().getStatus() == TnxStatus.PENDING){ // neu pending thi mo dialog xac thuc
                    if (isVerified) {
                        // cap nhat trang thai giao dich
                        transactionViewModel.executeTransaction(result.getData(), transactionPayload.getSenderAccount(), sessionViewModel.user.getValue());

                    } else {
                        Toast.makeText(getContext(), "Chưa xác thực giao dịch", Toast.LENGTH_SHORT).show();
                    }
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
            } else {
//                binding.lottieLoadingWaitingConfirm.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).showLoadingFeature(true);

            }
        });
    }

    private void initEvents(){
        binding.btnConfirmTransfer.setOnClickListener(v -> {
            // =========
            // Tạo giao dịch pending
            // =========
            transactionViewModel.createTransaction(transactionPayload.getTransaction());


            // =========
            // Mở Dialog xac thuc: OTP, MFA, bio,... nếu thành công mới update status chuyển màn hình
            // =========
            // cu cho la da xac thuc giao dich
            isVerified = true;
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