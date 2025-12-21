package com.example.tad_bank_t1.util;

public class PhoneUtil {
    public static String toVnLocalDisplay(String phone) {
        if (phone == null) return "";
        String p = phone.trim().replace(" ", "");

        if (p.startsWith("+84")) {
            return "0" + p.substring(3);
        }
        if (p.startsWith("84") && p.length() > 9) { // đôi khi lưu "84..."
            return "0" + p.substring(2);
        }
        return p; // đã là 0... hoặc dạng khác
    }

    /** Lưu về E.164 (+84...) từ input người dùng (0xxx hoặc +84xxx) */
    public static String toE164Vn(String input) {
        if (input == null) return "";
        String p = input.trim().replace(" ", "");

        if (p.startsWith("+84")) return p;
        if (p.startsWith("0")) return "+84" + p.substring(1);
        if (p.startsWith("84")) return "+" + p;

        // fallback: nếu người dùng nhập 9/10 số không có đầu 0
        if (p.matches("^\\d{9,10}$")) {
            // nếu 9 số => giả định thiếu 0
            if (p.length() == 9) return "+84" + p;
            // 10 số => giả định đã có 0 đầu? (vd 037...) nhưng bị bỏ 0
            return "+84" + p.substring(p.length() - 9);
        }
        return p;
    }
}
