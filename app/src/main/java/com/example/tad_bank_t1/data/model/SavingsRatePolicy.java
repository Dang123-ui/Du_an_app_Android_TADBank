package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.saving.SavingPolicyStatus;
import com.example.tad_bank_t1.util.MapUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SavingsRatePolicy {
    private String savingPolicyId;
    /**
     * Tên gói tiết kiệm để hiển thị.
     * Ví dụ: "Tiết kiệm 6 tháng", "Tiết kiệm 12 tháng lãi cuối kỳ".
     */
    private String policyName;

    /**
     * Kỳ hạn của gói tiết kiệm (đơn vị: tháng).
     * Ví dụ: 1, 3, 6, 12, 24...
     * - Dùng để tính maturityDate = startDate + termMonths (khi mở sổ).
     */
    private int termMonths;

    /**
     * Lãi suất năm (APR) theo %/năm.
     * Ví dụ: 6.5 nghĩa là 6.5%/năm (KHÔNG phải 0.065).
     * - ProfitPerMonth (ước tính) = principal * interestRate/100 / 12.
     */
    private double interestRate;

    private SavingPolicyStatus status;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updateBy;


    // Constructor


    public SavingsRatePolicy() {
    }

    public SavingsRatePolicy(String savingPolicyId, String policyName, int termMonths, double interestRate, SavingPolicyStatus status, Date createdAt, Date updatedAt, String createdBy, String updateBy) {
        this.savingPolicyId = savingPolicyId;
        this.policyName = policyName;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updateBy = updateBy;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    // to map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("savingPolicyId", savingPolicyId);
        MapUtils.putIfNotNull(map, "policyName", policyName);
        map.put("termMonths", termMonths);
        map.put("interestRate", interestRate);
        MapUtils.putIfNotNull(map, "status", status);
        MapUtils.putIfNotNull(map, "createdAt", createdAt);
        MapUtils.putIfNotNull(map, "updatedAt", updatedAt);
        MapUtils.putIfNotNull(map, "createdBy", createdBy);
        MapUtils.putIfNotNull(map, "updateBy", updateBy);

        return map;
    }

    @Override
    public String toString() {
        return "SavingsPolicy{" +
                "savingPolicyId='" + savingPolicyId + '\'' +
                ", policyName='" + policyName + '\'' +
                ", termMonths=" + termMonths +
                ", interestRate=" + interestRate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy='" + createdBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                '}';
    }
}

