package com.example.tad_bank_t1.ui.fragment.customer.transaction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
//import com.example.tad_bank_t1.data.fake_data.TxnFakeData;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewadapter.TxnHistoryAdapter;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;
import java.util.stream.Collectors;


public class TransactionHistoryFragment extends Fragment implements UiConfig {
    private MaterialButtonToggleGroup toggleGroupTxnHistory;
    private MaterialButton btnTxnHistoryAll, btnTxnHistoryIncoming, btnTxnHistoryOutgoing;
    private RecyclerView rvTxnHistory;
    private List<Transaction> txns;
    private TxnHistoryAdapter txnHistoryAdapter;
    // loading
    private LottieAnimationView lottie_loading_txn_history;
    // viewmodel
    private SessionViewModel sessionViewModel;
    private TransactionViewModel transactionViewModel;


    @Override
    public String getAppBarTitle() {
        return getString(R.string.lich_su_giao_dich);
    }

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
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        initView(view);

        initVM();

        // card
        FragmentUtil.replaceFragment(new CardTransactionHistoryFragment(), getParentFragmentManager(),
                R.id.fragment_card_transaction_history_container, false
        );

        return view;
    }

    private void initView(View view){


        toggleGroupTxnHistory = view.findViewById(R.id.toggleGroupTxnHistory);
        lottie_loading_txn_history = view.findViewById(R.id.lottie_loading_txn_history);
        rvTxnHistory = view.findViewById(R.id.rvTxnHistory);


        // onclick toggle button
        toggleGroupTxnHistory.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (txns == null) return; // tranh null khi chua load
                    if (checkedId == R.id.btnTxnHistoryAll) {
                        txnHistoryAdapter.setData(txns);
                    } else if (checkedId == R.id.btnTxnHistoryIncoming) {
                        txnHistoryAdapter.setData(filterTxns(true));
                    } else if (checkedId == R.id.btnTxnHistoryOutgoing) {
                        txnHistoryAdapter.setData(filterTxns(false));
                    } else {
//                        txnHistoryAdapter.setData(TxnFakeData.getDataTxns());
                    }
                }
            }
        });

        // recycler vỉew
        txnHistoryAdapter = new TxnHistoryAdapter(List.of());
        rvTxnHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTxnHistory.setAdapter(txnHistoryAdapter);



        // listener item
        txnHistoryAdapter.setOnItemClickListener(new TxnHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction txn) {
                transactionViewModel.selectTransaction(txn);
                ((MainActivity) requireActivity()).openFeatureFragment(
                            new TransactionDetailFragment(),
                            getString(R.string.chi_tiet_gd)
                );
            }
        });
    }

    private void initVM(){
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // 1. Luôn ưu tiên load theo selectedAccountId
        transactionViewModel.getSelectedAccountId().observe(getViewLifecycleOwner(), accountId -> {
            if (accountId != null) {
                transactionViewModel.loadTransactions(accountId);
            }
        });

        // 2. Khi defaultAccount có → nhưng selectedAccount chưa có → load theo account của user
        sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null && transactionViewModel.getSelectedAccountId().getValue() == null) {
                transactionViewModel.loadTransactions(account.getAccountId());
            }
        });

        // 3. Nhận kết quả trả về từ Firestore
        transactionViewModel.transactions.observe(getViewLifecycleOwner(), txns -> {
            this.txns = txns;
            txnHistoryAdapter.setData(txns);
        });

        // 4. Loading indicator
        transactionViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            lottie_loading_txn_history.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            rvTxnHistory.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
    }

    public List<Transaction> filterTxns(boolean isIncoming) {
        if (isIncoming) {
            return txns.stream().filter(TransactionUtil::isIncoming).
                    collect(Collectors.toList());
        }
        return txns.stream().filter(txn -> !TransactionUtil.isIncoming(txn)).
                collect(Collectors.toList());
    }
}