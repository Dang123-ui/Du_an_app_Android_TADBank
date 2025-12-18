package com.example.tad_bank_t1.ui.fragment.customer.bills;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.SearchTransferInfomationFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.CardPaymentFragment;
import com.example.tad_bank_t1.util.TadConstants;
import com.example.tad_bank_t1.util.FragmentUtil;

public class ElectricBillPaymentFragment extends Fragment implements UiConfig {
    private ImageButton imbtShowListElectricityProvider;

    @Override
    public String getAppBarTitle() {
        return getString(R.string.thanh_toan_tien_dien);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_electric_bill_payment, container, false);



        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_electricity_bill,
                false);

        imbtShowListElectricityProvider = view.findViewById(R.id.imbtShowListElectricityProvider);

        imbtShowListElectricityProvider.setOnClickListener(v -> {
            SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                    "Chọn nhà cung cấp",
                    "Nhập nhà cung cấp",
                    "Danh sách nhà cung cấp",
                    TadConstants.SEARCH_ELECTRICITY_PROVIDER
            );
            FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                    getParentFragmentManager(),
                    R.id.fragment_container_search_electricity);
        });
        return view;
    }
}