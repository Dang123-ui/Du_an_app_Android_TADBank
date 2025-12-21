package com.example.tad_bank_t1.ui.fragment.officer.saving;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.enums.saving.SavingPolicyStatus;
import com.example.tad_bank_t1.ui.viewmodel.officer.saving.CreateSavingPolicyViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreatSavingContractFragment extends Fragment {
    public static final String ARG_POLICY_ID = "policyId";

    private CreateSavingPolicyViewModel viewModel;
    private String currentPolicyId = null;

    // Contract info
    private TextInputLayout tilCode, tilName, tilDescription, spTarget, spStatus;
    private TextInputEditText etCode, etName, etDescription;
    private MaterialAutoCompleteTextView edtTarget, edtStatus;

    // Saving type toggle (XML: 2 radio button, KHÔNG có RadioGroup)
    private RadioButton rbNoTerm, rbTerm;
    private View groupNoTerm, groupTerm;

    // NoTerm
    private TextInputLayout tilNoTermRate;
    private TextInputEditText etNoTermRate;
    private MaterialAutoCompleteTextView edtInterestPaymentNoTerm;

    // Term inputs
    private TextInputLayout tilTermMonths, tilTermRate;
    private TextInputEditText etTermMonths, etTermRate;
    private MaterialAutoCompleteTextView edtInterestPaymentTerm;

    // Renewal + Limits
    private RadioGroup rgRenew;
    private MaterialSwitch switchOnline, switchAdditional, switchPartial, switchEarly;
    private View groupPartialDetails, groupEarlyDetails;

    private TextInputLayout tilMinBalance;
    private TextInputEditText etMinBalance;
    private TextInputLayout spPartialInterest;
    private TextInputEditText etPartialInterest;

    private TextInputLayout spEarlyInterest;
    private TextInputEditText etEarlyInterest;

    private TextInputLayout tilPenaltyRate;
    private TextInputEditText etPenaltyRate;

    private TextInputLayout tilMinDays, tilMaxDays;
    private TextInputEditText etMinDays, etMaxDays;

    private TextInputLayout tilMinDeposit, tilMaxDeposit;
    private TextInputEditText etMinDeposit, etMaxDeposit;

    // Bottom bar
    private MaterialButton btnUpdate;
    private View btnCreate; // XML là AppCompatButton -> để View vẫn ok

    public CreatSavingContractFragment() {}

    public static CreatSavingContractFragment newInstance(@Nullable String policyId) {
        CreatSavingContractFragment f = new CreatSavingContractFragment();
        Bundle b = new Bundle();
        if (policyId != null) b.putString(ARG_POLICY_ID, policyId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) currentPolicyId = getArguments().getString(ARG_POLICY_ID, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_creat_saving_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CreateSavingPolicyViewModel.class);

        bindViews(view);
        setupDropdowns();
        setupToggleGroups();
        setupSwitchGroups();
        setupActions();
        observeState();
        applyMode();

        if (!TextUtils.isEmpty(currentPolicyId)) {
            viewModel.loadPolicy(currentPolicyId);
            observeLoadedPolicy();
        } else {
            applyDefaultUi();
        }
    }

    private void applyMode() {
        boolean isEdit = !TextUtils.isEmpty(currentPolicyId);

        btnUpdate.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(isEdit);

        if (isEdit) btnCreate.setVisibility(View.GONE);
        else btnCreate.setVisibility(View.VISIBLE);
    }

    private void observeLoadedPolicy() {
        viewModel.getEditingPolicy().observe(getViewLifecycleOwner(), policy -> {
            bindPolicyToUi(policy);
        });
    }

    private void bindPolicyToUi(SavingsRatePolicy policy) {
        etCode.setText(safe(policy.getContractCode()));
        etName.setText(safe(policy.getPolicyName()));
        etDescription.setText(safe(policy.getShortDescription()));

        SavingPolicyStatus status = policy.getStatus();
        if (status == null) status = SavingPolicyStatus.ACTIVE;
        edtStatus.setText(status.name(), false);

        edtTarget.setText("Khách hàng phổ thông", false);

        int termMonths = policy.getTermMonths();
        double apr = policy.getInterestRate();

        if (termMonths <= 0) {
            // NoTerm
            rbNoTerm.setChecked(true);
            rbTerm.setChecked(false);
            showNoTerm(true);

            etNoTermRate.setText(String.valueOf(apr));
            if (etTermMonths != null) etTermMonths.setText("");
            if (etTermRate != null) etTermRate.setText("");
        } else {
            // Term
            rbTerm.setChecked(true);
            rbNoTerm.setChecked(false);
            showNoTerm(false);

            if (etTermMonths != null) etTermMonths.setText(String.valueOf(termMonths));
            if (etTermRate != null) etTermRate.setText(String.valueOf(apr));
            if (etNoTermRate != null) etNoTermRate.setText("");
        }

        if (!TextUtils.isEmpty(policy.getSavingPolicyId())) {
            currentPolicyId = policy.getSavingPolicyId();
        }
    }

    private void bindViews(View v) {
        tilCode = v.findViewById(R.id.tilCode);
        tilName = v.findViewById(R.id.tilName);
        tilDescription = v.findViewById(R.id.tilDescription);
        spTarget = v.findViewById(R.id.spTarget);
        spStatus = v.findViewById(R.id.spStatus);

        etCode = v.findViewById(R.id.etCode);
        etName = v.findViewById(R.id.etName);
        etDescription = v.findViewById(R.id.etDescription);

        edtTarget = v.findViewById(R.id.edtTarget);
        edtStatus = v.findViewById(R.id.edtStatus);

        rbNoTerm = v.findViewById(R.id.rbNoTerm);
        rbTerm = v.findViewById(R.id.rbTerm);
        groupNoTerm = v.findViewById(R.id.groupNoTerm);
        groupTerm = v.findViewById(R.id.groupTerm);

        tilNoTermRate = v.findViewById(R.id.tilNoTermRate);
        etNoTermRate = v.findViewById(R.id.etNoTermRate);
        edtInterestPaymentNoTerm = v.findViewById(R.id.edtInterestPaymentNoTerm);

        tilTermMonths = v.findViewById(R.id.tilTermMonths);
        tilTermRate = v.findViewById(R.id.tilTermRate);
        etTermMonths = v.findViewById(R.id.etTermMonths);
        etTermRate = v.findViewById(R.id.etTermRate);
        edtInterestPaymentTerm = v.findViewById(R.id.edtInterestPaymentTerm);

        rgRenew = v.findViewById(R.id.rgRenew);

        tilMinDeposit = v.findViewById(R.id.tilMinDeposit);
        tilMaxDeposit = v.findViewById(R.id.tilMaxDeposit);
        etMinDeposit = v.findViewById(R.id.etMinDeposit);
        etMaxDeposit = v.findViewById(R.id.etMaxDeposit);

        switchOnline = v.findViewById(R.id.switchOnline);
        switchAdditional = v.findViewById(R.id.switchAdditional);
        switchPartial = v.findViewById(R.id.switchPartial);
        switchEarly = v.findViewById(R.id.switchEarly);

        groupPartialDetails = v.findViewById(R.id.groupPartialDetails);
        groupEarlyDetails = v.findViewById(R.id.groupEarlyDetails);

        tilMinBalance = v.findViewById(R.id.tilMinBalance);
        etMinBalance = v.findViewById(R.id.etMinBalance);
        spPartialInterest = v.findViewById(R.id.spPartialInterest);
        etPartialInterest = v.findViewById(R.id.etPartialInterest);

        spEarlyInterest = v.findViewById(R.id.spEarlyInterest);
        etEarlyInterest = v.findViewById(R.id.etEarlyInterest);

        tilPenaltyRate = v.findViewById(R.id.tilPenaltyRate);
        etPenaltyRate = v.findViewById(R.id.etPenaltyRate);

        tilMinDays = v.findViewById(R.id.tilMinDays);
        tilMaxDays = v.findViewById(R.id.tilMaxDays);
        etMinDays = v.findViewById(R.id.etMinDays);
        etMaxDays = v.findViewById(R.id.etMaxDays);

        btnUpdate = v.findViewById(R.id.btnUpdate);
        btnCreate = v.findViewById(R.id.btnCreate);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new String[]{"Khách hàng phổ thông", "Khách hàng cao cấp", "Khách hàng VIP"}
        );
        edtTarget.setAdapter(targetAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new String[]{"ACTIVE", "INACTIVE"}
        );
        edtStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> payAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new String[]{"Trả lãi cuối kỳ", "Trả lãi định kỳ", "Trả lãi trước"}
        );
        edtInterestPaymentNoTerm.setAdapter(payAdapter);
        edtInterestPaymentTerm.setAdapter(payAdapter);
    }

    private void setupToggleGroups() {
        // default NoTerm
        rbNoTerm.setChecked(true);
        rbTerm.setChecked(false);
        showNoTerm(true);

        rbNoTerm.setOnClickListener(v -> {
            rbNoTerm.setChecked(true);
            rbTerm.setChecked(false);
            showNoTerm(true);
            clearTermErrors();
        });

        rbTerm.setOnClickListener(v -> {
            rbTerm.setChecked(true);
            rbNoTerm.setChecked(false);
            showNoTerm(false);
            clearTermErrors();
        });
    }

    private void showNoTerm(boolean isNoTerm) {
        if (groupNoTerm != null) groupNoTerm.setVisibility(isNoTerm ? View.VISIBLE : View.GONE);
        if (groupTerm != null) groupTerm.setVisibility(isNoTerm ? View.GONE : View.VISIBLE);
    }

    private void setupSwitchGroups() {
        if (groupPartialDetails != null) {
            groupPartialDetails.setVisibility(switchPartial != null && switchPartial.isChecked() ? View.VISIBLE : View.GONE);
        }
        if (groupEarlyDetails != null) {
            groupEarlyDetails.setVisibility(switchEarly != null && switchEarly.isChecked() ? View.VISIBLE : View.GONE);
        }

        if (switchPartial != null) {
            switchPartial.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (groupPartialDetails != null) groupPartialDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            });
        }

        if (switchEarly != null) {
            switchEarly.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (groupEarlyDetails != null) groupEarlyDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            });
        }
    }

    private void setupActions() {
        btnCreate.setOnClickListener(v -> {
            clearAllErrors();

            boolean isNoTerm = rbNoTerm != null && rbNoTerm.isChecked();

            String contractCode = text(etCode);
            String policyName = text(etName);
            String description = text(etDescription);
            SavingPolicyStatus status = parseStatus(text(edtStatus));

            String termMonthsText;
            String interestRateText;

            if (isNoTerm) {
                termMonthsText = "0";
                interestRateText = text(etNoTermRate);

                // validate interest rate no term
                if (!validateInterestRate(interestRateText, tilNoTermRate)) return;

            } else {
                termMonthsText = text(etTermMonths);
                interestRateText = text(etTermRate);

                if (!validateTermMonths(termMonthsText, tilTermMonths)) return;
                if (!validateInterestRate(interestRateText, tilTermRate)) return;
            }

            viewModel.submitCreateSavingPolicy(
                    contractCode,
                    policyName,
                    description,
                    isNoTerm,
                    termMonthsText,
                    interestRateText,
                    status
            );
        });

        btnUpdate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(currentPolicyId)) {
                Toast.makeText(requireContext(), "Hợp đồng chưa được tạo", Toast.LENGTH_SHORT).show();
                return;
            }

            clearAllErrors();

            boolean isNoTerm = rbNoTerm != null && rbNoTerm.isChecked();

            String contractCode = text(etCode);
            String policyName = text(etName);
            String description = text(etDescription);
            SavingPolicyStatus status = parseStatus(text(edtStatus));

            String termMonthsText;
            String interestRateText;

            if (isNoTerm) {
                termMonthsText = "0";
                interestRateText = text(etNoTermRate);

                if (!validateInterestRate(interestRateText, tilNoTermRate)) return;
            } else {
                termMonthsText = text(etTermMonths);
                interestRateText = text(etTermRate);

                if (!validateTermMonths(termMonthsText, tilTermMonths)) return;
                if (!validateInterestRate(interestRateText, tilTermRate)) return;
            }

            viewModel.submitUpdateSavingPolicy(
                    currentPolicyId,
                    contractCode,
                    policyName,
                    description,
                    isNoTerm,
                    termMonthsText,
                    interestRateText,
                    status
            );
        });
    }

    private void observeState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            btnUpdate.setEnabled(!state.loading);
            btnCreate.setEnabled(!state.loading);

            tilCode.setError(state.contractCodeError);
            tilName.setError(state.policyNameError);

            boolean isNoTerm = rbNoTerm != null && rbNoTerm.isChecked();

            if (!TextUtils.isEmpty(state.termMonthsError)) {
                if (!isNoTerm && tilTermMonths != null) tilTermMonths.setError(state.termMonthsError);
                else Toast.makeText(requireContext(), state.termMonthsError, Toast.LENGTH_SHORT).show();
            }

            if (!TextUtils.isEmpty(state.interestRateError)) {
                if (isNoTerm) {
                    tilNoTermRate.setError(state.interestRateError);
                } else if (tilTermRate != null) {
                    tilTermRate.setError(state.interestRateError);
                }
            }

            if (!TextUtils.isEmpty(state.toastMessage)) {
                Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_SHORT).show();
                viewModel.clearToast();
            }

            if (!TextUtils.isEmpty(state.createdPolicyId)) {
                currentPolicyId = state.createdPolicyId;
                Toast.makeText(requireContext(), "Thành công: " + state.createdPolicyId, Toast.LENGTH_SHORT).show();
                viewModel.clearCreatedResult();
                applyMode();
            }
        });
    }

    private void applyDefaultUi() {
        edtTarget.setText("Khách hàng phổ thông", false);
        edtStatus.setText("ACTIVE", false);

        rbNoTerm.setChecked(true);
        rbTerm.setChecked(false);
        showNoTerm(true);

        edtInterestPaymentNoTerm.setText("Trả lãi cuối kỳ", false);
        edtInterestPaymentTerm.setText("Trả lãi cuối kỳ", false);

        if (etNoTermRate != null) etNoTermRate.setText("");
        if (etTermMonths != null) etTermMonths.setText("");
        if (etTermRate != null) etTermRate.setText("");
    }

    // =======================
    // VALIDATION (UI)
    // =======================

    private boolean validateTermMonths(String monthsText, TextInputLayout til) {
        String m = monthsText == null ? "" : monthsText.trim();
        if (m.isEmpty()) {
            if (til != null) til.setError("Bạn cần nhập kỳ hạn (tháng)");
            return false;
        }
        // months phải là int
        if (m.contains(".") || m.contains(",")) {
            if (til != null) til.setError("Kỳ hạn phải là số nguyên (tháng)");
            return false;
        }
        try {
            int v = Integer.parseInt(m);
            if (v <= 0) {
                if (til != null) til.setError("Kỳ hạn phải > 0");
                return false;
            }
        } catch (Exception e) {
            if (til != null) til.setError("Kỳ hạn phải là số nguyên");
            return false;
        }
        return true;
    }

    private boolean validateInterestRate(String rateText, TextInputLayout til) {
        String r = rateText == null ? "" : rateText.trim();
        if (r.isEmpty()) {
            if (til != null) til.setError("Bạn cần nhập lãi suất (ví dụ 0.1)");
            return false;
        }
        try {
            Double.parseDouble(r.replace(',', '.'));
            return true;
        } catch (Exception e) {
            if (til != null) til.setError("Lãi suất phải là số thực (ví dụ 0.1)");
            return false;
        }
    }

    private void clearTermErrors() {
        if (tilTermMonths != null) tilTermMonths.setError(null);
        if (tilTermRate != null) tilTermRate.setError(null);
        if (tilNoTermRate != null) tilNoTermRate.setError(null);
    }

    private void clearAllErrors() {
        if (tilCode != null) tilCode.setError(null);
        if (tilName != null) tilName.setError(null);
        if (tilNoTermRate != null) tilNoTermRate.setError(null);
        if (tilTermMonths != null) tilTermMonths.setError(null);
        if (tilTermRate != null) tilTermRate.setError(null);
    }

    // =======================
    // Helpers
    // =======================

    private String text(TextInputEditText et) {
        if (et == null || et.getText() == null) return "";
        return et.getText().toString().trim();
    }

    private String text(MaterialAutoCompleteTextView et) {
        if (et == null) return "";
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private SavingPolicyStatus parseStatus(String s) {
        if (TextUtils.isEmpty(s)) return SavingPolicyStatus.ACTIVE;
        String x = s.trim().toUpperCase();
        if (x.contains("ACTIVE") || x.contains("HOẠT")) return SavingPolicyStatus.ACTIVE;
        if (x.contains("INACTIVE") || x.contains("KHÔNG")) return SavingPolicyStatus.INACTIVE;
        try { return SavingPolicyStatus.valueOf(x); } catch (Exception ignored) {}
        return SavingPolicyStatus.ACTIVE;
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
