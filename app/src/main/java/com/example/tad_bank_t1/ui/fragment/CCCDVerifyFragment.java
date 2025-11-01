package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CCCDVerifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CCCDVerifyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID = "uid";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_EMAIL = "email";

    // TODO: Rename and change types of parameters
    private String uid, phone, username, email;
    public CCCDVerifyFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CCCDVerifyFragment newInstance(String uid, String phone, String username, String email) {
        CCCDVerifyFragment fragment = new CCCDVerifyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            phone = getArguments().getString(ARG_PHONE);
            username = getArguments().getString(ARG_USERNAME);
            email = getArguments().getString(ARG_EMAIL);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_c_c_c_d_verify, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SignUpActivity) requireActivity()).setHeaderBackEnabled(false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        Button btnNext = view.findViewById(R.id.btnTakeThePicture);
        btnNext.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("uid", uid);
            args.putString("phone", phone);
            args.putString("username", username);
            args.putString("email", email);
            TakePhotoFragment next = new TakePhotoFragment();
            next.setArguments(args);
            ((SignUpActivity) requireActivity()).navigateTo(next, true);
        });

    }
}