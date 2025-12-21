package com.example.tad_bank_t1.ui.fragment.officer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewadapter.officer.AccountsCardAdapter;
import com.example.tad_bank_t1.ui.viewadapter.officer.PersonalCardAdapter;
import com.example.tad_bank_t1.ui.viewadapter.officer.RiskLogAdapter;
import com.example.tad_bank_t1.ui.viewmodel.officer.AccountsCardViewModel;
import com.example.tad_bank_t1.ui.viewmodel.officer.PersonalCardUiState;
import com.example.tad_bank_t1.ui.viewmodel.officer.PersonalCardViewModel;
import com.example.tad_bank_t1.ui.viewmodel.officer.SecurityRiskCardViewModel;
import com.example.tad_bank_t1.ui.viewmodel.officer.UserAdminActionViewModel;
import com.example.tad_bank_t1.util.PhoneUtil;
import com.google.android.material.button.MaterialButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

public class CustomUserProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "arg_user_id";

    // FragmentResult keys
    public static final String REQ_PERSONAL_UPDATED = "personal_updated";
    public static final String KEY_UPDATED = "updated";

    private String uid;
    private PersonalCardViewModel personalVm;
    private PersonalCardAdapter personalAdapter;

    private AccountsCardViewModel accountsVM;
    private AccountsCardAdapter accountsAdapter;

    private SecurityRiskCardViewModel riskVm;
    private RiskLogAdapter riskLogAdapter;

    private BoomMenuButton bmbMore;
    private UserAdminActionViewModel adminVm;
    private boolean isLockedNow = false;


    public CustomUserProfileFragment() {}

    public static CustomUserProfileFragment newInstance(String userId) {
        CustomUserProfileFragment f = new CustomUserProfileFragment();
        Bundle b = new Bundle();
        b.putString(ARG_USER_ID, userId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            uid = args.getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (uid == null || uid.isEmpty()) {
            Toast.makeText(requireContext(), "User ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        bmbMore = view.findViewById(R.id.bmbMore);
        adminVm = new ViewModelProvider(this).get(UserAdminActionViewModel.class);
        adminVm.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        // =========================
        // CARD 1) PERSONAL
        // =========================
        // 1) ViewModel
        personalVm = new ViewModelProvider(this).get(PersonalCardViewModel.class);
        setupBoomMenu();

        // 2) Listen result from Dialog: when save success -> reload user -> avatar refresh
        getParentFragmentManager().setFragmentResultListener(
                REQ_PERSONAL_UPDATED,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean updated = bundle != null && bundle.getBoolean(KEY_UPDATED, false);
                    if (updated) {
                        personalVm.start(uid); // reload state -> UI updates avatar immediately
                    }
                }
        );

        // 3) Adapter
        View cardPersonalRoot = view.findViewById(R.id.cardPersonal);
        personalAdapter = new PersonalCardAdapter(cardPersonalRoot, new PersonalCardAdapter.Actions() {
            @Override
            public void onEdit() {
                EditPersonalCardDialogFragment dialog =
                        EditPersonalCardDialogFragment.newInstance(uid);
                dialog.show(getParentFragmentManager(), "EditPersonalCardDialog");
            }

            @Override
            public void onCall(String phone) {
                String e164 = PhoneUtil.toVnLocalDisplay(phone);
                if (e164.isEmpty()) return;
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + e164)));
            }

            @Override
            public void onSms(String phone) {
                String e164 = PhoneUtil.toE164Vn(phone);
                if (e164.isEmpty()) return;
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + e164)));
            }

            @Override
            public void onEmail(String email) {
                if (email.isEmpty()) return;
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                startActivity(i);
            }
        });

        // 4) Observe UI state
        personalVm.getState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            personalAdapter.bind(state);
            isLockedNow = isUserLocked(state);
        });

        personalVm.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        // 5) Start loading
        personalVm.start(uid);

        // =========================
        // CARD 2) ACCOUNTS
        // =========================

        accountsVM = new ViewModelProvider(this).get(AccountsCardViewModel.class);
        View cardAccountsRoot = view.findViewById(R.id.cardAccounts);
        if (cardAccountsRoot == null) {
            Toast.makeText(requireContext(),
                    "Missing view: R.id.cardAccounts (Accounts card not found in layout)",
                    Toast.LENGTH_SHORT).show();
        }else{
            accountsAdapter = new AccountsCardAdapter(cardAccountsRoot, item -> {

            });
            accountsAdapter.wireFilterClicks(accountsVM);
            accountsVM.getState().observe(getViewLifecycleOwner(), state -> {
                if (state != null) accountsAdapter.bind(state);
            });
            accountsVM.getError().observe(getViewLifecycleOwner(), err -> {
                if (err != null) {
                    Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
                }
            });
            accountsVM.start(uid);
        }
        // =========================
        // CARD 5) SECURITY & RISK
        // =========================
        riskVm = new ViewModelProvider(this).get(SecurityRiskCardViewModel.class);
        View cardRiskRoot = view.findViewById(R.id.cardSecurityRisk);
        if (cardRiskRoot == null) {
            Toast.makeText(requireContext(),
                    "Missing view: R.id.cardSecurityRisk (Security & Risk card not found in layout)",
                    Toast.LENGTH_SHORT).show();
        } else {
            RecyclerView rv = cardRiskRoot.findViewById(R.id.rvRiskAlerts);
            MaterialButton btnLow = cardRiskRoot.findViewById(R.id.btnMarkLowRisk);
            MaterialButton btnMed = cardRiskRoot.findViewById(R.id.btnMarkMediumRisk);
            MaterialButton btnHigh = cardRiskRoot.findViewById(R.id.btnMarkHighRisk);
            TextView tvLastLoginValue = cardRiskRoot.findViewById(R.id.tvLastLoginValue);
            TextView tvRiskValue = cardRiskRoot.findViewById(R.id.tvRiskValue);
            riskLogAdapter = new RiskLogAdapter();
            rv.setLayoutManager(new LinearLayoutManager(requireContext()));
            rv.setHasFixedSize(false);
            rv.setAdapter(riskLogAdapter);

            // observe list logs
            riskVm.getLastLoginText().observe(getViewLifecycleOwner(), tvLastLoginValue::setText);
            riskVm.getRiskText().observe(getViewLifecycleOwner(), tvRiskValue::setText);
            riskVm.getRiskColor().observe(getViewLifecycleOwner(), tvRiskValue::setTextColor);
            riskVm.getLogs().observe(getViewLifecycleOwner(), list -> {
                if (list != null) riskLogAdapter.submitList(list);
            });

            // disable nút trùng state
            riskVm.getEnableLow().observe(getViewLifecycleOwner(), btnLow::setEnabled);
            riskVm.getEnableMedium().observe(getViewLifecycleOwner(), btnMed::setEnabled);
            riskVm.getEnableHigh().observe(getViewLifecycleOwner(), btnHigh::setEnabled);

            riskVm.getError().observe(getViewLifecycleOwner(), err -> {
                if (err != null && !err.isEmpty()) {
                    Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
                }
            });

            // click actions (reason tạm hardcode)
            btnLow.setOnClickListener(v1 -> riskVm.setRiskLow(uid, "Officer set LOW"));
            btnMed.setOnClickListener(v1 -> riskVm.setRiskMedium(uid, "Officer set MEDIUM"));
            btnHigh.setOnClickListener(v1 -> riskVm.setRiskHigh(uid, "Officer set HIGH"));

            // start listen
            riskVm.start(uid);
        }
    }
    private void setupBoomMenu() {
        if(bmbMore == null) return;
        bmbMore.clearBuilders();
        bmbMore.addBuilder(new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_lock).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        if (isLockedNow) {
                            Toast.makeText(requireContext(), "User đã bị khóa rồi", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adminVm.lockUser(uid);
                        personalVm.start(uid);
                    }
                })
        );
        bmbMore.addBuilder(new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_unlock) // TODO: icon của bạn
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // Nếu chưa locked -> toast
                        if (!isLockedNow) {
                            Toast.makeText(requireContext(), "User đang ACTIVE, không cần mở khóa", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adminVm.unlockUser(uid);
                        personalVm.start(uid);
                    }
                })
        );
    }
    private boolean isUserLocked(PersonalCardUiState state) {
        return state != null
                && state.status != null
                && state.status.trim().equalsIgnoreCase("locked");
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (personalVm != null) personalVm.stop();
        if (accountsVM != null) accountsVM.stop();
        if (riskVm != null) riskVm.stop();
    }

}
