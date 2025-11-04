package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.session.SessionManager;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.viewadapter.AccountListAdapter;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;


public class AccountListFragment extends Fragment {
    private AccountType accountType = AccountType.CHECKING;
    private AccountListAdapter accountListAdapter;
    private RecyclerView rvAccountList;
    private SessionViewModel sessionViewModel;
    private TransactionViewModel transactionViewModel;
    private List<Account> accounts;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAccountList = view.findViewById(R.id.rvAccList);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleGroupAccList);


        // init viewmodel
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);


        accountListAdapter = new AccountListAdapter(List.of());
        accountListAdapter.setOnclickAccountListener(new AccountListAdapter.OnclickAccountListener() {
            @Override
            public void onClickOpenAccountTxnHistory(Account account) {
                sessionViewModel.setSelectedAccount(account);
                transactionViewModel.setSelectedAccountId(account.getAccountId());
                ((MainActivity) requireActivity())
                        .openFeatureFragment(new TransactionHistoryFragment(), getString(R.string.lich_su_giao_dich));
            }
        });

        // session view model
        sessionViewModel.accounts.observe(getViewLifecycleOwner(), accounts -> {
            this.accounts = accounts;
            accountListAdapter.setData(getAccountsByType(accountType));
            rvAccountList.setAdapter(accountListAdapter);
            rvAccountList.setLayoutManager(new LinearLayoutManager(getContext()));
        });


        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int id = toggleGroup.getCheckedButtonId();

                if (id == R.id.btnAccListChecking) {
                    filterAccountsByType(AccountType.CHECKING);
                }
                if (id == R.id.btnAccListSaving) {
                    filterAccountsByType(AccountType.SAVING);
                }
                if (id == R.id.btnAccListMortgage) {
                    filterAccountsByType(AccountType.MORTGAGE);
                }
            }
        });
    }

    private void filterAccountsByType(AccountType accountType) {
        accountListAdapter.setData(getAccountsByType(accountType));
    }

    private List<Account> getAccountsByType(AccountType accountType) {
        return accounts
                .stream()
                .filter(account -> account.getType().equals(accountType)).toList();
    }
}