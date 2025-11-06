package com.example.tad_bank_t1.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;

public class SplashActivity extends AppCompatActivity {
    private static final String PREFS = "AppPrefs";
    private static final String KEY_HAS_REGISTERED = "hasRegistered";
    private static final String KEY_LAST_UID = "lastUserId";

    private final UserRepository userRepo = new FirebaseUserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(this, "Chào mừng trở lại, chúc quý khách một ngày tốt lành!", Toast.LENGTH_LONG).show();
        ImageView logoSplash = findViewById(R.id.logoSplash);
        ViewCompat.setTransitionName(logoSplash, "app_logo");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet shared = new TransitionSet()
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform());
            shared.setDuration(1100); // 1000–1300ms là mượt
            shared.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            shared.setPathMotion(new ArcMotion());
            getWindow().setSharedElementEnterTransition(shared);
            getWindow().setSharedElementReturnTransition(shared);
            getWindow().setSharedElementsUseOverlay(true);
        }

        // Delay ngắn để thấy splash (tuỳ chỉnh 800–1500ms)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            boolean hasRegistered = prefs.getBoolean(KEY_HAS_REGISTERED, false);
            String lastUid = prefs.getString(KEY_LAST_UID, null);

            if (hasRegistered && lastUid != null) {
                // Xác thực lại user còn tồn tại trên Firestore
                userRepo.getById(lastUid).addOnSuccessListener(user -> {
                    if (user != null) {
                        // → LoginActivity2 (truyền UID) + morph logo
                        Intent intent = new Intent(SplashActivity.this, LoginActivity2.class);
                        intent.putExtra(LoginActivity2.EXTRA_UID, lastUid);
                        ActivityOptionsCompat opts = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                SplashActivity.this, new Pair<>(logoSplash, "app_logo"));
                        startActivity(intent, opts.toBundle());
                        supportFinishAfterTransition();
                    } else {
                        // user bị xoá → reset cờ, về LoginActivity + morph
                        prefs.edit().putBoolean(KEY_HAS_REGISTERED, false).remove(KEY_LAST_UID).apply();
                        goToLoginWithMorph(logoSplash);
                    }
                }).addOnFailureListener(e -> {
                    // Lỗi mạng → cho về LoginActivity + morph
                    goToLoginWithMorph(logoSplash);
                });
            } else {
                // Chưa đăng ký → LoginActivity + morph
                goToLoginWithMorph(logoSplash);
            }
        }, 1200);
    }

    private void goToLoginWithMorph(ImageView logoSplash) {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        ActivityOptionsCompat opts = ActivityOptionsCompat.makeSceneTransitionAnimation(
                SplashActivity.this, new Pair<>(logoSplash, "app_logo"));
        startActivity(intent, opts.toBundle());
        supportFinishAfterTransition();
    }
}
