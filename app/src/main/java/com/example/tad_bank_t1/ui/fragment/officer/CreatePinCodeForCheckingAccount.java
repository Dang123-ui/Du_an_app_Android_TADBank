package com.example.tad_bank_t1.ui.fragment.officer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.tad_bank_t1.R;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.OfficerMainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePinCodeForCheckingAccount extends Fragment {
    private static final String ARG_UID = "key_uid";
    // accountId key để officer flow truyền vào
    private static final String ARG_ACCOUNT_ID = "accountId";

    private String uid;
    private String accountId;
    private EditText et1, et2, et3, et4, et5, et6;
    private Button btnNext;
    private LottieAnimationView animationView;
    private final AccountRepository accountRepository = new FirebaseAccountRepository();
    private final UserRepository userRepository = new FirebaseUserRepository();
    private Account account;
    private User user;
    public CreatePinCodeForCheckingAccount() {
        // Required empty public constructor
    }

    public static CreatePinCodeForCheckingAccount newInstance(@NonNull String accountId, @Nullable String uid) {
        CreatePinCodeForCheckingAccount fragment = new CreatePinCodeForCheckingAccount();
        Bundle args = new Bundle();
        args.putString(ARG_ACCOUNT_ID, accountId);
        if (uid != null) args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            uid = args.getString(ARG_UID);
            accountId = args.getString(ARG_ACCOUNT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_pin_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et6 = view.findViewById(R.id.et6);
        btnNext = view.findViewById(R.id.btn_Next);
        animationView = view.findViewById(R.id.loading);

        btnNext.setEnabled(false);
        setupPinInputs();
        setLoading(false);

        if (TextUtils.isEmpty(accountId)) {
            toast("Thiếu accountId.");
            btnNext.setEnabled(false);
            return;
        }
        setLoading(true);
        if (!TextUtils.isEmpty(uid)) {
            userRepository.getById(uid).addOnCompleteListener(task -> {
                if (task.isSuccessful()) user = task.getResult();
                // rồi load account
                loadAccount();
            });
        } else {
            loadAccount();
        }
        btnNext.setOnClickListener(v -> {
            if (account == null) {
                toast("Không tìm thấy tài khoản.");
                return;
            }

            String pin = readPin();
            String error = validatePin(pin, user);
            if (error != null) {
                toast(error);
                return;
            }

            account.setPinCode(pin);
            account.setUpdatedAt(new Date());

            setLoading(true);
            accountRepository.update(account.getAccountId(), account)
                    .addOnSuccessListener(u -> {
                        setLoading(false);
                        toast("Tạo tạo khoản Cheking thành công.");
                        btnNext.setEnabled(false);

                        // Officer flow: thường popBackStack về màn trước / hoặc navigate success tuỳ bạn
                        // Mình để popBackStack cho an toàn.
                        if (isAdded()) {
                            AccountListOfficerFragment accountListOfficerFragment = new AccountListOfficerFragment();
                            if(getActivity() instanceof OfficerMainActivity) {
                                ((OfficerMainActivity) getActivity()).navigateTo(accountListOfficerFragment, false);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        toast("Lưu mã PIN thất bại: " + e.getMessage());
                    });
        });
    }
    private void loadAccount() {
        accountRepository.getById(accountId).addOnCompleteListener(task -> {
            setLoading(false);
            if (!task.isSuccessful() || task.getResult() == null) {
                toast("Không tìm thấy tài khoản.");
                btnNext.setEnabled(false);
                return;
            }
            account = task.getResult();
            // enable theo pin length
            btnNext.setEnabled(readPin().length() == 6);
        });
    }

    private void setupPinInputs() {
        EditText[] inputs = {et1, et2, et3, et4, et5, et6};
        for (EditText et : inputs) {
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        }

        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            inputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void afterTextChanged(Editable s) {
                    // paste 6 số
                    if (s != null && s.length() > 1) {
                        String pasted = s.toString().replaceAll("\\D", "");
                        if (pasted.length() == 6) {
                            fillCode(pasted);
                            inputs[5].requestFocus();
                            btnNext.setEnabled(readPin().length() == 6 && !isLoading());
                            return;
                        }
                    }
                    if (s != null && s.length() == 1 && idx < inputs.length - 1) {
                        inputs[idx + 1].requestFocus();
                    }
                    btnNext.setEnabled(readPin().length() == 6 && !isLoading());
                }
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            inputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                        inputs[idx].getText().length() == 0 && idx > 0) {
                    inputs[idx - 1].requestFocus();
                    inputs[idx - 1].setText("");
                    btnNext.setEnabled(readPin().length() == 6 && !isLoading());
                    return true;
                }
                return false;
            });
        }
    }
    private void fillCode(String code6) {
        if (code6 == null) return;
        EditText[] inputs = new EditText[]{et1, et2, et3, et4, et5, et6};
        for (int i = 0; i < 6 && i < code6.length(); i++) {
            inputs[i].setText(String.valueOf(code6.charAt(i)));
        }
    }
    private String readPin() {
        return (safeGet(et1) + safeGet(et2) + safeGet(et3)
                + safeGet(et4) + safeGet(et5) + safeGet(et6)).trim();
    }

    private String safeGet(EditText e) {
        return e.getText() == null ? "" : e.getText().toString();
    }

    private @Nullable String validatePin(String pin, @Nullable User user) {
        if (pin.length() != 6 || !pin.matches("\\d{6}")) {
            return "Mã PIN phải gồm đúng 6 chữ số.";
        }
        if (allSame(pin)) {
            return "Mã PIN quá đơn giản (6 số giống nhau).";
        }
        if ("123456".equals(pin)) {
            return "Mã PIN quá đơn giản (123456).";
        }
        String dob6 = getDobAs_ddMMyy(user);
        if (!TextUtils.isEmpty(dob6) && dob6.equals(pin)) {
            return "Mã PIN không được trùng ngày sinh (ddMMyy).";
        }
        return null;
    }

    private boolean allSame(String s) {
        char c = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != c) return false;
        }
        return true;
    }

    private @Nullable String getDobAs_ddMMyy(@Nullable User u) {
        if (u == null) return null;
        Date dob = getUserDob(u);
        if (dob == null) return null;
        return new SimpleDateFormat("ddMMyy", Locale.getDefault()).format(dob);
    }
    private @Nullable Date getUserDob(User u) {
        try {
            // giống file bạn đưa: ưu tiên getDateOfBirth()
            try { return (Date) User.class.getMethod("getDateOfBirth").invoke(u); }
            catch (NoSuchMethodException ignore) {}
        } catch (Exception ignore) {}
        return null;
    }
    private boolean isLoading() {
        return animationView != null && animationView.getVisibility() == View.VISIBLE;
    }
    private void setLoading(boolean loading) {
        if (!isAdded() || getView() == null) return;
        animationView.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);

        et1.setEnabled(!loading);
        et2.setEnabled(!loading);
        et3.setEnabled(!loading);
        et4.setEnabled(!loading);
        et5.setEnabled(!loading);
        et6.setEnabled(!loading);

        btnNext.setEnabled(!loading && readPin().length() == 6);
    }

    private void toast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
