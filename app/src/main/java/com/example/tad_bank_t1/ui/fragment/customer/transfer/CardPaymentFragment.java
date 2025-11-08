package com.example.tad_bank_t1.ui.fragment.customer.transfer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewmodel.AccountSharedViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;


public class CardPaymentFragment extends Fragment {

    private AccountSharedViewModel accountSharedViewModel;
    private SessionViewModel sessionViewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchJob;
    private String lastQuery = "";


    private TextView txtTransferAccNumber, txtTransferAccBalance;

    public CardPaymentFragment() {
        // Required empty public constructor
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    }

    public static CardPaymentFragment newInstance(String param1, String param2) {
        CardPaymentFragment fragment = new CardPaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }



        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.BOTTOM));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.TOP));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtTransferAccNumber = view.findViewById(R.id.txtTransferAccNumber);
        txtTransferAccBalance = view.findViewById(R.id.txtTransferAccBalance);

        // Khởi tạo ViewModel
        accountSharedViewModel = new ViewModelProvider(requireActivity()).get(AccountSharedViewModel.class);
        accountSharedViewModel.setDebitAccountNumberCurrent(txtTransferAccNumber.getText().toString().trim());

        sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                txtTransferAccNumber.setText(account.getAccountNumber());
                txtTransferAccBalance.setText(CurrencyUtil.formatVND(account.getBalance()) + " " + account.getCurrency());
            }
        });

        sessionViewModel.selectedAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                txtTransferAccNumber.setText(account.getAccountNumber());
                txtTransferAccBalance.setText(CurrencyUtil.formatVND(account.getBalance()) + " " + account.getCurrency());
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "CARD TRANSFER onstart");

    //        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}