package com.example.tad_bank_t1.saving;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.saving.adapter.SavingsAccountAdapter;
import com.example.tad_bank_t1.saving.model.AccountStatus;
import com.example.tad_bank_t1.saving.model.PaymentMethod;
import com.example.tad_bank_t1.saving.model.SavingsAccount;
import com.example.tad_bank_t1.saving.utils.FormatUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SavingsOverviewActivity extends AppCompatActivity {

    private TextView tvTotalBalance;
    private TextView tvActiveCount;
    private RecyclerView recyclerActiveAccounts;
    private RecyclerView recyclerClosedAccounts;
    private LinearLayout sectionActiveAccounts;
    private LinearLayout sectionClosedAccounts;
    private LinearLayout emptyState;
    private FloatingActionButton fabAddAccount;

    private SavingsAccountAdapter activeAccountsAdapter;
    private SavingsAccountAdapter closedAccountsAdapter;

    private List<SavingsAccount> allAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_overview);

        initViews();
        setupToolbar();
        setupRecyclerViews();
        loadSampleData();
        setupListeners();
    }

    private void initViews() {
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        recyclerActiveAccounts = findViewById(R.id.recyclerActiveAccounts);
        recyclerClosedAccounts = findViewById(R.id.recyclerClosedAccounts);
        sectionActiveAccounts = findViewById(R.id.sectionActiveAccounts);
        sectionClosedAccounts = findViewById(R.id.sectionClosedAccounts);
        emptyState = findViewById(R.id.emptyState);
        fabAddAccount = findViewById(R.id.fabAddAccount);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerViews() {
        recyclerActiveAccounts.setLayoutManager(new LinearLayoutManager(this));
        activeAccountsAdapter = new SavingsAccountAdapter(new ArrayList<>(), account -> {
            // Mở màn hình chi tiết
            Intent intent = new Intent(this, AccountDetailActivity.class);
            startActivity(intent);
        });
        recyclerActiveAccounts.setAdapter(activeAccountsAdapter);

        recyclerClosedAccounts.setLayoutManager(new LinearLayoutManager(this));
        closedAccountsAdapter = new SavingsAccountAdapter(new ArrayList<>(), account -> {
            // Mở màn hình chi tiết
            Intent intent = new Intent(this, AccountDetailActivity.class);
            startActivity(intent);
        });
        recyclerClosedAccounts.setAdapter(closedAccountsAdapter);
    }

    private void loadSampleData() {
        allAccounts = new ArrayList<>();

        allAccounts.add(new SavingsAccount(
                "1",
                "STK-001-2024-0001",
                150000000,
                "VND",
                "2024-01-15",
                "2025-01-15",
                6.5,
                PaymentMethod.MONTHLY,
                AccountStatus.ACTIVE
        ));

        allAccounts.add(new SavingsAccount(
                "2",
                "STK-001-2024-0002",
                75000000,
                "VND",
                "2024-03-10",
                "2024-09-10",
                5.8,
                PaymentMethod.QUARTERLY,
                AccountStatus.ACTIVE
        ));

        allAccounts.add(new SavingsAccount(
                "3",
                "STK-001-2023-0015",
                200000000,
                "VND",
                "2023-06-01",
                "2024-06-01",
                7.2,
                PaymentMethod.MONTHLY,
                AccountStatus.CLOSED
        ));

        updateUI();
    }

    private void updateUI() {
        if (allAccounts.isEmpty()) {
            showEmptyState();
            return;
        }

        hideEmptyState();

        List<SavingsAccount> activeAccounts = allAccounts.stream()
                .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .collect(Collectors.toList());

        List<SavingsAccount> closedAccounts = allAccounts.stream()
                .filter(acc -> acc.getStatus() == AccountStatus.CLOSED)
                .collect(Collectors.toList());

        activeAccountsAdapter.updateData(activeAccounts);
        closedAccountsAdapter.updateData(closedAccounts);

        long totalBalance = activeAccounts.stream()
                .mapToLong(SavingsAccount::getBalance)
                .sum();

        tvTotalBalance.setText(FormatUtils.formatCurrency(totalBalance, "VND"));
        tvActiveCount.setText(getString(R.string.active_accounts_count, activeAccounts.size()));

        sectionActiveAccounts.setVisibility(activeAccounts.isEmpty() ? View.GONE : View.VISIBLE);
        sectionClosedAccounts.setVisibility(closedAccounts.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        sectionActiveAccounts.setVisibility(View.GONE);
        sectionClosedAccounts.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
    }

    private void setupListeners() {
        fabAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnOpenFirstAccount).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }
}

