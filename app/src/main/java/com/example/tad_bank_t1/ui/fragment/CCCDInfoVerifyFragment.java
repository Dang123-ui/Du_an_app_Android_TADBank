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
import com.google.android.material.textfield.TextInputEditText;
public class CCCDInfoVerifyFragment extends Fragment {
    private static final String ARG_EKYC = "key_ekyc";
    private static final String ARG_UID = "key_uid";
    private static final String ARG_PHONE = "key_phone";
    private static final String ARG_USERNAME = "key_username";
    private static final String ARG_EMAIL = "key_email";

    private Ekyc ekyc;
    private String uid, phone, username, email;
    public static CCCDInfoVerifyFragment newInstance(Ekyc ekyc, String uid, String phone, String username, String email){
        CCCDInfoVerifyFragment fragment = new CCCDInfoVerifyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EKYC, ekyc);
        args.putString(ARG_UID, uid);
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    public CCCDInfoVerifyFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Lấy object Ekyc
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
        return inflater.inflate(R.layout.fragment_c_c_c_d_info_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText etUsername = view.findViewById(R.id.edtNumberAccount);
        TextInputEditText etSex = view.findViewById(R.id.etSex);
        TextInputEditText etSoCCCD = view.findViewById(R.id.etSoCCCD);
        TextInputEditText etBirthday = view.findViewById(R.id.etBirthday);
        TextInputEditText etNgayCap = view.findViewById(R.id.etNgayCap);
        TextInputEditText etNoiCap = view.findViewById(R.id.etNoiCap);
        TextInputEditText etAddress = view.findViewById(R.id.etAddress);
        Button btnXacNhanCCCD = view.findViewById(R.id.btnXacNhanCCCD);
        Button btnChupLai = view.findViewById(R.id.btn_ChupLai);
        if (ekyc != null) {
            etUsername.setText(ekyc.getFullName());
            etSex.setText(ekyc.getGender());
            etSoCCCD.setText(ekyc.getNationalIdNumber());
            etBirthday.setText(formatDate(ekyc.getDateOfBirth()));
            etNgayCap.setText(formatDate(ekyc.getDateOfIssue()));
            etNoiCap.setText(ekyc.getPlaceOfIssue());
            etAddress.setText(ekyc.getAddress());
        }
        btnXacNhanCCCD.setOnClickListener(v -> {
            FaceVerifyFragment faceVerifyFragment = FaceVerifyFragment.newInstance(ekyc, uid, phone, username, email);
            if(getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).navigateTo(faceVerifyFragment, true);
            }
        });
        btnChupLai.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }
    private String formatDate(String raw){
        if(raw == null) return "";
        String date = raw.trim();
        if(date.length() == 8 && date.matches("\\d{8}")){
            return date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4);
        }
        return date;
    }

}