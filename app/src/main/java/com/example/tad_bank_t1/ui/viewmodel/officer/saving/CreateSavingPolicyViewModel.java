package com.example.tad_bank_t1.ui.viewmodel.officer.saving;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.enums.saving.SavingPolicyStatus;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.savingPolicy.FirebaseSavingPolicyRepository;
import com.example.tad_bank_t1.data.repository.savingPolicy.SavingPolicyRepository;

import java.util.Date;

public class CreateSavingPolicyViewModel extends ViewModel {

    public static class UiState {
        public final boolean loading;
        public final String contractCodeError;
        public final String policyNameError;
        public final String termMonthsError;
        public final String interestRateError;

        public final String toastMessage;
        public final String createdPolicyId;

        public UiState(boolean loading,
                       String contractCodeError,
                       String policyNameError,
                       String termMonthsError,
                       String interestRateError,
                       String toastMessage,
                       String createdPolicyId) {
            this.loading = loading;
            this.contractCodeError = contractCodeError;
            this.policyNameError = policyNameError;
            this.termMonthsError = termMonthsError;
            this.interestRateError = interestRateError;
            this.toastMessage = toastMessage;
            this.createdPolicyId = createdPolicyId;
        }

        public static UiState idle() {
            return new UiState(false, null, null, null, null, null, null);
        }

        public UiState withLoading(boolean v) {
            return new UiState(v, contractCodeError, policyNameError, termMonthsError, interestRateError, toastMessage, createdPolicyId);
        }
    }

