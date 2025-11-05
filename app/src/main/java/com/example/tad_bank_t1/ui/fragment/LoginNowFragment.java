package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginNowFragment extends Fragment {
    private static final String ARG_UID = "key_uid";
    private String uid;

    private TextInputLayout tilPhone, tilPassword;
    private TextInputEditText etPhone, etPassword;
    private CheckBox checkBox;
    private Button btnNext;
    private LottieAnimationView loading;

    private final UserRepository userRepository = new FirebaseUserRepository();
    private final AccountRepository accountRepository = new FirebaseAccountRepository();

    public LoginNowFragment() {}

    public static LoginNowFragment newInstance(String uid) {
        LoginNowFragment fragment = new LoginNowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            uid = args.getString(ARG_UID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_now, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilPassword = view.findViewById(R.id.tilPassword);
        etPhone = view.findViewById(R.id.edtPhone);
        etPassword = view.findViewById(R.id.etPassword);
        checkBox = view.findViewById(R.id.cbArgreeWithPolicy2);
        btnNext = view.findViewById(R.id.btnNext);
        loading = view.findViewById(R.id.loading);
        TextWatcher watcher = new SimpleWatcher(() -> {
            validateAndShowErrors();
            updateButtonState();
        });
        etPhone.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonState());
        // Báo lỗi ngay từ đầu
        validateAndShowErrors();
        updateButtonState();

        btnNext.setOnClickListener(v -> onClickNext());
    }

    private void onClickNext() {
        boolean validNow = validateAndShowErrors();
        if (!validNow || !checkBox.isChecked()) return;
        final String phoneInput = safeText(etPhone);
        final String password = safeText(etPassword);

        setLoading(true);
        userRepository.getById(uid)
                .addOnSuccessListener(userObj -> {
                    if (!(userObj instanceof User)) {
                        showGeneralError();
                        return;
                    }
                    User user = (User) userObj;
                    String storedNorm = normalizePhone(user.getPhone());
                    String inputNorm  = normalizePhone(phoneInput);
                    if (!inputNorm.equals(storedNorm)) {
                        tilPhone.setError("Số điện thoại không khớp với tài khoản");
                        setLoading(false);
                        return;
                    }
                    if (!passwordMatches(user, password)) {
                        tilPassword.setError("Mật khẩu không chính xác");
                        setLoading(false);
                        return;
                    }
                    PhoneVerifyFragment1 phoneVerifyFragment1 = PhoneVerifyFragment1.newInstance(uid);
                    if (getActivity() instanceof SignUpActivity) {
                        ((SignUpActivity) getActivity()).navigateTo(phoneVerifyFragment1, true);
                    }
                })
                .addOnFailureListener(e -> showGeneralError());
    }

    /** Chuẩn hoá số VN về E.164: +84xxxxxxxxx (bỏ ký tự lạ, đổi 0xxxxxxxxx -> +84xxxxxxxxx) */
    private String normalizePhone(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim().replaceAll("\\s+", "");
        if (trimmed.startsWith("+")) {
            String digits = trimmed.substring(1).replaceAll("\\D", "");
            return "+" + digits;
        }
        String digitsOnly = trimmed.replaceAll("\\D", "");
        if (digitsOnly.startsWith("0") && digitsOnly.length() >= 10) {
            return "+84" + digitsOnly.substring(1);
        }
        if (digitsOnly.startsWith("84")) {
            return "+" + digitsOnly;
        }
        if (digitsOnly.length() == 9) {
            return "+84" + digitsOnly;
        }
        return digitsOnly;
    }
    private boolean validateAndShowErrors() {
        boolean valid = true;

        String phone = safeText(etPhone).replaceAll("\\s", "");
        String password = safeText(etPassword);

        tilPhone.setError(null);
        tilPassword.setError(null);
        String phonePattern = "^(0\\d{9}|\\+84\\d{9})$";
        if (phone.isEmpty()) {
            tilPhone.setError(getString(R.string.info_sign_up_err_phone_empty));
            valid = false;
        } else if (!phone.matches(phonePattern)) {
            tilPhone.setError(getString(R.string.info_sign_up_invalid_phone));
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            valid = false;
        }

        return valid;
    }

    private void updateButtonState() {
        boolean enabled = validateAndShowErrors() && checkBox.isChecked();
        btnNext.setEnabled(enabled);
    }

    private static String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean passwordMatches(User user, String input) {
        String saved = user.getPassword();
        return saved != null && saved.equals(input);
    }

    private void showGeneralError() {
        tilPhone.setError(getString(R.string.info_sign_up_err_general));
        setLoading(false);
    }

    private void setLoading(boolean show) {
        if (!isAdded() || getView() == null) return;
        if (loading != null) {
            loading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            if (show) loading.playAnimation();
            else loading.cancelAnimation();
        }
        btnNext.setEnabled(!show);
        btnNext.setText(show ? getString(R.string.info_sign_up_loading) : getString(R.string.continue_));
        etPhone.setEnabled(!show);
        etPassword.setEnabled(!show);
        checkBox.setEnabled(!show);
    }

    private static class SimpleWatcher implements TextWatcher {
        private final Runnable cb;
        SimpleWatcher(Runnable cb) { this.cb = cb; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { cb.run(); }
    }
}
