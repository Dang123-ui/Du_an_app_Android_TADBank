package com.example.tad_bank_t1.ui.fragment.customer.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.base.UiConfig;

public class SettingFragment extends Fragment implements UiConfig {




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "SETTING onstart");
//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TAG", "SETTING onstop");

        // Show the bottom navigation bar when the user leaves this fragment
//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }


    @Override
    public String getAppBarTitle() {
        return getString(R.string.cai_dat_nav);
    }

}