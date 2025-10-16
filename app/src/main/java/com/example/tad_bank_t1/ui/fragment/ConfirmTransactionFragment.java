package com.example.tad_bank_t1.ui.fragment;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.dto.TransferInfoDTO;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.viewmodel.AccountSharedViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.FragmentUtil;


public class ConfirmTransactionFragment extends Fragment {
    private Button btnConfirmTransfer;
    private LottieAnimationView lottie_loading_waiting_confirm;
    private ConstraintLayout main_confirm_transaction;
    private View blurFullScreenConfirm;
    private TextView txtConfirmTransactionAccountSource, txtConfirmTransactionAccountReceiver,
            txtConfirmTransactionNameReceiver, txtConfirmTransactionBankReceiver,
            txtConfirmTransactionContent, txtConfirmTransactionFeeAmount, txtConfirmTransactionAmount;
    private ImageButton imbtConfirmTransMethod;
    private AccountSharedViewModel accountSharedViewModel;

    public static String ARG_DEBIT_ACCOUNT = "accSource";
    public static String ARG_ACC_RECEIVER = "accReceiver";
    public static String ARG_NAME_RECEIVER = "nameReceiver";
    public static String ARG_BANK_RECEIVER = "bankReceiver";
    public static String ARG_CONTENT = "content";
    public static String ARG_FEE = "fee";
    public static String ARG_AMOUNT = "amount";
    public static String ARG_METHOD = "method";

    private String debitAccount, accReceiver, nameReceiver, bankReceiver, content, method;

    private double amount, fee;


    public ConfirmTransactionFragment() {
        // Required empty public constructor
    }

    public static ConfirmTransactionFragment newInstance(TransferInfoDTO transferInfoDTO) {
        ConfirmTransactionFragment fragment = new ConfirmTransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEBIT_ACCOUNT, transferInfoDTO.getDebitAccount());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            debitAccount = getArguments().getString(ARG_DEBIT_ACCOUNT);
            accReceiver = getArguments().getString(ARG_ACC_RECEIVER);
            nameReceiver = getArguments().getString(ARG_NAME_RECEIVER);
            bankReceiver = getArguments().getString(ARG_BANK_RECEIVER);
            content = getArguments().getString(ARG_CONTENT);

            fee = getArguments().getDouble(ARG_FEE);
            amount = getArguments().getDouble(ARG_AMOUNT);
            method = getArguments().getString(ARG_METHOD);


        }


        accountSharedViewModel = new ViewModelProvider(requireActivity()).get(AccountSharedViewModel.class);

        // Quan sát dữ liệu thay đổi
        accountSharedViewModel.getAccountNumberCurrent().observe(this, newAccountNumber -> {
            // Cập nhật View khi dữ liệu thay đổi
            txtConfirmTransactionAccountSource.setText(newAccountNumber);
        });
        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.BOTTOM));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.TOP));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comfirm_transaction, container, false);



        btnConfirmTransfer = view.findViewById(R.id.btnConfirmTransfer);
        lottie_loading_waiting_confirm = view.findViewById(R.id.lottie_loading_waiting_confirm);
        main_confirm_transaction = view.findViewById(R.id.main_confirm_transaction);
        blurFullScreenConfirm = view.findViewById(R.id.blurFullScreenConfirm);

        txtConfirmTransactionAccountSource = view.findViewById(R.id.txtConfirmTransactionAccountSource);
        txtConfirmTransactionAccountReceiver = view.findViewById(R.id.txtConfirmTransactionAccountReceiver);
        txtConfirmTransactionNameReceiver = view.findViewById(R.id.txtConfirmTransactionNameReceiver);
        txtConfirmTransactionBankReceiver = view.findViewById(R.id.txtConfirmTransactionBankReceiver);
        txtConfirmTransactionContent = view.findViewById(R.id.txtConfirmTransactionContent);
        txtConfirmTransactionFeeAmount = view.findViewById(R.id.txtConfirmTransactionFeeAmount);
        txtConfirmTransactionAmount = view.findViewById(R.id.txtConfirmTransactionAmount);
        imbtConfirmTransMethod = view.findViewById(R.id.imbtConfirmTransMethod);


        txtConfirmTransactionAccountSource.setText(debitAccount);
        txtConfirmTransactionAccountReceiver.setText(accReceiver);
        txtConfirmTransactionNameReceiver.setText(nameReceiver);
        txtConfirmTransactionBankReceiver.setText(bankReceiver);
        txtConfirmTransactionContent.setText(content);

        txtConfirmTransactionFeeAmount.setText(CurrencyUtil.formatVND(fee));
        txtConfirmTransactionAmount.setText(CurrencyUtil.formatVND(amount));


        btnConfirmTransfer.setOnClickListener(v -> {
            // Disable the button to prevent multiple clicks
//            blurFullScreenConfirm.setEnabled(false);
            blurFullScreenConfirm.setVisibility(View.VISIBLE);
            // Show the Lottie animation
            lottie_loading_waiting_confirm.setVisibility(View.VISIBLE);

            // Simulate a task (e.g., API call) with a delay
            new Handler().postDelayed(() -> {
                blurFullScreenConfirm.setVisibility(View.GONE);
                // Show the Lottie animation
                lottie_loading_waiting_confirm.setVisibility(View.GONE);

                // You can now proceed with your next action, like replacing the fragment
                // FragmentUtil.replaceFragment(new CardPaymentFragment(), getParentFragmentManager(), R.id.fragment_card_electricity_bill, false);

            }, 3000); // 2-second delay
        });
//        FragmentUtil.replaceFragment(new CardPaymentFragment(),
//                getParentFragmentManager(),
//                R.id.fragment_card_electricity_bill,
//                false);

        return view;
    }
}