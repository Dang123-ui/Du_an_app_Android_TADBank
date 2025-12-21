package com.example.tad_bank_t1.data.model;

import androidx.annotation.NonNull;

import com.example.tad_bank_t1.data.model.enums.UserSegment;
import com.example.tad_bank_t1.data.model.enums.saving.DefaultRenewalPolicy;
import com.example.tad_bank_t1.data.model.enums.saving.InterestPayoutOption;
import com.example.tad_bank_t1.data.model.enums.saving.InterestReceivingOption;
import com.example.tad_bank_t1.data.model.enums.saving.ProductType;
import com.example.tad_bank_t1.data.model.enums.saving.SavingPolicyStatus;
import com.example.tad_bank_t1.util.MapUtils;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavingsRatePolicy {
    // ===== Identity / Basic Info =====
    @DocumentId
    private String savingPolicyId;            // Firestore documentId
    private String contractCode;        // Mã hợp đồng
    private String policyName;         // Tên sản phẩm
    private String shortDescription;    // Mô tả ngắn

    // MASS / PREMIER / PRIORITY
    private UserSegment targetSegment;

    // ACTIVE / INACTIVE
    private SavingPolicyStatus status;

    // NON_TERM / TERM
    private ProductType productType;

    // ===== Non-term settings =====
    private Double nonTermApr; // lãi suất không kỳ hạn (%/năm) - null nếu productType=TERM

    // ===== Interest payout & receiving =====
    private InterestPayoutOption interestPayoutOption; // Trả lãi theo phương thức nào
    private InterestReceivingOption defaultInterestReceivingOption; // Nhận lãi ở đâu (checking/ tái tục)



    private int termMonths;
    private double interestRate;

    // Không tái tục / tái tục gốc / tái tục gốc + lãi
    private DefaultRenewalPolicy defaultRenewalPolicy;

    // ===== Limits & rules =====
    private Long minDeposit;
    private Long maxDeposit;

    private Boolean allowOnlineOpening;
    private Boolean allowAdditionalDeposits;

    // ===== Partial withdrawals =====
    private Boolean allowPartialWithdrawals;

    // Số dư tối thiểu phải duy trì trong sổ (khi allowPartialWithdrawals = true)
    private Long minBalanceToKeep;

    // Bạn muốn input text thủ công thay vì select
    private String partialWithdrawInterestCalcNote;

    // ===== Audit =====
    private Date createdAt;
    private Date updatedAt;



    public SavingsRatePolicy() {}
    public SavingsRatePolicy(String savingPolicyId, String policyName, int termMonths, double interestRate, SavingPolicyStatus status, Date createdAt, Date updatedAt, String createdBy, String updateBy) {
        this.savingPolicyId = savingPolicyId;
        this.policyName = policyName;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    @Exclude
    public String getSavingPolicyId() {
        return savingPolicyId;
    }
    public void setSavingPolicyId(String savingPolicyId) {
        this.savingPolicyId = savingPolicyId;
    }

    public String getPolicyName() {
        return policyName;
    }


    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public SavingPolicyStatus getStatus() {
        return status;
    }

    public void setStatus(SavingPolicyStatus status) {
        this.status = status;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    // ===== Firestore mapping =====
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        MapUtils.putIfNotNull(map, "policyName", policyName);
        MapUtils.putIfNotNull(map, "contractCode", contractCode);
        MapUtils.putIfNotNull(map, "shortDescription", shortDescription);
        map.put("termMonths", termMonths);
        map.put("interestRate", interestRate);
        MapUtils.putIfNotNull(map, "status", status);
        map.put("productType", ProductType.TERM);
        MapUtils.putIfNotNull(map, "createdAt", createdAt);
        MapUtils.putIfNotNull(map, "updatedAt", updatedAt);
        map.put("updateBy", "seed");
        return map;
    }
    @NonNull
    @Override
    public String toString() {
        return "SavingsPolicy{" +
                "savingPolicyId='" + savingPolicyId + '\'' +
                ", policyName='" + policyName + '\'' +
                ", termMonths=" + termMonths +
                ", interestRate=" + interestRate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt + '}';
    }
}
