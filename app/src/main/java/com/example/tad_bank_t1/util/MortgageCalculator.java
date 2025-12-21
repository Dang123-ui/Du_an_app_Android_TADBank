package com.example.tad_bank_t1.util;

import com.example.tad_bank_t1.data.model.enums.mortgage.MortgagePaymentFrequency;

/**
 * Utility tính số tiền phải trả mỗi kỳ theo công thức annuity (trả đều).
 * Đủ để đáp ứng yêu cầu: xem số tiền trả mỗi tháng / mỗi 2 tuần.
 */
public final class MortgageCalculator {


    /**
     * Calculates the total number of payment periods for a mortgage.  When
     * the payment frequency is biweekly we approximate the number of
     * periods per year as 26.  For monthly payments it is simply the
     * number of months in the term.
     *
     * @param termMonths the number of months in the loan term
     * @param frequency  the payment frequency (monthly or biweekly)
     * @return the total number of payment periods
     */
    public static int calculateTotalPeriods(int termMonths, MortgagePaymentFrequency frequency) {
        if (frequency == MortgagePaymentFrequency.BIWEEKLY) {
            // 26 payments per year ≈ every two weeks; convert months to years
            return (int) Math.round(termMonths * 26.0 / 12.0);
        }
        // Monthly payments: one period per month
        return termMonths;
    }

    /**
     * Calculates the periodic payment for a standard amortizing loan.
     * This uses the standard annuity formula: P * r / (1 - (1+r)^-n),
     * where r is the periodic interest rate and n is the total number of
     * periods.  If the interest rate is zero then the payment is simply
     * the principal divided by the number of periods.
     *
     * @param principalAmount    the original loan amount
     * @param interestRateAnnual the annual interest rate (percentage per year)
     * @param termMonths         the number of months in the loan term
     * @param frequency          the payment frequency (monthly or biweekly)
     * @return the periodic payment amount, rounded to the nearest currency unit
     */
    public static long calculateAmountDuePerPeriod(long principalAmount,
                                                   double interestRateAnnual,
                                                   int termMonths,
                                                   MortgagePaymentFrequency frequency) {
        int periodsPerYear = (frequency == MortgagePaymentFrequency.BIWEEKLY) ? 26 : 12;
        int totalPeriods = calculateTotalPeriods(termMonths, frequency);
        double ratePerPeriod = (interestRateAnnual / 100.0) / periodsPerYear;

        if (totalPeriods <= 0) {
            return 0L;
        }

        // When the rate is zero, payments are simply principal / number of periods
        if (ratePerPeriod == 0) {
            return Math.round((double) principalAmount / totalPeriods);
        }

        // Standard amortization formula
        double discountFactor = 1 - Math.pow(1 + ratePerPeriod, -totalPeriods);
        double payment = principalAmount * ratePerPeriod / discountFactor;
        return Math.round(payment);
    }

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
