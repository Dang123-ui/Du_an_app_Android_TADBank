package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.fake_data.TxnFakeData;
import com.example.tad_bank_t1.ui.viewadapter.TxnHistoryAdapter;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;


public class TransactionHistoryFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroupTxnHistory;
    private MaterialButton btnTxnHistoryAll, btnTxnHistoryIncoming, btnTxnHistoryOutgoing;
    private RecyclerView rvTxnHistory;
    private TxnHistoryAdapter txnHistoryAdapter;

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

        FragmentUtil.replaceFragment(new CardTransactionHistoryFragment(), getParentFragmentManager(),
                    R.id.fragment_card_transaction_history_container, false
                );

        toggleGroupTxnHistory = view.findViewById(R.id.toggleGroupTxnHistory);
        rvTxnHistory = view.findViewById(R.id.rvTxnHistory);

        txnHistoryAdapter = new TxnHistoryAdapter(TxnFakeData.getDataTxns());
        rvTxnHistory.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvTxnHistory.setAdapter(txnHistoryAdapter);


        toggleGroupTxnHistory.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    if(checkedId == R.id.btnTxnHistoryAll){
                        txnHistoryAdapter.setData(TxnFakeData.getDataTxns());
                    } else if (checkedId == R.id.btnTxnHistoryIncoming){
                        txnHistoryAdapter.setData(TxnFakeData.filter(true));
                    } else if (checkedId == R.id.btnTxnHistoryOutgoing) {
                        txnHistoryAdapter.setData(TxnFakeData.filter(false));
                    } else {
                        txnHistoryAdapter.setData(TxnFakeData.getDataTxns());
                    }
                }
            }
        });

        return view;
    }
}