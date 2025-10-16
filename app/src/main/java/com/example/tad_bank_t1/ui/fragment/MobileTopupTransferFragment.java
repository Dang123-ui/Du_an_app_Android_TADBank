package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.util.Constants;
import com.example.tad_bank_t1.util.FragmentUtil;

public class MobileTopupTransferFragment extends Fragment {
    private ImageButton imbtShowListBeneficiaryTopUp;

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
        View view = inflater.inflate(R.layout.fragment_mobile_topup_transfer, container, false);


        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_topup,
                false);

        imbtShowListBeneficiaryTopUp = view.findViewById(R.id.imbtShowListBeneficiaryTopUp);

        imbtShowListBeneficiaryTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                        "Chọn số điện thoại thụ hưởng",
                        "Nhập số điện thoại",
                        "Danh sách thụ hưởng",
                        Constants.SEARCH_BENEFICIARY_PHONE
                );
                FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                        getParentFragmentManager(),
                        R.id.fragment_container_search_topup);
            }

        });

        return view;
    }
}