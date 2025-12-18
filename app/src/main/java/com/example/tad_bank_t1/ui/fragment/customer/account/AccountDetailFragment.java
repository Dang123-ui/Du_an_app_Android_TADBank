package com.example.tad_bank_t1.ui.fragment.customer.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;

import java.util.Date;

public class AccountDetailFragment extends Fragment implements UiConfig {

    private SessionViewModel sessionViewModel;

    public AccountDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean showBottomNav() {
        return false;
    }

    @Override
    public boolean showAppBar() {
        return true;
    }

    @Override
    public String getAppBarTitle() {
        return getString(R.string.chi_tiet_tai_khoan);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // viewmodel init
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);

        // UI init
        sessionViewModel.selectedAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null){
                initUI(view, account);
                return;
            }
            sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), accountDef -> {
                if (accountDef != null){
                    initUI(view, accountDef);
                }
            });
        });

    }

    public void initUI(View view, Account account){
        TextView txtAccountDetailAccName = view.findViewById(R.id.txtAccountDetailAccName);
        TextView txtAccountDetailAccNumber = view.findViewById(R.id.txtAccountDetailAccNumber);
        TextView txtAccountDetailCurrentBalance = view.findViewById(R.id.txtAccountDetailCurrentBalance);
        TextView txtAccountDetailInterestRate = view.findViewById(R.id.txtAccountDetailInterestRate);
        TextView txtAccountDetailOpenDate = view.findViewById(R.id.txtAccountDetailOpenDate);
        TextView txtAccountDetailDateOfLastTxn = view.findViewById(R.id.txtAccountDetailDateOfLastTxn);

        txtAccountDetailAccName.setText(account.getAccountName());
        txtAccountDetailAccNumber.setText(account.getAccountNumber());
        txtAccountDetailCurrentBalance.setText(CurrencyUtil.formatAmount(account.getBalance()) + " " + account.getCurrency());
        txtAccountDetailInterestRate.setText(0.0 + "%");
        txtAccountDetailOpenDate.setText(formatDate(account.getCreatedAt()));
//        txtAccountDetailDateOfLastTxn.setText(account.getDateOfLastTxn());
    }

    private String formatDate(Date date){
        return DateTimeUtil.localDateTimeToStr(date);
    }
}