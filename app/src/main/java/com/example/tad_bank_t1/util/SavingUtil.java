package com.example.tad_bank_t1.util;

import java.util.Calendar;
import java.util.Date;

public class SavingUtil {

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
