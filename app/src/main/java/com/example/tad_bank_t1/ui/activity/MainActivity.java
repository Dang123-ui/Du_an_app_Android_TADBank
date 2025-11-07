package com.example.tad_bank_t1.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.saving.SavingsOverviewActivity;
import com.example.tad_bank_t1.ui.fragment.BankTransferFragment;
import com.example.tad_bank_t1.ui.fragment.HomeCustomerFragment;
import com.example.tad_bank_t1.ui.fragment.SettingFragment;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    private LottieAnimationView loadingAnim;
    private SessionViewModel sessionViewModel;

    public static final String EXTRA_USERID = "extra_userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.transition.TransitionSet shared = new android.transition.TransitionSet()
                    .addTransition(new android.transition.ChangeBounds())
                    .addTransition(new android.transition.ChangeTransform())
                    .addTransition(new android.transition.ChangeImageTransform());
            shared.setDuration(1000); // tăng nhẹ so với 1200 nếu còn nhanh, 1000–1400 là “ngon”
            shared.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            shared.setPathMotion(new android.transition.ArcMotion());
            getWindow().setSharedElementEnterTransition(shared);
            getWindow().setSharedElementReturnTransition(shared);
            supportPostponeEnterTransition();
        }
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
        loadingAnim = findViewById(R.id.lottie_loading_waiting_redirect);
        setSupportActionBar(toolbar);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    getSupportFragmentManager().executePendingTransactions();
                    Fragment current = getSupportFragmentManager()
                            .findFragmentById(R.id.frame_main_container);
                    updateUIForFragment(current);
                    checkCurrentFragment();
                } else {
                    // Cho phép hành vi mặc định (thoát app)
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        replaceFragment(new HomeCustomerFragment(), false);
        updateUIForFragment(new HomeCustomerFragment());

        // load user account bang session viewmodel
        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        Intent intent = getIntent();
        String userId = null;
        if (intent != null) {
            userId = intent.getStringExtra(EXTRA_USERID);
        }
        if (userId != null) {
            sessionViewModel.loadCurrentUserAndAccounts(userId);
            sessionViewModel.observeUserAndAccountsRealtime(userId);
            sessionViewModel.isLoading.observe(this, isLoading -> {
                if (isLoading) {
                    showLoading(true);
                } else {
                    showLoading(false);
                }
            });
        }
        checkCurrentFragment();
        // Sự kiện chọn bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeCustomerFragment();
                replaceFragment(fragment, false);
                updateUIForFragment(fragment);
                return true;
            } else if (id == R.id.nav_transfer) {
                fragment = new BankTransferFragment();
                replaceFragment(fragment, true);
                setupToolbar("Chuyển tiền", true);
                bottomNav.setVisibility(View.GONE);
                return true;
            } else if (id == R.id.nav_setting) {
                fragment = new SettingFragment();
                replaceFragment(fragment, true);
                setupToolbar("Cài đặt", true);
                bottomNav.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        // Nút back trên toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu); // 👈 nạp menu có icon home
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            // Khi bấm icon home → quay lại Home
            replaceFragment(new HomeCustomerFragment(), false);
            updateUIForFragment(new HomeCustomerFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openFeatureFragment(Fragment fragment, String title) {
        replaceFragment(fragment, true);
        setupToolbar(title, true);
        bottomNav.setVisibility(View.GONE);
    }

    public void setupToolbar(String title, boolean showBackButton) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(title);
        if (showBackButton) {
            toolbar.setNavigationIcon(R.drawable.ic_back_previous_activity);
        } else {
            toolbar.setNavigationIcon(null);
        }
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        var ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_main_container, fragment);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
        updateUIForFragment(fragment);
        checkCurrentFragment();
    }

    private void updateUIForFragment(Fragment fragment) {
        if (fragment instanceof HomeCustomerFragment) {
            toolbar.setVisibility(View.GONE);
            bottomNav.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            bottomNav.setVisibility(View.GONE);
        }
    }

    // ✅ Hàm show/hide loading
    private void showLoading(boolean show) {
        if (show) {
            loadingAnim.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            bottomNav.setVisibility(View.GONE);
        } else {
            loadingAnim.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    public void checkCurrentFragment() {
        // ID của container mà bạn dùng để host các Fragment (ví dụ:
        // R.id.fragment_container)
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_container);

        if (currentFragment instanceof HomeCustomerFragment) {
            // Fragment hiện tại là HomeCustomerFragment
            // Trong Activity/Fragment, sau khi View được tạo
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appbar), (v, insets) -> {
                // Lấy chiều cao của thanh trạng thái
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Áp dụng padding: Padding cũ + Chiều cao thanh trạng thái + Thêm khoảng cách
                // 10dp
                v.setPadding(v.getPaddingLeft(), 0, v.getPaddingRight(), v.getPaddingBottom());

                return insets;
            });
        } else {
            // Trong Activity/Fragment, sau khi View được tạo
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appbar), (v, insets) -> {
                // Lấy chiều cao của thanh trạng thái
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Áp dụng padding: Padding cũ + Chiều cao thanh trạng thái + Thêm khoảng cách
                // 10dp
                v.setPadding(v.getPaddingLeft(), (int) (systemBars.top * 0.75), v.getPaddingRight(),
                        v.getPaddingBottom());

                return insets;
            });
        }
    }

}
