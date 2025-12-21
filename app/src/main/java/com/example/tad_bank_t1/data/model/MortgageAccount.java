package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.mortgage.MortgagePaymentFrequency;
import com.example.tad_bank_t1.util.MapUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Thông tin riêng của tài khoản vay thế chấp.
 * Được lưu trong document accounts dưới key: "mortgage".
 */
public class MortgageAccount implements Serializable {

    // public class MortgageAccount {
    /** Số tiền vay ban đầu (gốc). */
    private long principalAmount;

    /** Lãi suất năm (%/năm). Ví dụ 9.5 nghĩa là 9.5%/năm. */
    private double interestRateAnnual;

    /** Thời hạn vay (tháng). */
    private int termMonths;

    /** Tần suất trả nợ: theo tháng hoặc mỗi 2 tuần. */
    private MortgagePaymentFrequency paymentFrequency;

    /** Ngày giải ngân / bắt đầu khoản vay. */
    private Date startDate;

    /** Ngày đến hạn kỳ tiếp theo. */
    private Date nextDueDate;

    /** Nhân viên ngân hàng phụ trách khoản vay. */
    private String officerId;

    // ====== BỔ SUNG (để code UI + pay dễ hơn) ======

    /** Tổng số kỳ phải trả (đã quy đổi theo paymentFrequency). */
    private int totalPeriods;

    /** Số tiền phải trả mỗi kỳ (tháng/2 tuần). */
    private long amountDuePerPeriod;

    /** Số kỳ đã trả. */
    private int paidPeriods;

    /** Gốc còn lại (tính/ cập nhật sau mỗi lần trả). */
    private long outstandingPrincipal;

    /** Ngày trả kỳ gần nhất. */
    private Date lastPaymentDate;

    // /** Số ngày gia hạn trước khi tính OVERDUE (tuỳ chọn). */
    // private int graceDays;
    //
    // /** Phí trễ hạn cố định mỗi kỳ nếu OVERDUE (tuỳ chọn). */
    // private long lateFeeAmount;

    // getters/setters...
    // /** Thời hạn vay (tháng). Ví dụ 240 tháng = 20 năm. */
    // private int termMonths;

    // /** Trả theo tháng hoặc mỗi 2 tuần. */
    // private MortgagePaymentFrequency paymentFrequency;

    // /** Ngày bắt đầu khoản vay (giải ngân). */
    // private Date startDate;

    // /** Ngày đến hạn kỳ tiếp theo (để UI hiển thị nhanh). */
    // private Date nextDueDate;

    // /** UserId của nhân viên ngân hàng phụ trách khoản vay. */
    // private String officerId;

    public MortgageAccount() {
    }

    // ===== getters/setters =====
    public long getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(long principalAmount) {
        this.principalAmount = principalAmount;
    }

    public double getInterestRateAnnual() {
        return interestRateAnnual;
    }

    public void setInterestRateAnnual(double interestRateAnnual) {
        this.interestRateAnnual = interestRateAnnual;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public MortgagePaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(MortgagePaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Date nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public int getTotalPeriods() {
        return totalPeriods;
    }

    public void setTotalPeriods(int totalPeriods) {
        this.totalPeriods = totalPeriods;
    }

    public long getAmountDuePerPeriod() {
        return amountDuePerPeriod;
    }

    public void setAmountDuePerPeriod(long amountDuePerPeriod) {
        this.amountDuePerPeriod = amountDuePerPeriod;
    }

    public int getPaidPeriods() {
        return paidPeriods;
    }

    public void setPaidPeriods(int paidPeriods) {
        this.paidPeriods = paidPeriods;
    }

    public long getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(long outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    /**
     * Convert MortgageInfo sang Map để nhét vào Account.toMap().
     * Chỉ put field không null để tránh ghi đè null.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        // Core loan info
        MapUtils.putIfNotNull(map, "principalAmount", principalAmount);
        MapUtils.putIfNotNull(map, "interestRateAnnual", interestRateAnnual);
        MapUtils.putIfNotNull(map, "termMonths", termMonths);
        MapUtils.putIfNotNull(map, "startDate", startDate);
        MapUtils.putIfNotNull(map, "nextDueDate", nextDueDate);
        MapUtils.putIfNotNull(map, "officerId", officerId);

        // Enum -> String
        if (paymentFrequency != null) {
            map.put("paymentFrequency", paymentFrequency.name());
        }

        // ===== Optional fields (nếu class em đã bổ sung thì giữ, chưa có thì xoá)
        // =====
        MapUtils.putIfNotNull(map, "totalPeriods", totalPeriods);
        MapUtils.putIfNotNull(map, "amountDuePerPeriod", amountDuePerPeriod);
        MapUtils.putIfNotNull(map, "paidPeriods", paidPeriods);
        MapUtils.putIfNotNull(map, "outstandingPrincipal", outstandingPrincipal);
        MapUtils.putIfNotNull(map, "lastPaymentDate", lastPaymentDate);

        return map;
    }
}
