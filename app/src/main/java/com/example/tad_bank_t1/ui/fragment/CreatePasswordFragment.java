package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePasswordFragment extends Fragment {
    private static final String ARG_UID = "key_uid";
    private static final String ARG_USERNAME = "key_username";
    private static final String ARG_ACCOUNTNUMBER = "key_accountnumber";
    private String uid, username, numberAccount;
    TextInputLayout tilPassword, tilConfirmPassword;
    TextInputEditText etPassword, etConfirmPassword;
    Button btnCreatePW;
    private static final Pattern PW_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?`~|\\\\])\\S{6,20}$"
    );
    public CreatePasswordFragment() {
        // Required empty public constructor
    }

    public static CreatePasswordFragment newInstance(String uid, String username, String numberAccount) {
        CreatePasswordFragment fragment = new CreatePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_ACCOUNTNUMBER, numberAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            username = getArguments().getString(ARG_USERNAME);
            numberAccount = getArguments().getString(ARG_ACCOUNTNUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tilPassword = view.findViewById(R.id.textInputLayoutMatKhau);
        tilConfirmPassword = view.findViewById(R.id.textInputLayoutXNMatKhau);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnCreatePW = view.findViewById(R.id.btnCreatePW);
        btnCreatePW.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAndUpdateUI();
            }
        };
        etPassword.addTextChangedListener(watcher);
        etConfirmPassword.addTextChangedListener(watcher);
        validateAndUpdateUI();
        btnCreatePW.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            CompleteSignUpFragment completeSignUpFragment = CompleteSignUpFragment.newInstance(uid, username, numberAccount, password);
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).navigateTo(completeSignUpFragment, true);
            }
        });
    }
    private void validateAndUpdateUI() {
        String pw = textOf(etPassword);
        String cpw = textOf(etConfirmPassword);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilPassword.setHelperText(null);
        tilConfirmPassword.setHelperText(null);
        String pwError = getPasswordError(pw, username);
        if (pwError != null) {
            tilPassword.setError(pwError);
            btnCreatePW.setEnabled(false);
            return;
        } else {
            tilPassword.setHelperText("Mật khẩu hợp lệ");
        }

        // Kiểm tra confirm
        if (TextUtils.isEmpty(cpw)) {
            tilConfirmPassword.setError("Vui lòng nhập lại mật khẩu");
            btnCreatePW.setEnabled(false);
            return;
        }
        if (!pw.equals(cpw)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            btnCreatePW.setEnabled(false);
            return;
        } else {
            tilConfirmPassword.setHelperText("Khớp");
        }

        // Tất cả điều kiện đều thỏa
        btnCreatePW.setEnabled(true);
    }
    private static String textOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString();
    }
    @Nullable
    private static String getPasswordError(String pw, @Nullable String username) {
        if (TextUtils.isEmpty(pw)) return "Vui lòng nhập mật khẩu";

        // Không trùng username (không phân biệt hoa/thường)
        if (!TextUtils.isEmpty(username)) {
            if (pw.toLowerCase(Locale.US).equals(username.toLowerCase(Locale.US))) {
                return "Mật khẩu không được trùng với tên đăng nhập";
            }
        }
        if (!PW_PATTERN.matcher(pw).matches()) {
            if (pw.length() < 6 || pw.length() > 20) return "Độ dài 6–20 ký tự";
            if (pw.contains(" ")) return "Không được chứa khoảng trắng";
            if (!pw.matches(".*[0-9].*")) return "Phải có ít nhất 1 chữ số";
            if (!pw.matches(".*[a-z].*")) return "Phải có ít nhất 1 chữ thường";
            if (!pw.matches(".*[A-Z].*")) return "Phải có ít nhất 1 chữ hoa";
            if (!pw.matches(".*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?`~|\\\\].*"))
                return "Phải có ít nhất 1 ký tự đặc biệt";
            return "Mật khẩu không hợp lệ";
        }
        return null;
    }
}