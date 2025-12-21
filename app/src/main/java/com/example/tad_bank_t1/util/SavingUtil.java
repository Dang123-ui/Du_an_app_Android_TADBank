package com.example.tad_bank_t1.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SavingUtil {
    /**
    * Kiểm tra sổ đã đáo hạn chưa (maturityDate <= hiện tại).
    */
    public static boolean isMatured(Date maturityDate) {
        if (maturityDate == null) return false;
        return maturityDate.getTime() <= System.currentTimeMillis();
    }

    /**
     * Tính lãi đến thời điểm đóng sổ.
     * - Nếu đáo hạn: dùng APR lúc mở sổ và số tháng từ start -> maturity
     * - Nếu chưa đáo hạn: dùng EARLY_WITHDRAW_APR và số tháng từ start -> closeDate
     *
     * Công thức lãi đơn theo tháng:
     * interest = principal * (apr/100) / 12 * months
     */
    public static long calculateInterestUntilClose(
            long principal,
            double aprAtOpen,
            Date startDate,
            Date maturityDate,
            Date closeDate,
            boolean isMatured
    ) {
        if (principal <= 0) return 0;
        if (startDate == null) return 0;
        if (closeDate == null) closeDate = new Date();

        int months;
        double appliedApr;

        if (isMatured) {
            // đáo hạn: tính đúng kỳ hạn
            if (maturityDate == null) return 0;
            months = calculateFullMonths(startDate, maturityDate);
            appliedApr = aprAtOpen;
        } else {
            // rút trước hạn: demo cho lãi 0.05 (hoặc đổi sang lãi không kỳ hạn)
            months = calculateFullMonths(startDate, closeDate);
            appliedApr = TadConstants.RATE_WHEN_CLOSE_SAVING_BEFORE_MATURITY;
        }

        if (months <= 0) return 0;

        double interest = principal * (appliedApr / 100.0) / 12.0 * months;

        // Làm tròn và đảm bảo không âm
        return Math.max(0L, Math.round(interest));
    }

    /**
     * Tính số "tháng tròn" giữa 2 mốc thời gian.
     * Ví dụ: 2025-01-15 -> 2025-02-14 = 0 tháng (chưa đủ 1 tháng)
     *        2025-01-15 -> 2025-02-15 = 1 tháng
     */
    public static int calculateFullMonths(Date from, Date to) {
        if (from == null || to == null) return 0;
        if (to.before(from)) return 0;

        Calendar start = Calendar.getInstance();
        start.setTime(from);

        Calendar end = Calendar.getInstance();
        end.setTime(to);

        int yearDiff = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        int monthDiff = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);

        int months = yearDiff * 12 + monthDiff;

        // Nếu ngày trong tháng của end < ngày của start => chưa đủ 1 tháng tròn, trừ đi 1
        if (end.get(Calendar.DAY_OF_MONTH) < start.get(Calendar.DAY_OF_MONTH)) {
            months--;
        }

        return Math.max(0, months);
    }

    // tao accont number tự động
    private static final SecureRandom RNG = new SecureRandom();

    public static String generateAccountNumber() {
        String datePart = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());
        String randomPart = randomDigits(6);
        return datePart + randomPart;
    }
    private static String randomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RNG.nextInt(10));
        }
        return sb.toString();
    }


    // tính lãi theo kỳ hạn với rate: lãi suất/năm, principal: số tiền gốc
    public static long calProfitPerMonth(double rate, long principal){
        return (long) (principal * (rate / 100.0) / 12.0);
    }


    // tính kỳ hạn (đơn vị: tháng)
    public static int calculateTermMonths(Date startDate, Date maturityDate) {
        if (startDate == null || maturityDate == null) return 0;

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(maturityDate);

        int startTotal = start.get(Calendar.YEAR) * 12 + start.get(Calendar.MONTH);
        int endTotal = end.get(Calendar.YEAR) * 12 + end.get(Calendar.MONTH);

        int diff = endTotal - startTotal;
        return Math.max(diff, 0);
    }

    /** tính ngày kết thúc kỳ hạn (đơn vị: ngày) theo ngày mở sổ và kỳ hạn */
    public static Date addMonths(Date startDate, int termMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, termMonths);
        return calendar.getTime();
    }

}
