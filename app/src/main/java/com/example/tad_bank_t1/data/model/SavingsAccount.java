package com.example.tad_bank_t1.data.model;


import com.example.tad_bank_t1.data.model.enums.saving.InterestPaymentMethod;
import com.example.tad_bank_t1.data.model.enums.saving.SavingCapitalization;
import com.example.tad_bank_t1.util.MapUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SavingsAccount: Model dùng trong UI/logic.
 * - Kế thừa Account để reuse field chung.
 * - Có thêm field saving để hiển thị detail dễ.
 */
public class SavingsAccount implements Serializable {
    public String policyId;
    public String policyName;
    public double aprAtOpen;  // rate khi chọn gói tiết kiệm

    public Date startDate;
    public Date maturityDate;

    public SavingCapitalization capitalization; //
    public InterestPaymentMethod interestPaymentMethod;  // cách trả lãi AT_MATURITY,   // trả lãi cuối kỳTO_CHECKING,   // trả lãi về tài khoản thanh toán REINVEST       // lãi nhập gốc

    public String payoutAccountId;  // account được nhận khi rút

    public SavingsAccount() {
        super();
    }

    public SavingsAccount(String policyId, Date startDate, Date maturityDate, SavingCapitalization capitalization, double aprAtOpen, InterestPaymentMethod interestPaymentMethod, String payoutAccountId) {
        this.policyId = policyId;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.capitalization = capitalization;
        this.aprAtOpen = aprAtOpen;
        this.interestPaymentMethod = interestPaymentMethod;
        this.payoutAccountId = payoutAccountId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public SavingCapitalization getCapitalization() {
        return capitalization;
    }

    public void setCapitalization(SavingCapitalization capitalization) {
        this.capitalization = capitalization;
    }

    public double getAprAtOpen() {
        return aprAtOpen;
    }

    public void setAprAtOpen(double aprAtOpen) {
        this.aprAtOpen = aprAtOpen;
    }

    public InterestPaymentMethod getInterestPaymentMethod() {
        return interestPaymentMethod;
    }

    public void setInterestPaymentMethod(InterestPaymentMethod interestPaymentMethod) {
        this.interestPaymentMethod = interestPaymentMethod;
    }

    public String getPayoutAccountId() {
        return payoutAccountId;
    }

    public void setPayoutAccountId(String payoutAccountId) {
        this.payoutAccountId = payoutAccountId;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        MapUtils.putIfNotNull(map, "policyId", policyId);
        MapUtils.putIfNotNull(map, "policyName", policyName);
        MapUtils.putIfNotNull(map, "startDate", startDate);
        MapUtils.putIfNotNull(map, "maturityDate", maturityDate);
        MapUtils.putIfNotNull(map, "aprAtOpen", aprAtOpen); MapUtils.putIfNotNull(map, "payoutAccountId", payoutAccountId);

        // enums
        map.put("capitalization", capitalization != null ? capitalization.name() : null);
        map.put("interestPaymentMethod", interestPaymentMethod != null ? interestPaymentMethod.name() : null);

        return map;
    }
}
