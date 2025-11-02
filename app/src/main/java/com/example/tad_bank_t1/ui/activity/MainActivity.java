package com.example.tad_bank_t1.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.fragment.BankTransferFragment;
import com.example.tad_bank_t1.ui.fragment.HomeCustomerFragment;
import com.example.tad_bank_t1.ui.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
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

        // Hiển thị Home mặc định
        replaceFragment(new HomeCustomerFragment(), false);
        updateUIForFragment(new HomeCustomerFragment());
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
        if (addToBackStack) ft.addToBackStack(null);
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


    public void checkCurrentFragment() {
        // ID của container mà bạn dùng để host các Fragment (ví dụ: R.id.fragment_container)
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_container);

        if (currentFragment instanceof HomeCustomerFragment) {
            // Fragment hiện tại là HomeCustomerFragment
            // Trong Activity/Fragment, sau khi View được tạo
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appbar), (v, insets) -> {
                // Lấy chiều cao của thanh trạng thái
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Áp dụng padding: Padding cũ + Chiều cao thanh trạng thái + Thêm khoảng cách 10dp
                v.setPadding(v.getPaddingLeft(), 0, v.getPaddingRight(), v.getPaddingBottom());

                return insets;
            });
        } else{
            // Trong Activity/Fragment, sau khi View được tạo
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appbar), (v, insets) -> {
                // Lấy chiều cao của thanh trạng thái
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Áp dụng padding: Padding cũ + Chiều cao thanh trạng thái + Thêm khoảng cách 10dp
                v.setPadding(v.getPaddingLeft(), (int)(systemBars.top * 0.75), v.getPaddingRight(), v.getPaddingBottom());

                return insets;
            });
        }
    }

}
