package com.example.tad_bank_t1.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class DataUriUtil {
    public static String extractPureBase64(String maybeDataUri) {
        if (maybeDataUri == null) return "";
        String s = maybeDataUri.trim();
        int comma = s.indexOf(',');
        if (comma >= 0 && s.substring(0, comma).toLowerCase().contains("base64")) {
            return s.substring(comma + 1).trim();
        }
        return s; // đã là base64 thuần
    }

    /** Decode dataUri/base64 -> Bitmap (an toàn) */
    public static Bitmap decodeToBitmap(String maybeDataUri) {
        try {
            String pure = extractPureBase64(maybeDataUri);
            if (pure.isEmpty()) return null;
            byte[] bytes = Base64.decode(pure, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    /** Tạo dataUri JPEG từ bytes (đúng format bạn đang lưu) */
    public static String toJpegDataUri(byte[] jpegBytes) {
        String b64 = Base64.encodeToString(jpegBytes, Base64.NO_WRAP);
        return "data:image/jpeg;base64," + b64;
    }
}
