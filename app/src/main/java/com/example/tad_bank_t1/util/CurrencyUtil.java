package com.example.tad_bank_t1.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    public static String formatVND(double amount) {
        // Tạo một đối tượng NumberFormat cho tiền tệ của Việt Nam
        Locale vietnameseLocale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnameseLocale);

        // Định dạng số và thêm ký hiệu VNĐ
        return currencyFormatter.format(amount);
    }

    public static double convertToDouble(String amountString) {
        // Loại bỏ tất cả các dấu phẩy
        String cleanString = amountString.replaceAll(",", "");

        // Chuyển đổi chuỗi đã làm sạch sang kiểu double
        return Double.parseDouble(cleanString);
    }
}