    private final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.idle());
    private final MutableLiveData<SavingsRatePolicy> editingPolicy = new MutableLiveData<>(null);

    private final SavingPolicyRepository repo = new FirebaseSavingPolicyRepository();

    public LiveData<SavingsRatePolicy> getEditingPolicy() {
        return editingPolicy;
    }

    public LiveData<UiState> getUiState() {
        return uiState;
    }

    public void clearToast() {
        UiState cur = uiState.getValue();
        if (cur == null) cur = UiState.idle();
        uiState.setValue(new UiState(
                cur.loading,
                cur.contractCodeError,
                cur.policyNameError,
                cur.termMonthsError,
                cur.interestRateError,
                null,
                cur.createdPolicyId
        ));
    }

    public void clearCreatedResult() {
        UiState cur = uiState.getValue();
        if (cur == null) cur = UiState.idle();
        uiState.setValue(new UiState(
                cur.loading,
                cur.contractCodeError,
                cur.policyNameError,
                cur.termMonthsError,
                cur.interestRateError,
                cur.toastMessage,
                null
        ));
    }

    // =========================
    // Public APIs (NEW)
    // =========================

    public void submitCreateSavingPolicy(
            String contractCode,
            String policyName,
            String describe,
            boolean isNoTerm,
            String termMonthsText,      // ✅ inputText
            String interestRateText,    // ✅ inputText
            SavingPolicyStatus status
    ) {
        validateAndUpsert(
                null,
                contractCode,
                policyName,
                describe,
                isNoTerm,
                termMonthsText,
                interestRateText,
                status,
                true
        );
    }

    public void submitUpdateSavingPolicy(
            String policyId,
            String contractCode,
            String policyName,
            String describe,
            boolean isNoTerm,
            String termMonthsText,      // ✅ inputText
            String interestRateText,    // ✅ inputText
            SavingPolicyStatus status
    ) {
        if (isBlank(policyId)) {
            uiState.setValue(new UiState(false, null, null, null, null, "Hợp đồng chưa được tạo", null));
            return;
        }

        validateAndUpsert(
                policyId,
                contractCode,
                policyName,
                describe,
                isNoTerm,
                termMonthsText,
                interestRateText,
                status,
                false
        );
    }

    public void loadPolicy(String policyId) {
        if (isBlank(policyId)) {
            editingPolicy.setValue(null);
            uiState.setValue(new UiState(false, null, null, null, null, "policyId is missing", null));
            return;
        }

        uiState.setValue(UiState.idle().withLoading(true));
        repo.getById(policyId, new ResultCallback<SavingsRatePolicy>() {
            @Override
            public void onSuccess(SavingsRatePolicy data) {
                editingPolicy.setValue(data);
                uiState.setValue(UiState.idle().withLoading(false));
            }

            @Override
            public void onError(String errorMessage) {
                editingPolicy.setValue(null);
                uiState.setValue(new UiState(false, null, null, null, null,
                        (errorMessage == null ? "Load policy failed" : errorMessage),
                        null));
            }
        });
    }

    // =========================
    // Core validate + upsert
    // =========================

    private void validateAndUpsert(
            String policyIdOrNull,
            String contractCode,
            String policyName,
            String describe,
            boolean isNoTerm,
            String termMonthsText,
            String interestRateText,
            SavingPolicyStatus status,
            boolean isCreate
    ) {
        String codeErr = null;
        String nameErr = null;
        String termErr = null;
        String rateErr = null;

        if (isBlank(contractCode)) codeErr = "Mã hợp đồng là bắt buộc";
        if (isBlank(policyName)) nameErr = "Tên hợp đồng là bắt buộc";

        // ---- parse termMonths (int) ----
        Integer termMonths = null;
        String tm = safeTrim(termMonthsText);

        if (tm.isEmpty()) {
            termErr = isNoTerm ? null : "Bạn cần nhập kỳ hạn (tháng)";
            // NoTerm thì termMonths sẽ được ép = 0 ở dưới
        } else {
            try {
                // chỉ cho int (tháng)
                if (tm.contains(".") || tm.contains(",")) {
                    termErr = "Kỳ hạn phải là số nguyên (tháng)";
                } else {
                    termMonths = Integer.parseInt(tm);
                    if (termMonths < 0) termErr = "Kỳ hạn không được âm";
                }
            } catch (Exception e) {
                termErr = "Kỳ hạn phải là số nguyên (tháng)";
            }
        }

        if (isNoTerm) {
            // NoTerm -> bắt buộc 0
            if (termMonths == null) termMonths = 0;
            if (termMonths != 0) termErr = "Không kỳ hạn phải có kỳ hạn = 0";
        } else {
            // Term -> bắt buộc > 0
            if (termMonths == null) termErr = "Bạn cần nhập kỳ hạn (tháng)";
            else if (termMonths <= 0) termErr = "Kỳ hạn phải > 0 (tháng)";
        }

        // ---- parse interestRate (double) ----
        Double interestRate = null;
        String ir = safeTrim(interestRateText);

        if (ir.isEmpty()) {
            rateErr = "Bạn cần nhập lãi suất (ví dụ 0.1)";
        } else {
            try {
                // chấp nhận "0.1" hoặc "0,1" (nếu bạn không muốn thì bỏ replace)
                String normalized = ir.replace(',', '.');
                interestRate = Double.parseDouble(normalized);

                if (Double.isNaN(interestRate) || Double.isInfinite(interestRate)) {
                    rateErr = "Lãi suất không hợp lệ";
                } else {
                    if (interestRate < 0) rateErr = "Lãi suất phải >= 0";
                    // nếu muốn bắt buộc >0 cho kỳ hạn có term:
                    if (!isNoTerm && interestRate <= 0) rateErr = "Lãi suất phải > 0 (ví dụ 0.1)";
                }
            } catch (Exception e) {
                rateErr = "Lãi suất phải là số thực (ví dụ 0.1)";
            }
        }

        if (codeErr != null || nameErr != null || termErr != null || rateErr != null) {
            uiState.setValue(new UiState(false, codeErr, nameErr, termErr, rateErr, null, null));
            return;
        }

        if (status == null) status = SavingPolicyStatus.ACTIVE;

        uiState.setValue(UiState.idle().withLoading(true));

        SavingsRatePolicy policy = new SavingsRatePolicy();
        if (!isBlank(policyIdOrNull)) {
            policy.setSavingPolicyId(policyIdOrNull);
        }

        policy.setContractCode(contractCode.trim());
        policy.setPolicyName(policyName.trim());
        policy.setShortDescription(isBlank(describe) ? "" : describe.trim());

        // ✅ set trực tiếp, không qua SavingsTermRate nữa
        policy.setTermMonths(termMonths);
        policy.setInterestRate(interestRate);
        policy.setStatus(status);

        Date now = new Date();
        if (isCreate) policy.setCreatedAt(now);
        policy.setUpdatedAt(now);

        // NOTE: bạn đang dùng repo.create cho cả update.
        // Nếu repo của bạn có update riêng thì đổi lại.
        repo.create(policy, new ResultCallback<SavingsRatePolicy>() {
            @Override
            public void onSuccess(SavingsRatePolicy data) {
                String id = (data == null) ? null : data.getSavingPolicyId();
                uiState.setValue(new UiState(false, null, null, null, null, null, id));
            }

            @Override
            public void onError(String errorMessage) {
                uiState.setValue(new UiState(false, null, null, null, null,
                        (errorMessage == null ? "Save policy failed" : errorMessage),
                        null));
            }
        });
    }

    // =========================
    // helpers
    // =========================

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
