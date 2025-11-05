package com.example.tad_bank_t1.ui.fragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class ChoseNumberCardFragment extends Fragment {

    private static final String ARG_UID = "key_uid";
    private static final String ARG_USERNAME = "key_username";

    // TODO: Rename and change types of parameters
    private String uid, username;
    Button btnChoseNumberCardToFinally;
    TextInputEditText etNumberAccount;
    TextInputLayout tilNumberAccount;
    LottieAnimationView loading;
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{12,14}$");
    private FirebaseAccountRepository accountRepo = new FirebaseAccountRepository();

    public ChoseNumberCardFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChoseNumberCardFragment newInstance(String uid,String username) {
        ChoseNumberCardFragment fragment = new ChoseNumberCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chose_number_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnChoseNumberCardToFinally = view.findViewById(R.id.btnChoseNumberCardToFinally);
        etNumberAccount = view.findViewById(R.id.etNumberAccount);
        tilNumberAccount = view.findViewById(R.id.tilNumberAccount);
        loading = view.findViewById(R.id.loading);
        if (etNumberAccount != null) {
            etNumberAccount.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }
        btnChoseNumberCardToFinally.setOnClickListener(V -> checkAccountNumber());
    }
    private void checkAccountNumber(){
        String input = etNumberAccount.getText() != null
                ? etNumberAccount.getText().toString().trim()
                : "";
        if (TextUtils.isEmpty(input) || !ACCOUNT_NUMBER_PATTERN.matcher(input).matches()) {
            showError("Số tài khoản phải có 12–14 chữ số (0–9)");
            return;
        }
        btnChoseNumberCardToFinally.setEnabled(false);
        btnChoseNumberCardToFinally.setText("Đang xử lý...");
        loading.setVisibility(View.VISIBLE);
        accountRepo.isAccountNumberAvailable(input).addOnSuccessListener(isAvailable -> {
            if(!isAdded()) return;
            if(Boolean.TRUE.equals(isAvailable)){
                btnChoseNumberCardToFinally.setText("Tiếp tục");
                loading.setVisibility(View.INVISIBLE);
                showError("Số tài khoản hợp lệ");
                String numberAccount = etNumberAccount.getText().toString().trim();
                CreatePasswordFragment createPasswordFragment = CreatePasswordFragment.newInstance(uid, username, numberAccount);
                if (getActivity() instanceof SignUpActivity) {
                    ((SignUpActivity) getActivity()).navigateTo(createPasswordFragment, true);
                }
            }else{
                btnChoseNumberCardToFinally.setText("Tiếp tục");
                showError("Số tài khoản đã tồn tại");
                loading.setVisibility(View.INVISIBLE);
            }
            btnChoseNumberCardToFinally.setEnabled(true);
        }).addOnFailureListener(e -> {
            if (!isAdded()) return;
            btnChoseNumberCardToFinally.setEnabled(true);
            btnChoseNumberCardToFinally.setText("Tiếp tục");
            loading.setVisibility(View.INVISIBLE);
            showError("Lỗi khi kiểm tra: " + e.getMessage());
        });
    }
    private void showError(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}