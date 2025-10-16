package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewadapter.VPNotiAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class NotiFragment extends Fragment {



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
        View view = inflater.inflate(R.layout.fragment_noti, container, false);

        TabLayout tabNoti = view.findViewById(R.id.tloNoti);
        ViewPager2 vpNoti = view.findViewById(R.id.vpNoti);

        VPNotiAdapter vpNotiAdapter = new VPNotiAdapter(this);
        vpNoti.setAdapter(vpNotiAdapter);

        new TabLayoutMediator(tabNoti, vpNoti, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Biến động");
                    break;
                case 1:
                    tab.setText("Thông báo");
                    break;
            }
        }).attach();

        return view;
    }
}