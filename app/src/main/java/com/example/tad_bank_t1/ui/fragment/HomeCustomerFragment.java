package com.example.tad_bank_t1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.LoginActivity;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.util.FragmentUtil;

public class HomeCustomerFragment extends Fragment {
    private LinearLayout lnloCardDepositPhone,
            lnloCardTranfer, lnloCardBillPayment,
            lnloHomeHistoryTransac, lnloHomeAccManagement,
            lnloCardFindBranch
    ;
    private ImageButton imbtHomeNotify, imbtLogout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_home_customer, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        imbtHomeNotify = view.findViewById(R.id.imbtHomeNotify);
        imbtLogout = view.findViewById(R.id.imbtLogout);
        lnloHomeHistoryTransac = view.findViewById(R.id.lnloHomeHistoryTransac);
        lnloHomeAccManagement = view.findViewById(R.id.lnloHomeAccManagement);

        // layout features card
        lnloCardFindBranch = view.findViewById(R.id.lnloCardFindBranch);
        lnloCardDepositPhone = view.findViewById(R.id.lnloCardDepositPhone);
        lnloCardTranfer = view.findViewById(R.id.lnloCardTranfer);
        lnloCardBillPayment = view.findViewById(R.id.lnloCardBillPayment);


        imbtHomeNotify.setOnClickListener(v -> featureCardOnClick(new NotiFragment(), getString(R.string.thong_bao)));
        imbtLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // on click listener for features card
        lnloCardFindBranch.setOnClickListener(v -> featureCardOnClick(new MapBranchFragment(), getString(R.string.tim_kiem_chi_nhanh)));
        lnloHomeAccManagement.setOnClickListener(v -> featureCardOnClick(new AccountListFragment(), getString(R.string.danh_sach_tai_khoan)));
        lnloHomeHistoryTransac.setOnClickListener(v -> featureCardOnClick(new TransactionHistoryFragment(), getString(R.string.tai_khoan_hien_tai)));
        lnloCardDepositPhone.setOnClickListener(v -> featureCardOnClick(new MobileTopupTransferFragment(), getString(R.string.nap_tien_dien_thoai)));
        lnloCardTranfer.setOnClickListener(v-> featureCardOnClick(new BankTransferFragment(), getString(R.string.chuyen_tien)));
        lnloCardBillPayment.setOnClickListener(v-> featureCardOnClick(new BillsPaymentFragment(), getString(R.string.thanh_toan_hoa_don)));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "HOME onstart");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "HOME onstop");

//         ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    public void featureCardOnClick(Fragment fragment, String title){
        ((MainActivity) requireActivity())
                .openFeatureFragment(fragment, title);
    }
}