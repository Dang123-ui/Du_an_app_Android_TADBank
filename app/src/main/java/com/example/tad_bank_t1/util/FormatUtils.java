package com.example.tad_bank_t1.util;

import android.graphics.Color;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

    public static String formatCurrency(long amount, String currency) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        if ("VND".equals(currency)) {
            formatter.setCurrency(Currency.getInstance("VND"));
            formatter.setMaximumFractionDigits(0);
            return formatter.format(amount);
        }
        return String.valueOf(amount);
    }

    public static String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("vi", "VN"));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date != null ? date : new Date());
        } catch (Exception e) {
            return dateString;
        }
    }

//    public static int getStatusDotDrawable(AccountStatus status) {
//        if (status == AccountStatus.OPEN) {
//            return R.drawable.dot_status_active;
//        } else {
//            return R.drawable.dot_status_closed;
//        }
//    }
//
//    public static int getStatusBadgeDrawable(AccountStatus status) {
//        if (status == AccountStatus.OPEN) {
//            return R.drawable.bg_badge_active;
//        } else {
//            return R.drawable.bg_badge_closed;
//        }
//    }

    public static class ColorPair {
        public int backgroundColor;
        public int textColor;

        public ColorPair(int backgroundColor, int textColor) {
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
        }
    }

    public static ColorPair getStatusBadgeColors(AccountStatus status) {
        if (status == AccountStatus.OPEN) {
            return new ColorPair(
                    Color.parseColor("#DC2626"), // bg
                    Color.parseColor("#FFFFFF")  // text
            );
        } else {
            return new ColorPair(
                    Color.parseColor("#E7E5E4"), // bg
                    Color.parseColor("#78716C")  // text
            );
        }
    }
}

