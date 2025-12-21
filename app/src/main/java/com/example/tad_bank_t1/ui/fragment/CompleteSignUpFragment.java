package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment displayed at the end of the sign‑up flow.  This screen shows the
 * account number, activation date and allows the user to immediately log in.
 *
 * When the "Log in now" button is clicked we create a new checking account for
 * the current user, update the user’s status to active and then navigate to
 * the login screen.  All asynchronous operations are tied to the fragment’s
 * lifecycle to avoid leaking callbacks after the view has been destroyed.
 */
public class CompleteSignUpFragment extends Fragment {

    private static final String ARG_UID = "key_uid";
    private static final String ARG_USERNAME = "key_username";
    private static final String ARG_ACCOUNTNUMBER = "key_accountnumber";
    private static final String ARG_PASSWORD = "key_password";

    private String uid;
    private String username;
    private String numberAccount;
    private String password;

    private TextInputEditText etNumberAccount;
    private Button btnLoginNow;
    private TextView tvUserName;
    private TextView tvNgayKichHoat;

    public CompleteSignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the
     * provided parameters.
     */
    public static CompleteSignUpFragment newInstance(String uid, String username,
                                                     String numberAccount, String password) {
        CompleteSignUpFragment fragment = new CompleteSignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_ACCOUNTNUMBER, numberAccount);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            username = getArguments().getString(ARG_USERNAME);
            numberAccount = getArguments().getString(ARG_ACCOUNTNUMBER);
            password = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNumberAccount = view.findViewById(R.id.edtNumberAccount);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvNgayKichHoat = view.findViewById(R.id.tvNgayKichHoat);
        btnLoginNow = view.findViewById(R.id.btnLoginNow);

        // Populate the UI with the provided arguments
        tvUserName.setText(username);
        etNumberAccount.setText(numberAccount);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = sdf.format(new Date());
        tvNgayKichHoat.setText(today);

        btnLoginNow.setOnClickListener(v -> {
            // Disable button to prevent repeated clicks
            btnLoginNow.setEnabled(false);
            createAccountAndActivateUser();
        });
    }
    private void createAccountAndActivateUser() {
        // Construct an Account object
        Account account = new Account();
        account.setUserId(uid);
        account.setAccountName(username);
        account.setAccountNumber(numberAccount);
        account.setCreatedAt(new Date());
        account.setType(AccountType.CHECKING);
        account.setCurrency("VND");
        account.setBalance(0L);
        account.setStatus(AccountStatus.OPEN);
        account.setBranchId("br001");
        account.setDefault(true);
        FirebaseAccountRepository accountRepo = new FirebaseAccountRepository();
        accountRepo.create(account)
                .addOnSuccessListener(avoid -> {
                    // Account created successfully
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT)
                                .show();
                    }
                    // Once the account is created, update the user status
                    updateUserStatus();
                })
                .addOnFailureListener(e -> {
                    // Re‑enable the button only if it exists
                    if (btnLoginNow != null) {
                        btnLoginNow.setEnabled(true);
                    }
                    if (isAdded()) {
                        Toast.makeText(requireContext(),
                                        "Tạo tài khoản thất bại: " + e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
    private void updateUserStatus() {
        FirebaseUserRepository userRepo = new FirebaseUserRepository();

        userRepo.getById(uid)
                .addOnSuccessListener(requireActivity(), user -> {
                    if (user != null) {
                        user.setStatus(UserStatus.ACTIVE);
                        user.setPassword(password);
                        userRepo.update(uid, user)
                                .addOnSuccessListener(requireActivity(), aVoid -> {
                                    Toast.makeText(requireContext(),
                                                    "Cập nhật user thành công", Toast.LENGTH_SHORT)
                                            .show();
                                    navigateToLogin();
                                })
                                .addOnFailureListener(requireActivity(), e -> {
                                    btnLoginNow.setEnabled(true);
                                    Toast.makeText(requireContext(),
                                                    "Cập nhật user thất bại: " + e.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                });
                    } else {
                        btnLoginNow.setEnabled(true);
                        Toast.makeText(requireContext(),
                                        "Không tìm thấy user", Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .addOnFailureListener(requireActivity(), e -> {
                    btnLoginNow.setEnabled(true);
                    Toast.makeText(requireContext(),
                                    "Lấy user thất bại: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                });
    }
    private void navigateToLogin() {
        if (btnLoginNow != null) {
            btnLoginNow.setEnabled(true);
        }

        LoginNowFragment loginNowFragment = LoginNowFragment.newInstance(uid);
        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) getActivity()).navigateTo(loginNowFragment, false);
        }
    }
}