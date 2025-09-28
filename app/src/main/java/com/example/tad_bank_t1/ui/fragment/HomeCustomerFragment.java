package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.MainActivity;

public class HomeCustomerFragment extends Fragment {


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
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "HOME onstart");

        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "HOME onstop");

         ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }
}