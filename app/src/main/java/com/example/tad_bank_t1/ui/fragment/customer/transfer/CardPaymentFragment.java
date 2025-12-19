package com.example.tad_bank_t1.ui.fragment.customer.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.ui.fragment.customer.account.ChoosePayAccountBottomSheet;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;

public class CardPaymentFragment extends Fragment {

    private SessionViewModel sessionViewModel;

    private TextView txtTransferAccNumber, txtTransferAccBalance;
    private View cardRoot;
    private ImageButton btnOpenBottomSheet;

    public CardPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ Share chung session VM với Transfer/Bill + BottomSheet
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);

        // ids theo XML bạn gửi
        cardRoot = view.findViewById(R.id.cviewTransferCardInfo);
        btnOpenBottomSheet = view.findViewById(R.id.imbtOpenBottomSheetChoosePay);
        txtTransferAccNumber = view.findViewById(R.id.txtTransferAccNumber);
        txtTransferAccBalance = view.findViewById(R.id.txtTransferAccBalance);

        // ✅ bấm card hoặc icon đều mở bottomsheet
        View.OnClickListener openPicker = v ->
                new ChoosePayAccountBottomSheet().show(getParentFragmentManager(), "ChoosePayAcc");

        if (cardRoot != null) cardRoot.setOnClickListener(openPicker);
        if (btnOpenBottomSheet != null) btnOpenBottomSheet.setOnClickListener(openPicker);

        // ✅ CHỈ observe payAccount (đừng observe defaultAccount nữa để tránh update 2 lần)
        sessionViewModel.payAccount.observe(getViewLifecycleOwner(), this::bindAccount);
    }

    private void bindAccount(Account account) {
        if (account == null) {
            txtTransferAccNumber.setText("—");
            txtTransferAccBalance.setText("—");
            return;
        }

        txtTransferAccNumber.setText(account.getAccountNumber());
        txtTransferAccBalance.setText(
                CurrencyUtil.formatAmount(account.getBalance()) + " " + account.getCurrency()
        );
    }
}
