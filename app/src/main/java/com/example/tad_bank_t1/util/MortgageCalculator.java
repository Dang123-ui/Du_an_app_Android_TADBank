package com.example.tad_bank_t1.util;

import com.example.tad_bank_t1.data.model.enums.mortgage.MortgagePaymentFrequency;

/**
 * Utility tính số tiền phải trả mỗi kỳ theo công thức annuity (trả đều).
 * Đủ để đáp ứng yêu cầu: xem số tiền trả mỗi tháng / mỗi 2 tuần.
 */
public final class MortgageCalculator {

    private MortgageCalculator() {}

    /**
     * @param principalAmount       số tiền vay ban đầu (VND)
     * @param annualRatePercent     lãi suất năm (%/năm), ví dụ 9.5
     * @param termMonths            thời hạn vay (tháng)
     * @param frequency             MONTHLY hoặc BIWEEKLY
     * @return số tiền phải trả mỗi kỳ (VND, làm tròn)
     */
    public static long calculateInstallmentAmount(
            long principalAmount,
            double annualRatePercent,
            int termMonths,
            MortgagePaymentFrequency frequency
    ) {
        if (principalAmount <= 0 || termMonths <= 0 || frequency == null) return 0L;

        int payments;
        int periodsPerYear;

        if (frequency == MortgagePaymentFrequency.MONTHLY) {
            periodsPerYear = 12;
            payments = termMonths;
        } else {
            // BIWEEKLY: 26 kỳ / năm
            periodsPerYear = 26;
            double years = termMonths / 12.0;
            payments = (int) Math.round(years * periodsPerYear);
            if (payments <= 0) payments = 1;
        }

        double annualRate = annualRatePercent / 100.0;
        double periodRate = annualRate / periodsPerYear;

        // Nếu lãi suất = 0 => trả đều gốc
        if (periodRate == 0) {
            return Math.round((double) principalAmount / payments);
        }

        // payment = P * r / (1 - (1 + r)^(-N))
        double numerator = principalAmount * periodRate;
        double denominator = 1 - Math.pow(1 + periodRate, -payments);
        double payment = numerator / denominator;

        return Math.round(payment);
    }
}
