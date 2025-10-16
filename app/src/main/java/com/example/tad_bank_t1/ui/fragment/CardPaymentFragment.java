package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.viewmodel.AccountSharedViewModel;


public class CardPaymentFragment extends Fragment {

    private AccountSharedViewModel accountSharedViewModel;
    private TextView txtTransferAccNumber;

    public static CardPaymentFragment newInstance(String param1, String param2) {
        CardPaymentFragment fragment = new CardPaymentFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);
        txtTransferAccNumber = view.findViewById(R.id.txtTransferAccNumber);

        // Khởi tạo ViewModel
        accountSharedViewModel = new ViewModelProvider(requireActivity()).get(AccountSharedViewModel.class);
        accountSharedViewModel.setDebitAccountNumberCurrent(txtTransferAccNumber.getText().toString().trim());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "CARD TRANSFER onstart");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "CARD TRANSFER onstop");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }
}