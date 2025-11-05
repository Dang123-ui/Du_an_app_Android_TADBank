package com.example.tad_bank_t1.ui.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Ekyc;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;

public class FaceVerifyFragment extends Fragment {
    private static final String ARG_EKYC = "key_ekyc";
    private static final String ARG_UID = "key_uid";
    private static final String ARG_PHONE = "key_phone";
    private static final String ARG_USERNAME = "key_username";
    private static final String ARG_EMAIL = "key_email";
    private Ekyc ekyc;
    private String uid, phone, username, email;
    private Button btnBatDauChup;
    public FaceVerifyFragment() {
        // Required empty public constructor
    }

    public static FaceVerifyFragment newInstance(Ekyc ekyc, String uid, String phone, String username, String email) {
        FaceVerifyFragment fragment = new FaceVerifyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EKYC, ekyc);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Cách mới an toàn cho Android 13+
                ekyc = getArguments().getSerializable(ARG_EKYC, Ekyc.class);
            } else {
                // Cách cũ
                ekyc = (Ekyc) getArguments().getSerializable(ARG_EKYC);
            }

            // Lấy các chuỗi
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
        return inflater.inflate(R.layout.fragment_face_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBatDauChup = view.findViewById(R.id.btnBatDauChup);
        btnBatDauChup.setOnClickListener(v -> {
            FaceVerify1Fragment faceVerify1Fragment = FaceVerify1Fragment.newInstance(ekyc, uid, phone, username, email);
            if(getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).navigateTo(faceVerify1Fragment, true);
            }
        });
    }
}