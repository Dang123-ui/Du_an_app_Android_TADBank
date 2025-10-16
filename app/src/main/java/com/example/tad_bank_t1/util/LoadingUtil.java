package com.example.tad_bank_t1.util;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
public class LoadingUtil {
    public static void loadingRedirect(
            Context context,
            FragmentManager fragmentManager,
            View viewToDisable,
            LottieAnimationView lottieAnimationView) {

        lottieAnimationView.setVisibility(View.VISIBLE);
        viewToDisable.setEnabled(false);

        // Giả lập một tác vụ xử lý (ví dụ: gọi API)
        new Handler().postDelayed(() -> {
            // Khi tác vụ hoàn thành:
            lottieAnimationView.setVisibility(View.GONE);
            viewToDisable.setEnabled(true);
        }, 2000); // 2 giây
    }
}