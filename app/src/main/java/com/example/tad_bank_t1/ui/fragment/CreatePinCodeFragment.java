package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePinCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePinCodeFragment extends Fragment {
    private static final String ARG_UID = "key_uid";
    private String uid;
    private EditText et1, et2, et3, et4, et5, et6;
    private Button btnNext;
    private LottieAnimationView animationView;
    private final AccountRepository accountRepository = new FirebaseAccountRepository();
    private final UserRepository userRepository = new FirebaseUserRepository();
    private Account account;
    private User user;
    public CreatePinCodeFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static CreatePinCodeFragment newInstance(String uid) {
        CreatePinCodeFragment fragment = new CreatePinCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            uid = args.getString(ARG_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        userRepository.getById(uid).addOnCompleteListener(task -> {
            if(task.isSuccessful()) user = task.getResult();
            accountRepository.getByUserId(uid).addOnCompleteListener(task1 -> {
                setLoading(false);
                if(!task1.isSuccessful() || task1.getResult() == null){
                    toast("Không tìm thấy tài khoản của người dùng.");
                    btnNext.setEnabled(false);
                    return;
                }
                account = task1.getResult();
            });
        });
        btnNext.setOnClickListener(v -> {
            if(account == null){
                toast("Không tìm thấy tài khoản của người dùng.");
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
            accountRepository.update(account.getAccountId(), account).addOnSuccessListener(u -> {
                setLoading(false);
                toast("Tạo mã pin thành công.");
                btnNext.setEnabled(false);
                Congratulation_Fragment congratulationFragment = Congratulation_Fragment.newInstance(uid);
                if (getActivity() instanceof SignUpActivity) {
                    ((SignUpActivity) getActivity()).navigateTo(congratulationFragment, false);
                }
            }).addOnFailureListener(e -> {
                setLoading(false);
                toast("Lưu mã PIN thất bại: " + e.getMessage());
            });
        });
    }
    private void setupPinInputs(){
        EditText[] inputs = {et1, et2, et3, et4, et5, et6};
        for (EditText et : inputs) {
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        }

        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            inputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void afterTextChanged(Editable s) {
                    if (s != null && s.length() > 1) {
                        String pasted = s.toString().replaceAll("\\D", "");
                        if (pasted.length() == 6) {
                            fillCode(pasted);
                            inputs[5].requestFocus();
                            btnNext.setEnabled(readPin().length() == 6);
                            return;
                        }
                    }
                    if (s != null && s.length() == 1 && idx < inputs.length - 1) {
                        inputs[idx + 1].requestFocus();
                    }
                    btnNext.setEnabled(readPin().length() == 6);
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
                    btnNext.setEnabled(readPin().length() == 6);
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
    private String readPin(){
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
    private boolean allSame(String s){
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
            // Ưu tiên getDateOfBirth()
            try { return (Date) User.class.getMethod("getDateOfBirth").invoke(u); }
            catch (NoSuchMethodException ignore) {}
        } catch (Exception ignore) {}
        return null;
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