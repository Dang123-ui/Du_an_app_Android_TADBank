package com.example.tad_bank_t1.util;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {
    public static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");

    public static ZonedDateTime toUTC(LocalDateTime localDateTime, ZoneId zoneId) {

// 3. Kết hợp lại để tạo ZonedDateTime (Thời điểm tại múi giờ đó)
        ZonedDateTime localZonedDateTime = localDateTime.atZone(zoneId);
// Kết quả: 2025-10-10T14:00:00+07:00[Asia/Ho_Chi_Minh]

// 4. CHUẨN HÓA: Chuyển đổi sang UTC
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
// Kết quả: 2025-10-10T07:00:00Z[UTC]
// (14:00 tại HCM (GMT+7) tương đương 07:00 tại UTC)

// 5. LƯU TRỮ: Lấy mốc thời gian Instant (lý tưởng để lưu vào database)
        Instant instant = localZonedDateTime.toInstant();
// Kết quả (cho database): 2025-10-10T07:00:00Z
        return localZonedDateTime;
    }

    public static String toLocalDateTimeStr(ZonedDateTime zonedDateTime) {
        // 2. Chuyển đổi: Lấy cùng một mốc thời gian nhưng áp dụng múi giờ VN
        ZonedDateTime eventVietnam = zonedDateTime.withZoneSameInstant(LOCAL_ZONE_ID);
// Kết quả (Giờ VN): 2025-10-20T07:30:00+07:00[Asia/Ho_Chi_Minh]
// (9:30 Tokyo tương đương 7:30 VN, vì VN chậm hơn 2 tiếng)

// 3. Định dạng để hiển thị cho người dùng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));

        return eventVietnam.format(formatter);
    }

    public static Date localDateTimeToDateUTC(LocalDateTime localDateTime){
        Instant instantUTC = localDateTime.atZone(LOCAL_ZONE_ID).toInstant();

        // Chuyển đổi Instant sang java.util.Date
        Date dateToStore = Date.from(instantUTC);

        return dateToStore;
    }

    public static LocalDateTime dateUTCToLocalDate(Date dateUTC){
        // 1. Chuyển đổi java.util.Date sang Instant (mốc thời gian tuyệt đối)
        Instant instantUTC = dateUTC.toInstant();

        // 2. Chuyển Instant sang ZonedDateTime, áp dụng múi giờ địa phương
        ZonedDateTime zonedDateTime = instantUTC.atZone(LOCAL_ZONE_ID);

        // 3. Lấy LocalDateTime (bỏ thông tin múi giờ, chỉ giữ Ngày/Giờ)
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        return localDateTime;
    }

    public static String localDateTimeToStr(Date dateUTC){
        LocalDateTime localDateTime = dateUTCToLocalDate(dateUTC);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));

        return localDateTime.format(formatter);
    }
}
