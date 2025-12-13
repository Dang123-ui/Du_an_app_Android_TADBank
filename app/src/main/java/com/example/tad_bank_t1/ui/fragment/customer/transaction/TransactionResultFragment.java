package com.example.tad_bank_t1.ui.fragment.customer.transaction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.databinding.FragmentTransactionResultBinding;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;

public class TransactionResultFragment extends Fragment implements UiConfig {
    // View binding
    private FragmentTransactionResultBinding binding;

    // View model
    private TransactionPayloadViewModel transactionPayloadViewModel;

    // declare private variable
    private Transaction transaction;
    private Bank bank;

    @Override
    public boolean showAppBar() {
        return false;
    }

    @Override
    public boolean showBottomNav() {
        return false;
    }

    @Override
    public String getAppBarTitle() {
        return getString(R.string.ket_qua_giao_dich);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        // Transition khi Fragment mới xuất hiện (Enter)
//        setEnterTransition(new Slide(Gravity.BOTTOM));
//
//        // Transition khi Fragment hiện tại biến mất (Exit)
//        setExitTransition(new Slide(Gravity.TOP));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionResultBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAndObserverVM();

        setUpEvent();
    }

    private void initAndObserverVM(){
//        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);

        Transaction txn = transactionPayloadViewModel.getTxnPayload().getTransaction();
        updateUI(txn);
    }
    private void updateUI(Transaction txn){
        binding.txtTransactionResultAmount.setText(CurrencyUtil.formatVND(txn.getAmount()));
        binding.txtTransactionResultTime.setText(DateTimeUtil.formatDateToVNTime(txn.getCreatedAt()));

        binding.txtTransactionResultFeeAmount.setText(CurrencyUtil.formatVND(txn.getFeeAmount()));
        binding.txtTransactionResultSourceAccount.setText(txn.getAccountNumber());
        binding.txtTransactionResultTargetAccount.setText(txn.getCounterpartyAccount());
        binding.txtTransactionResultTargetAccountName.setText(txn.getCounterpartyName());
        binding.txtTransactionResultTargetBank.setText(txn.getCounterpartyBankCode());
        binding.txtTransactionResultContent.setText(txn.getDescription());
        binding.txtTransactionResultTransactionCode.setText(txn.getTransactionReference());
        binding.txtTransactionResultType.setText(txn.getType().toString());
    }


    private void setUpEvent(){
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

