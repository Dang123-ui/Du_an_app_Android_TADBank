package com.example.tad_bank_t1.ui.fragment.customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.FragmentHomeCustomerBinding;
import com.example.tad_bank_t1.ui.activity.LoginActivity;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.account.AccountListFragment;
import com.example.tad_bank_t1.ui.fragment.customer.ggmap.MapBranchFragment;
import com.example.tad_bank_t1.ui.fragment.customer.notification.NotiFragment;
import com.example.tad_bank_t1.ui.fragment.customer.bills.BillsPaymentFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionHistoryFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.BankTransferFragment;
import com.example.tad_bank_t1.ui.fragment.customer.topup.MobileTopupFragment;
import com.example.tad_bank_t1.ui.viewmodel.BankViewModel;
import com.example.tad_bank_t1.ui.viewmodel.ExternalAccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.NotificationViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;

public class HomeCustomerFragment extends Fragment implements UiConfig {
    // View binding
    private FragmentHomeCustomerBinding binding;

    // View model
    private SessionViewModel sessionViewModel;
    private NotificationViewModel notificationViewModel;
    private BankViewModel bankViewModel;

    @Override
    public boolean showBottomNav() {
        return true;
    }

    @Override
    public boolean showAppBar() {
        return false;
    }

    @Override
    public String getAppBarTitle() {
        return "Home";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        binding = FragmentHomeCustomerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView homeLogo = view.findViewById(R.id.homeLogo);
        ViewCompat.setTransitionName(homeLogo, "app_logo");
        homeLogo.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                homeLogo.getViewTreeObserver().removeOnPreDrawListener(this);
                //requireActivity().supportStartPostponedEnterTransition();

                FragmentActivity act = getActivity();
                if (act != null) {
                    act.supportStartPostponedEnterTransition();
                }

                return true;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.lnloHeader, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.topMargin = topInset;    // ⭐ auto margin theo status bar
            v.setLayoutParams(lp);

            return WindowInsetsCompat.CONSUMED;
//            return insets;
        });


        initView(view);

        initAndObserveVM();

        setUpEvents();
    }

    private void initView(View view){
    }

    private void initAndObserveVM(){
        // session viewmodel
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        bankViewModel = new ViewModelProvider(this).get(BankViewModel.class);


        Log.d("TAG HOME", "initAndObserveVM:" + sessionViewModel.getUserId().getValue());

        notificationViewModel.startListeningUnreadCount(sessionViewModel.getUserId().getValue());
        notificationViewModel.unreadCount.observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                binding.txtBadgeNotify.setVisibility(View.VISIBLE);
                binding.txtBadgeNotify.setText(count > 99 ? "99+" : String.valueOf(count));

            } else {
//                Toast.makeText(getActivity(), "No unread notifications", Toast.LENGTH_SHORT).show();
                binding.txtBadgeNotify.setVisibility(View.GONE);
            }
        });

        sessionViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.shimmerCard.setVisibility(View.VISIBLE);
                binding.shimmerCard.startShimmer();
                binding.layoutCardContent.setVisibility(View.GONE);
            } else {
                binding.shimmerCard.stopShimmer();
                binding.shimmerCard.setVisibility(View.GONE);
                binding.layoutCardContent.setVisibility(View.VISIBLE);
            }
        });

        sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                binding.txtHomeUsername.setText(account.getAccountName());
                binding.txtHomeAccNumber.setText(account.getAccountNumber());
                binding.txtHomeBalance.setText(CurrencyUtil.formatAmount(account.getBalance()) + account.getCurrency());
            }
        });

        // moi lan ve home thi clear bank duoc chon truoc do
        bankViewModel.clearBankSelected();
    }

    private void setUpEvents(){
        // onclick toolbar
        binding.imbtHomeNotify.setOnClickListener(v -> featureCardOnClick(new NotiFragment(), getString(R.string.thong_bao)));
        binding.imbtLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // onclick account card
        binding.lnloHomeAccManagement.setOnClickListener(v -> featureCardOnClick(new AccountListFragment(), getString(R.string.danh_sach_tai_khoan)));
        binding.lnloHomeHistoryTransac.setOnClickListener(v -> featureCardOnClick(new TransactionHistoryFragment(), getString(R.string.tai_khoan_hien_tai)));


        // on click listener for features card
        binding.lnloCardTranfer.setOnClickListener(v -> {
            new ViewModelProvider(this)
                    .get(BankViewModel.class)
                    .clearBankSelected();

            new ViewModelProvider(this)
                    .get(ExternalAccountViewModel.class)
                    .clearAccounts();

            new ViewModelProvider(this)
                    .get(TransactionPayloadViewModel.class)
                    .clearPayload();

            featureCardOnClick(new BankTransferFragment(), getString(R.string.chuyen_tien));
        });
        binding.lnloCardDepositPhone.setOnClickListener(v -> featureCardOnClick(new MobileTopupFragment(), getString(R.string.nap_tien_dien_thoai)));
        binding.lnloCardTransactionHistory.setOnClickListener(v -> featureCardOnClick(new TransactionHistoryFragment(), getString(R.string.lich_su_giao_dich)));
        binding.lnloCardBillPayment.setOnClickListener(v -> featureCardOnClick(new BillsPaymentFragment(), getString(R.string.thanh_toan_hoa_don)));
        binding.lnloCardFindBranch.setOnClickListener(v -> featureCardOnClick(new MapBranchFragment(), getString(R.string.tim_kiem_chi_nhanh)));
    }

    public void featureCardOnClick(Fragment fragment, String title) {
        ((MainActivity) requireActivity())
                .openFeatureFragment(fragment, title);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionViewModel sessionVM = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        TransactionViewModel txnVM = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        sessionVM.clearSelectedAccount();
        txnVM.clearSelection();
        bankViewModel.clearBankSelected();
    }

    @Override
    public void onDestroyView() {
        notificationViewModel.stopListening();
        binding = null;
        super.onDestroyView();
    }
}