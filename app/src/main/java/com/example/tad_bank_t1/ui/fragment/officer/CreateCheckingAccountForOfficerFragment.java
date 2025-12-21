package com.example.tad_bank_t1.ui.fragment.officer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.OfficerMainActivity;
import com.example.tad_bank_t1.ui.viewmodel.officer.CreateCheckingAccountViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class CreateCheckingAccountForOfficerFragment extends Fragment {

    public static final String ARG_UID = "uid";
    public static final String ARG_ACCOUNT_ID = "accountId";
    public static final String ARG_USER_ID = "userId";

    private String uid;

    private MaterialTextView tvUid;

    private TextInputLayout tilAccountName, tilAccountNumber, tilBalance;
    private TextInputEditText etAccountName, etAccountNumber, etBalance;

    private TextInputEditText etBranchId, etCurrency, etStatus, etType;
    private MaterialButton btnContinue;

    private CreateCheckingAccountViewModel viewModel;

    public CreateCheckingAccountForOfficerFragment() {}

    public static CreateCheckingAccountForOfficerFragment newInstance(@NonNull String uid) {
        CreateCheckingAccountForOfficerFragment f = new CreateCheckingAccountForOfficerFragment();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        uid = (args != null) ? args.getString(ARG_UID) : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_checking_account_for_officer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CreateCheckingAccountViewModel.class);

        tvUid = view.findViewById(R.id.tvUid);

        tilAccountName = view.findViewById(R.id.tilAccountName);
        tilAccountNumber = view.findViewById(R.id.tilAccountNumber);
        tilBalance = view.findViewById(R.id.tilBalance);

        etAccountName = view.findViewById(R.id.etAccountName);
        etAccountNumber = view.findViewById(R.id.etAccountNumber);
        etBalance = view.findViewById(R.id.etBalance);

        // các field default có sẵn trong xml của bạn
        etBranchId = view.findViewById(R.id.etBranchId);
        etCurrency = view.findViewById(R.id.etCurrency);
        etStatus = view.findViewById(R.id.etStatus);
        etType = view.findViewById(R.id.etType);

        btnContinue = view.findViewById(R.id.btnContinue);

        tvUid.setText("UID: " + (uid == null ? "(null)" : uid));

        // ép default theo yêu cầu
        if (etBranchId != null) etBranchId.setText("br001");
        if (etCurrency != null) etCurrency.setText("VND");
        if (etType != null) etType.setText("CHECKING");
        if (etStatus != null && TextUtils.isEmpty(textOf(etStatus))) etStatus.setText("OPEN");
        etBranchId.setEnabled(false);
        etCurrency.setEnabled(false);
        etType.setEnabled(false);
        etStatus.setEnabled(false);

        // clear field error khi gõ lại
        attachClearOnTyping(etAccountName, tilAccountName);
        attachClearOnTyping(etAccountNumber, tilAccountNumber);
        attachClearOnTyping(etBalance, tilBalance);

        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            btnContinue.setEnabled(!state.loading);

            // Field errors -> TIL
            tilAccountName.setError(state.accountNameError);
            tilAccountNumber.setError(state.accountNumberError);
            tilBalance.setError(state.balanceError);

            // Toast-only errors (uid missing, firebase/network)
            if (!TextUtils.isEmpty(state.toastMessage)) {
                Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_SHORT).show();
                viewModel.clearToast();
            }

            if (!TextUtils.isEmpty(state.createdAccountId)) {
                CreatePinCodeForCheckingAccount pinFragment =
                        CreatePinCodeForCheckingAccount.newInstance(state.createdAccountId, uid);

                if (getActivity() instanceof OfficerMainActivity) {
                    ((OfficerMainActivity) getActivity()).navigateTo(pinFragment, false);
                }

                viewModel.clearCreatedResult();
            }
        });

        btnContinue.setOnClickListener(v -> {
            // clear errors trước
            tilAccountName.setError(null);
            tilAccountNumber.setError(null);
            tilBalance.setError(null);

            viewModel.submitCreateChecking(
                    uid,
                    textOf(etAccountName),
                    textOf(etAccountNumber),
                    textOf(etBalance)
            );
        });
    }

    private String textOf(TextInputEditText et) {
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void attachClearOnTyping(TextInputEditText et, TextInputLayout til) {
        if (et == null || til == null) return;
        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                til.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
