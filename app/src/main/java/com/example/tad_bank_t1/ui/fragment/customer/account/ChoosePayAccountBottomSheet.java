package com.example.tad_bank_t1.ui.fragment.customer.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewadapter.PayAccountPickAdapter;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChoosePayAccountBottomSheet extends BottomSheetDialogFragment {

    private SessionViewModel sessionVM;
    private PayAccountPickAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_choose_account, container, false);

        // ✅ QUAN TRỌNG: requireActivity() để share chung VM với màn Payment
        sessionVM = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);

        RecyclerView rv = view.findViewById(R.id.rvBottomSheetAccounts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PayAccountPickAdapter(acc -> {
            sessionVM.setSelectedAccount(acc); // ✅ update account thanh toán
            dismiss();
        });

        rv.setAdapter(adapter);

        // list account
        sessionVM.accounts.observe(getViewLifecycleOwner(), adapter::setData);

        // highlight account đang dùng để thanh toán
        sessionVM.payAccount.observe(getViewLifecycleOwner(), adapter::setSelected);
        // hoặc nếu bạn làm "payAccount" (fallback default) thì observe payAccount

        return view;
    }
}

