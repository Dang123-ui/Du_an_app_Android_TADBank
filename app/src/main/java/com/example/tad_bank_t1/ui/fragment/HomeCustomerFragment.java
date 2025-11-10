package com.example.tad_bank_t1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.LoginActivity;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.fragment.customer.account.AccountListFragment;
import com.example.tad_bank_t1.ui.fragment.customer.ggmap.MapBranchFragment;
import com.example.tad_bank_t1.ui.fragment.customer.notification.NotiFragment;
import com.example.tad_bank_t1.ui.fragment.customer.payment.BillsPaymentFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionHistoryFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.BankTransferFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.MobileTopupTransferFragment;
import com.example.tad_bank_t1.ui.viewmodel.NotificationViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

public class HomeCustomerFragment extends Fragment {
    private LinearLayout lnloCardDepositPhone,
            lnloCardTranfer, lnloCardBillPayment,
            lnloHomeHistoryTransac, lnloHomeAccManagement,
            lnloCardFindBranch;
    private ShimmerFrameLayout shimmerCard;
    private LinearLayout layoutCardContent;
    private ImageButton imbtHomeNotify, imbtLogout;
    private TextView txtHomeUsername, txtHomeAccNumber, txtHomeBalance;
    private SessionViewModel sessionViewModel;
    private NotificationViewModel notificationViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_home_customer, container, false);
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
                requireActivity().supportStartPostponedEnterTransition();
                return true;
            }
        });
        // toolbar
        imbtHomeNotify = view.findViewById(R.id.imbtHomeNotify);
        imbtLogout = view.findViewById(R.id.imbtLogout);
        // layout card and skimmer
        shimmerCard = view.findViewById(R.id.shimmer_card);
        layoutCardContent = view.findViewById(R.id.layout_card_content);
        // account default
        txtHomeUsername = view.findViewById(R.id.txtHomeUsername);
        txtHomeAccNumber = view.findViewById(R.id.txtHomeAccNumber);
        txtHomeBalance = view.findViewById(R.id.txtHomeBalance);
        // layout account card
        lnloHomeHistoryTransac = view.findViewById(R.id.lnloHomeHistoryTransac);
        lnloHomeAccManagement = view.findViewById(R.id.lnloHomeAccManagement);

        // layout features card
        lnloCardTranfer = view.findViewById(R.id.lnloCardTranfer);
        lnloCardDepositPhone = view.findViewById(R.id.lnloCardDepositPhone);
        lnloCardBillPayment = view.findViewById(R.id.lnloCardBillPayment);
        lnloCardFindBranch = view.findViewById(R.id.lnloCardFindBranch);

        // session viewmodel
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        TextView txtBadgeNotify = view.findViewById(R.id.txtBadgeNotify);
        notificationViewModel.startListeningUnreadCount(((MainActivity)requireActivity()).EXTRA_USERID);
        notificationViewModel.unreadCount.observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                txtBadgeNotify.setVisibility(View.VISIBLE);
                txtBadgeNotify.setText(count > 99 ? "99+" : String.valueOf(count));
                Toast.makeText(getActivity(), "New notifications", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(getActivity(), "No unread notifications", Toast.LENGTH_SHORT).show();
                txtBadgeNotify.setVisibility(View.GONE);
            }
        });

        sessionViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                shimmerCard.setVisibility(View.VISIBLE);
                shimmerCard.startShimmer();
                layoutCardContent.setVisibility(View.GONE);
            } else {
                shimmerCard.stopShimmer();
                shimmerCard.setVisibility(View.GONE);
                layoutCardContent.setVisibility(View.VISIBLE);
            }
        });

        sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                txtHomeUsername.setText(account.getAccountName());
                txtHomeAccNumber.setText(account.getAccountNumber());
                txtHomeBalance.setText(String.valueOf(account.getBalance()));
            }
        });


        // onclick toolbar
        imbtHomeNotify.setOnClickListener(v -> featureCardOnClick(new NotiFragment(), getString(R.string.thong_bao)));
        imbtLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // onclick account card
        lnloHomeAccManagement.setOnClickListener(v -> featureCardOnClick(new AccountListFragment(), getString(R.string.danh_sach_tai_khoan)));
        lnloHomeHistoryTransac.setOnClickListener(v -> featureCardOnClick(new TransactionHistoryFragment(), getString(R.string.tai_khoan_hien_tai)));


        // on click listener for features card
        lnloCardTranfer.setOnClickListener(v -> featureCardOnClick(new BankTransferFragment(), getString(R.string.chuyen_tien)));
        lnloCardDepositPhone.setOnClickListener(v -> featureCardOnClick(new MobileTopupTransferFragment(), getString(R.string.nap_tien_dien_thoai)));
        lnloCardBillPayment.setOnClickListener(v -> featureCardOnClick(new BillsPaymentFragment(), getString(R.string.thanh_toan_hoa_don)));
        lnloCardFindBranch.setOnClickListener(v -> featureCardOnClick(new MapBranchFragment(), getString(R.string.tim_kiem_chi_nhanh)));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "HOME onstart");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "HOME onstop");

//         ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    public void featureCardOnClick(Fragment fragment, String title) {
        ((MainActivity) requireActivity())
                .openFeatureFragment(fragment, title);
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionViewModel sessionVM = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        TransactionViewModel txnVM = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        sessionVM.clearSelectedAccount();
        txnVM.clearSelection();
    }

}