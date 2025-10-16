package com.example.tad_bank_t1.data.fake_data;

import com.example.tad_bank_t1.data.model.Notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotiFakeData {
    private static List<Notification> data = List.of(
            new Notification("user1", "Bien dong 1", "Bạn đã chuyển 500.000đ cho Nguyễn Văn A.", "SYSTEM", new Date()),
            new Notification("user2", "Bien dong 2", "Bạn đã chuyển 500.000đ cho Nguyễn Văn A." +
                    "Bạn đã chuyển 500.000đ cho Nguyễn Văn A." +
                    "Bạn đã chuyển 500.000đ cho Nguyễn Văn A." +
                    "Bạn đã chuyển 500.000đ cho Nguyễn Văn A.", "SYSTEM", new Date()),
            new Notification("user3", "Bien dong 3", "Bạn đã chuyển 500.000đ cho Nguyễn Văn A." +
                    "Bạn đã chuyển 500.000đ cho Nguyễn Văn A." +
                    "Bạn đã chuyển 500.000đ cho Nguyễn Văn A.", "SYSTEM", new Date()),
            new Notification("user4", "Bien dong 4", "Bạn đã chuyển 500.000đ cho Nguyễn Văn A.", "SYSTEM", new Date()),
            new Notification("user5", "Bien dong 5", "Bạn đã chuyển 500.000đ cho Nguyễn Văn A. So du hien tai la 1000000", "SYSTEM", new Date()),
            new Notification("user6", "Bien dong 6", "Bạn đã chuyển 500.000đ cho Nguyễn Văn A.", "SYSTEM", new Date())
    );

    public static List<Notification> getNotiData() {
        return data;
    }

    public static List<Notification> search(String keyword) {
        List<Notification> result = new ArrayList<>();

        for (Notification noti : data) {
            if (noti.getTitle().toLowerCase().contains(keyword.toLowerCase()) || noti.getMessage().toLowerCase().contains(keyword.toLowerCase()))
                    result.add(noti);
        }

        return result;
    }
}
