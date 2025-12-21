package com.example.tad_bank_t1.ui.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.enums.UserStatusOnlOff;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.fragment.ai.AIPredictFragment;
import com.example.tad_bank_t1.ui.fragment.officer.AccountListOfficerFragment;
import com.example.tad_bank_t1.ui.fragment.officer.DashboardFragment;
import com.example.tad_bank_t1.ui.fragment.officer.MortgageAcountFragment;
import com.example.tad_bank_t1.ui.fragment.officer.SavingsAccountFragment;
import com.example.tad_bank_t1.ui.fragment.officer.fragment_customer_profile;
import com.example.tad_bank_t1.ui.fragment.officer.saving.SavingPolicyListFragment;
import com.nafis.bottomnavigation.NafisBottomNavigation;

public class OfficerMainActivity extends AppCompatActivity {
    private TextView tvName;
    private View loginRevealOverlay;
    private NafisBottomNavigation bottomNav;
    private String currentUserId;
    private int currentTabId = R.id.nav_dashboard;
    private ImageButton imbtLogout;
    private final UserRepository userRepo = new FirebaseUserRepository();
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        var tx = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.windmill_enter,
                        R.anim.windmill_exit,
                        R.anim.windmill_pop_enter,
                        R.anim.windmill_pop_exit
                )
                .replace(R.id.officer_fragment_container, fragment);

        if (addToBackStack) tx.addToBackStack(null);
        tx.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_officer_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (savedInstanceState == null) {
            navigateTo(new DashboardFragment(), false);
        }
        bottomNav = findViewById(R.id.bottom_nav_officer);
        bottomNav.show(R.id.nav_dashboard, false);
        loginRevealOverlay = findViewById(R.id.loginRevealOverlay);
        imbtLogout = findViewById(R.id.imbtLogout1);
        runLoginRevealCloseAnimation();
        tvName = findViewById(R.id.tvNameOfficer);
        currentUserId = getIntent().getStringExtra(MainActivity.EXTRA_USERID);
        userRepo.getById(currentUserId).addOnSuccessListener(user -> {
            if (user != null) {
                tvName.setText(user.getFullName());
            }
        }).addOnFailureListener(e -> {});
        imbtLogout.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerMainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        setupBottomNav();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUserId != null){
            userRepo.updateStatusOnlOff(currentUserId, UserStatusOnlOff.ONLINE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUserId != null) {
            userRepo.updateStatusOnlOff(currentUserId, UserStatusOnlOff.OFFLINE);
        }
    }

    private void runLoginRevealCloseAnimation() {
        if (loginRevealOverlay == null) return;

        // Giả lập trạng thái "đang phủ full màn" bằng scale rất lớn
        loginRevealOverlay.setVisibility(View.VISIBLE);
        loginRevealOverlay.setScaleX(20f);
        loginRevealOverlay.setScaleY(20f);
        loginRevealOverlay.setAlpha(1f);
        loginRevealOverlay.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(1200)
                .withEndAction(() -> {
                    loginRevealOverlay.setVisibility(View.GONE);
                    loginRevealOverlay.setScaleX(1f);
                    loginRevealOverlay.setScaleY(1f);
                    loginRevealOverlay.setAlpha(1f);
                })
                .start();

    }
    private void setupBottomNav(){
        bottomNav.add(new NafisBottomNavigation.Model(R.id.nav_dashboard, R.drawable.ic_dashboard));
        bottomNav.add(new NafisBottomNavigation.Model(R.id.nav_customers, R.drawable.ic_customers ));
        bottomNav.add(new NafisBottomNavigation.Model(R.id.nav_transactions, R.drawable.ic_transactions));
        bottomNav.add(new NafisBottomNavigation.Model(R.id.nav_reports, R.drawable.ic_reports));
        bottomNav.setOnClickMenuListener(model -> {
            int id = model.getId();
            if (id == currentTabId) return null;
            currentTabId = id;
            if (id == R.id.nav_dashboard) {
                navigateTo(new DashboardFragment(), false);
            }else if (id == R.id.nav_customers) {
                navigateTo(new AccountListOfficerFragment(), false);

            } else if (id == R.id.nav_transactions) {
                navigateTo(new SavingPolicyListFragment(), false);

            } else if (id == R.id.nav_reports) {
                navigateTo(new AIPredictFragment(), false);
            }
            return null;
        });
    }


}