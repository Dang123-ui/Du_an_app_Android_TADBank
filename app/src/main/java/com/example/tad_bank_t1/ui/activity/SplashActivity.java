package com.example.tad_bank_t1.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tad_bank_t1.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                 // 1) GỌI super trước
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);           // 2) setContentView trước khi findViewById

        // Insets (giữ nguyên như bạn đang dùng)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 3) Lấy view sau khi đã setContentView
        ImageView logoSplash = findViewById(R.id.logoSplash);

        // 4) Shared element transition (chỉ hoạt động từ API 21+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet shared = new TransitionSet()
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform());
            shared.setDuration(1500);
            getWindow().setSharedElementEnterTransition(shared);
            getWindow().setSharedElementReturnTransition(shared);
        }

        // 5) Điều hướng sang LoginActivity sau 1.5s với shared element "app_logo"
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            ActivityOptionsCompat opts = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    SplashActivity.this,
                    new Pair<>(logoSplash, "app_logo") // transitionName phải trùng bên Login
            );
            startActivity(intent, opts.toBundle());
            // Đóng Splash để không quay lại khi nhấn back
            supportFinishAfterTransition();
        }, 1500);
    }
}
