package com.example.tad_bank_t1.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.ActivityMainBinding;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.BankTransferFragment;
import com.example.tad_bank_t1.ui.fragment.customer.HomeCustomerFragment;
import com.example.tad_bank_t1.ui.fragment.customer.setting.SettingFragment;
import com.example.tad_bank_t1.ui.viewmodel.PaymentReturnViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private SessionViewModel sessionViewModel;
    private PaymentReturnViewModel paymentReturnVM;

    public static final String EXTRA_USERID = "extra_userid";

    private static final int REQ_NOTI = 1001;


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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        setContentView(R.layout.activity_main);

        initView();

        initAndObserverViewModel();

        initEvents();


        // permission
        ensureNotiPermission();

        // intent payment
        handleDeepLink(getIntent());
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private  void setUpToolbarAndBottom(){
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainCustomer, (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            // Appbar né status bar
            binding.appbar.setPadding(
                    binding.appbar.getPaddingLeft(),
                    status.top,
                    binding.appbar.getPaddingRight(),
                    binding.appbar.getPaddingBottom()
            );

            // Card bottom nav né navigation bar (giữ margin gốc 12dp)
            ViewGroup.MarginLayoutParams lp =
                    (ViewGroup.MarginLayoutParams) binding.cardBottomNav.getLayoutParams();
            lp.bottomMargin = nav.bottom;
            binding.cardBottomNav.setLayoutParams(lp);

            return insets;
        });

        // back icon
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_previous_activity2);

        // margin status bar
//        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, insets) -> {
//            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
//
//            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//            lp.topMargin = topInset;    // ⭐ auto margin theo status bar
//            v.setLayoutParams(lp);
//
//            return WindowInsetsCompat.CONSUMED;
//        });
//
//        // bottom nav inset
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // giữ sát đáy, không chừa khoảng
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    0  // KHÔNG dùng bottom inset
            );

            return WindowInsetsCompat.CONSUMED;
        });

    }
    private void initView() {
        setSupportActionBar(binding.toolbar);

        setUpToolbarAndBottom();

        // home is Ui default
        replaceFragment(new HomeCustomerFragment(), false);
    }

    private void initAndObserverViewModel() {
        // load user account bang session viewmodel
        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        paymentReturnVM = new ViewModelProvider(this).get(PaymentReturnViewModel.class);

        Intent intent = getIntent();
        String userId = null;
        if (intent != null) {
            userId = intent.getStringExtra(EXTRA_USERID);
        }
        if (userId != null) {
            sessionViewModel.setUserId(userId);
//            sessionViewModel.loadCurrentUserAndAccounts(userId);
            sessionViewModel.observeUserAndAccountsRealtime(userId);
            sessionViewModel.isLoading.observe(this, isLoading -> {
                if (isLoading) {
                    showLoading(true);
                } else {
                    showLoading(false);
                }
            });
        }
    }

    private void initEvents() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    getSupportFragmentManager().executePendingTransactions();
                    Fragment current = getSupportFragmentManager()
                            .findFragmentById(R.id.frame_main_container);
                    updateUIForFragment(current);
//                    checkCurrentFragment();
                } else {
                    // Cho phép hành vi mặc định (thoát app)
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Sự kiện chọn bottom navigation
        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeCustomerFragment();
                replaceFragment(fragment, false);
                return true;
            } else if (id == R.id.nav_transfer) {
                fragment = new BankTransferFragment();
                replaceFragment(fragment, true);
                return true;
            } else if (id == R.id.nav_setting) {
                fragment = new SettingFragment();
                replaceFragment(fragment, true);
                return true;
            }
            return false;
        });

        // Nút back trên toolbar
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
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
//            replaceFragment(new HomeCustomerFragment(), false);
            navigateHomeAndClearStack();
//            bottomNav.setSelectedItemId(R.id.nav_home);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ensureNotiPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTI);
            }
        }
    }


    // =============================
    // ==== start control fragment
    // =============================

    // open fragment
    public void openFeatureFragment(Fragment fragment, String title) {
        replaceFragment(fragment, true);
    }

    public void clearBackStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_main_container, fragment);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();

        // CHỈ update UI sau khi view đã tạo xong (viewLifecycleOwnerAvailable)
        fragment.getViewLifecycleOwnerLiveData().observe(this, owner -> {
            if (owner != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    updateUIForFragment(fragment);
                    animateToolbar(fragment);
                });
            }
        });

    }

    private void updateUIForFragment(Fragment fragment) {
        if (fragment instanceof UiConfig) {
            if (((UiConfig) fragment).showAppBar()) {
                binding.toolbar.setTitle(((UiConfig) fragment).getAppBarTitle());
                binding.appbar.setVisibility(View.VISIBLE);
            } else {
                binding.appbar.setVisibility(View.GONE);  // hide appbar
            }
            binding.bottomNav.setVisibility(((UiConfig) fragment).showBottomNav() ? View.VISIBLE : View.GONE);
        }
    }


    // animate toolbar
    private void animateToolbar(Fragment fragment) {
        if (!(fragment instanceof UiConfig)) return;

        UiConfig ui = (UiConfig) fragment;

        if (!ui.showAppBar()) {
            binding.toolbar.setVisibility(View.GONE);
            return;
        }

        binding.toolbar.setVisibility(View.VISIBLE);
        binding.toolbar.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.toolbar_slide_up)
        );
    }


    // Tro ve Home Fragment Clear stack
    public void navigateHomeAndClearStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_main_container, new HomeCustomerFragment());
        ft.commit();

        // show bottom nav, hide toolbar
        binding.bottomNav.setVisibility(View.VISIBLE);
        binding.bottomNav.setSelectedItemId(R.id.nav_home);
        binding.toolbar.setVisibility(View.GONE);

    }

    // =============================
    // ==== END control fragment
    // =============================


    // =============================
    // ==== start control loading
    // =============================
    // ✅ Hàm show/hide loading
    private void showLoading(boolean show) {
        if (show) {
            binding.lottieLoadingWaitingRedirect.setVisibility(View.VISIBLE);
//            binding.toolbar.setVisibility(View.GONE);
//            binding.bottomNav.setVisibility(View.GONE);
            binding.mainCustomer.setEnabled(false);
        } else {
            binding.lottieLoadingWaitingRedirect.setVisibility(View.GONE);
//            binding.toolbar.setVisibility(View.GONE);
//            binding.bottomNav.setVisibility(View.VISIBLE);
            binding.mainCustomer.setEnabled(true);
        }
    }

    // hàm show/hide loading features
    public void showLoadingFeature(boolean show) {
        binding.lottieLoadingWaitingRedirect.setEnabled(!show);
        binding.lottieLoadingWaitingRedirect.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loadingMainIcon.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // =============================
    // ==== END control loading
    // =============================

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    // intent web payment
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data == null) return;

        if ("tadbank".equals(data.getScheme()) && "vnpay_return".equals(data.getHost())) {
            paymentReturnVM.publish(data);
        }
    }

    // end intent web payment
}
