package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.mortgage.MortgageInstallmentStatus;
import com.example.tad_bank_t1.util.MapUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MortgagePaymentSchedule {
    /** ID kỳ trả (Firestore có thể dùng docId thay). */
    public String scheduleId;

    /** ID account chứa mortgage. */
    public String accountId;

    /** Số thứ tự kỳ (1..N) để dễ hiển thị. */
    public Integer periodNumber;

    /** Ngày đến hạn (yyyy-MM-dd hoặc Date/Timestamp). */
    public Date dueDate;

    /** Số tiền phải trả kỳ này. */
    public Long amountDue;

    /** Số tiền đã trả kỳ này. */
    public Long amountPaid;

    /** Trạng thái kỳ: PENDING | PAID | OVERDUE. */
    public MortgageInstallmentStatus status;

    /** Mã transaction để mở lịch sử giao dịch (tuỳ chọn). */
    public String transactionId;

    /** Thời điểm đã trả (tuỳ chọn). */
    public Date paidAt;

    public MortgagePaymentSchedule() {
    }

    // get set

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(Integer periodNumber) {
        this.periodNumber = periodNumber;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Long getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Long amountDue) {
        this.amountDue = amountDue;
    }

    public Long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Long amountPaid) {
        this.amountPaid = amountPaid;
    }

    public MortgageInstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(MortgageInstallmentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
    }


    // to map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        MapUtils.putIfNotNull(map, "scheduleId", scheduleId);
        MapUtils.putIfNotNull(map, "accountId", accountId);
        MapUtils.putIfNotNull(map, "dueDate", dueDate);
        MapUtils.putIfNotNull(map, "amountDue", amountDue);
        MapUtils.putIfNotNull(map, "amountPaid", amountPaid);

        // Optional fields (nếu có)
        MapUtils.putIfNotNull(map, "periodNumber", periodNumber);
        MapUtils.putIfNotNull(map, "transactionId", transactionId);
        MapUtils.putIfNotNull(map, "paidAt", paidAt);

        // Enum -> String
        if (status != null) {
            map.put("status", status.name());
        }

        return map;
    }

}
