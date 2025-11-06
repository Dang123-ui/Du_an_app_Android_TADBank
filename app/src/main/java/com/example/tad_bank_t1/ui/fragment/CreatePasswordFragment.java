package com.example.tad_bank_t1.ui.fragment;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.LoginActivity2;
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
    private static final String ARG_FLOW           = "key_flow";
    private static final int    FLOW_SIGN_UP       = 0;
    private static final int    FLOW_FORGOT        = 1;
    private String uid, username, numberAccount;
    private int flow = FLOW_SIGN_UP;
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
        args.putInt(ARG_FLOW, FLOW_SIGN_UP);
        fragment.setArguments(args);
        return fragment;
    }
    public static CreatePasswordFragment newForForgot(String uid) {
        CreatePasswordFragment fragment = new CreatePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putInt(ARG_FLOW, FLOW_FORGOT);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            uid = b.getString(ARG_UID);
            flow = b.getInt(ARG_FLOW, FLOW_SIGN_UP);
            if (flow == FLOW_SIGN_UP) {
                username      = b.getString(ARG_USERNAME);
                numberAccount = b.getString(ARG_ACCOUNTNUMBER);
            } else {
                username = null;
                numberAccount = null;
            }
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
        if (flow == FLOW_FORGOT) {
            btnCreatePW.setText("Cập nhật mật khẩu");
        } else {
            btnCreatePW.setText("Tiếp tục");
        }
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
            if (flow == FLOW_FORGOT) {
                if (TextUtils.isEmpty(uid)) {
                    toast("Thiếu UID. Không thể cập nhật mật khẩu.");
                    return;
                }
                setLoading(true);
                UserRepository userRepo = new FirebaseUserRepository();
                userRepo.getById(uid).addOnSuccessListener(user -> {
                    if (user == null) {
                        setLoading(false);
                        tilPassword.setError("Không tìm thấy tài khoản");
                        return;
                    }
                    user.setPassword(password);
                    userRepo.update(uid, user)
                            .addOnSuccessListener(avoid -> {
                                setLoading(false);
                                toast("Cập nhật mật khẩu thành công");
                                // Điều hướng về LoginActivity2 (có thể đổi sang màn khác tuỳ bạn)
                                if (getActivity() != null) {
                                    Intent i = new Intent(getContext(), LoginActivity2.class);
                                    i.putExtra(LoginActivity2.EXTRA_UID, uid);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                tilPassword.setError("Lưu mật khẩu thất bại: " + e.getMessage());
                            });
                }).addOnFailureListener(e -> {
                    setLoading(false);
                    tilPassword.setError("Lỗi tải tài khoản: " + e.getMessage());
                });

                return;
            }
            CompleteSignUpFragment completeSignUpFragment = CompleteSignUpFragment.newInstance(uid, username, numberAccount, password);
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).navigateTo(completeSignUpFragment, true);
            }
        });
    }
    private void setLoading(boolean saving) {
        btnCreatePW.setEnabled(!saving);
        etPassword.setEnabled(!saving);
        etConfirmPassword.setEnabled(!saving);
        tilPassword.setEnabled(!saving);
        tilConfirmPassword.setEnabled(!saving);
    }
    private void toast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
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