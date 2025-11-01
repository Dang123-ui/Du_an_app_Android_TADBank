package com.example.tad_bank_t1.ui.fragment;

import android.annotation.SuppressLint;
import android.media.browse.MediaBrowser;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.Role;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InfoSignUpFragment extends Fragment {
    private TextInputLayout tlUsername, tlEmail, tlPhone;
    private TextInputEditText etUsername, etEmail, etPhone;
    private CheckBox cbAgreeWithPolicy;
    private Button btnNextToPhoneVerify;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private PlayerView playerView;
    private ExoPlayer player;
    private TextView tvPolicy;
    private LottieAnimationView animationView;
    private final UserRepository userRepository = new FirebaseUserRepository();
    public InfoSignUpFragment() {}
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_sign_up, container, false);
        tlUsername = view.findViewById(R.id.tlUsername);
        tlEmail = view.findViewById(R.id.tlEmail);
        animationView = view.findViewById(R.id.loading);
        tlPhone = view.findViewById(R.id.tlPhone);
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        cbAgreeWithPolicy = view.findViewById(R.id.cbArgreeWithPolicy);
        tvPolicy = view.findViewById(R.id.tvPolicy);
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        btnNextToPhoneVerify = view.findViewById(R.id.btnNextToPhoneVerify);
        TextWatcher watcher = new SimpleWatcher(this::updateButtonState);
        etUsername.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        cbAgreeWithPolicy.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonState());
        btnNextToPhoneVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validAll(true) && !cbAgreeWithPolicy.isChecked()){
                    return;
                }
                final String email = etEmail.getText().toString().trim();
                final String phone = etPhone.getText().toString().trim();
                final String username = etUsername.getText().toString().trim();
                btnNextToPhoneVerify.setEnabled(false);
                btnNextToPhoneVerify.setText(getString(R.string.info_sign_up_loading));
                animationView.setVisibility(View.VISIBLE);
                tlPhone.setError(null);
                userRepository.phoneExists(phone).addOnSuccessListener(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        tlPhone.setError(getString(R.string.info_sign_up_err_phone_exist));
                        btnNextToPhoneVerify.setEnabled(true);
                        btnNextToPhoneVerify.setText(getString(R.string.continue_));
                        animationView.setVisibility(View.GONE);
                    } else {
                        ((SignUpActivity) requireActivity()).navigateTo(PhoneVerifyFragment.newInstance(email, phone, username), true);
                    }
                }).addOnFailureListener(e -> {
                    tlPhone.setError(getString(R.string.info_sign_up_err_general));
                    btnNextToPhoneVerify.setEnabled(true);
                    btnNextToPhoneVerify.setText(getString(R.string.continue_));
                    animationView.setVisibility(View.GONE);
                });
            }
        });
        return view;
    }
    private void updateButtonState() {
        boolean enabled = validAll(false);
        btnNextToPhoneVerify.setEnabled(enabled && cbAgreeWithPolicy.isChecked());
    }
    private boolean validAll(boolean showError){
        boolean valid = true;
        String username = etUsername.getText().toString().trim(); // Trim luôn để tránh lỗi lặp
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 1. Reset tất cả các lỗi trước
        tlUsername.setError(null);
        tlEmail.setError(null);
        tlPhone.setError(null);

        // KIỂM TRA HỌ VÀ TÊN (BẮT BUỘC)
        if (username.isEmpty()) {
            tlUsername.setError(getString(R.string.info_sign_up_name_empty));
            valid = false;
        }

        // KIỂM TRA EMAIL (BẮT BUỘC + HỢP LỆ)
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (email.isEmpty()) {
            tlEmail.setError(getString(R.string.info_sign_up_err_email_empty));
            valid = false;
        } else if (!email.matches(emailPattern)) {
            // Chỉ kiểm tra regex nếu email không bị trống
            tlEmail.setError("Email không hợp lệ.Email phải có dạng @gmail.com");
            valid = false;
        }

        // KIỂM TRA SỐ ĐIỆN THOẠI (BẮT BUỘC + HỢP LỆ)
        String phonePattern = "^(0|\\+84)\\d{9}$";
        if (phone.isEmpty()) {
            tlPhone.setError(getString(R.string.info_sign_up_err_phone_empty));
            valid = false;
        } else if (!phone.matches(phonePattern)) {
            // Chỉ kiểm tra regex nếu số điện thoại không bị trống
            tlPhone.setError(getString(R.string.info_sign_up_invalid_phone));
            valid = false;
        }

        // Biến showError không cần thiết trong logic này vì bạn luôn hiển thị lỗi.

        return valid;
    }
    private static class SimpleWatcher implements TextWatcher {
        private final Runnable cb;
        SimpleWatcher(Runnable cb) {this.cb = cb;}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            cb.run();
        }
    }
}