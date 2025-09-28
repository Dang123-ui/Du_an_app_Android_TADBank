package com.example.tad_bank_t1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.util.FragmentUtil;


public class BankTransferFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank_transfer, container, false);


        FragmentUtil.replaceFragment(HeaderActionFragment.newInstance(getResources().getString(R.string.chuyen_tien)),
                getParentFragmentManager(),
                R.id.fragment_action_title,
                false);
        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_transfer,
                false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "BANK TRANSFER onstart");

        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "BANK TRANSFER onstop");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }
}