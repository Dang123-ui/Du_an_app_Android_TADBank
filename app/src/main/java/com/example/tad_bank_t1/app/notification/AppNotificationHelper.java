package com.example.tad_bank_t1.app.notification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.ui.activity.MainActivity; // đổi theo activity của bạn

public class AppNotificationHelper {

    private static final String CHANNEL_ID = "txn_channel";
    private static final String CHANNEL_NAME = "Transaction Alerts";

    public static void showTransactionNoti(Context context, Notification noti) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(ch);
        }

        // bấm noti mở app (tuỳ bạn mở màn lịch sử giao dịch)
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("open_notification_id", noti.getNotificationId());
        intent.putExtra("related_txn_id", noti.getRelatedId());

        PendingIntent pi = PendingIntent.getActivity(
                context,
                0,
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_IMMUTABLE
                        : 0
        );

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home_notify2) // tạo icon này trong drawable
                .setContentTitle(noti.getTitle())
                .setContentText(noti.getMessage())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(noti.getMessage()))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi);

        nm.notify((int) System.currentTimeMillis(), b.build());
    }
}
