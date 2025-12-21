package com.example.tad_bank_t1.util.mock;

import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.model.enums.mortgage.MortgageInstallmentStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class MortgageScheduleMockUtil {

    private MortgageScheduleMockUtil() {}

    private static final int DEFAULT_PERIODS = 12;

    public static List<MortgagePaymentSchedule> getDefaultMockSchedules() {
        List<MortgagePaymentSchedule> result = new ArrayList<>();

        // ====== 3 account IDs bạn đưa ======
        result.addAll(buildSchedulesForAccount(
                "ef1WXJQoWMj4e4u",
                createDate(2025, Calendar.NOVEMBER, 20),
                350_000_000L,
                9.5,
                240,
                DEFAULT_PERIODS
        ));

        result.addAll(buildSchedulesForAccount(
                "T4iSY6L7MzMiTuWQbUfW",
                createDate(2025, Calendar.JULY, 20),
                550_000_000L,
                9.5,
                240,
                DEFAULT_PERIODS
        ));

        result.addAll(buildSchedulesForAccount(
                "a0TnzQVeMizNx1XzRLfX",
                createDate(2025, Calendar.AUGUST, 20),
                500_000_000L,
                9.5,
                240,
                DEFAULT_PERIODS
        ));

        return result;
    }

    private static List<MortgagePaymentSchedule> buildSchedulesForAccount(
            String accountId,
            Date startDate,
            long principalAmount,
            double interestRateAnnualPercent,
            int termMonths,
            int periodsToSeed
    ) {
        List<MortgagePaymentSchedule> list = new ArrayList<>();
        Date now = new Date();

        long amountDue = calculateMockAmountDue(principalAmount, interestRateAnnualPercent, termMonths);

        for (int period = 1; period <= periodsToSeed; period++) {
            Date dueDate = addMonths(startDate, period);

            MortgagePaymentSchedule schedule = new MortgagePaymentSchedule();
            schedule.scheduleId = accountId + "_P" + period;   // deterministic id
            schedule.accountId = accountId;
            schedule.periodNumber = period;
            schedule.dueDate = dueDate;

            schedule.amountDue = amountDue;

            boolean isPast = dueDate.before(now);
            if (isPast) {
                if (period <= 2) {
                    schedule.status = MortgageInstallmentStatus.PAID;
                    schedule.amountPaid = amountDue;
                    schedule.paidAt = addDays(dueDate, -2);
                    schedule.transactionId = schedule.scheduleId + "_TX";
                } else {
                    schedule.status = MortgageInstallmentStatus.OVERDUE;
                    schedule.amountPaid = 0L;
                    schedule.paidAt = null;
                    schedule.transactionId = null;
                }
            } else {
                schedule.status = MortgageInstallmentStatus.PENDING;
                schedule.amountPaid = 0L;
                schedule.paidAt = null;
                schedule.transactionId = null;
            }

            list.add(schedule);
        }

        return list;
    }

    private static long calculateMockAmountDue(long principal, double annualRatePercent, int termMonths) {
        // Mock đơn giản: principal/term + interest(principal * monthlyRate)
        double monthlyRate = (annualRatePercent / 100.0) / 12.0;
        double principalPart = principal / (double) termMonths;
        double interestPart = principal * monthlyRate;
        return Math.round(principalPart + interestPart);
    }

    private static Date createDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private static Date addMonths(Date base, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    private static Date addDays(Date base, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }
}
