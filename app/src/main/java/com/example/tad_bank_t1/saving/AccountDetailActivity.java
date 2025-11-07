package com.example.tad_bank_t1.saving;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.saving.model.AccountStatus;
import com.example.tad_bank_t1.saving.model.SavingsAccountDetail;
import com.example.tad_bank_t1.saving.utils.FormatUtils;

public class AccountDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvStatusBadge;
    private TextView tvAccountNumber;
    private TextView tvBranch;
    private TextView tvStartDate;
    private TextView tvMaturityDate;
    private TextView tvPolicyName;
    private TextView tvInterestRate;
    private TextView tvTermLength;
    private TextView tvInterestPayment;
    private TextView tvPrincipal;
    private TextView tvEstimatedInterest;
    private TextView tvTotalMaturity;
    private View layoutAlert;
    private LinearLayout layoutActions;
    private Button btnCloseAccount;
    private Button btnExtendTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        initViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvBranch = findViewById(R.id.tvBranch);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvMaturityDate = findViewById(R.id.tvMaturityDate);
        tvPolicyName = findViewById(R.id.tvPolicyName);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvTermLength = findViewById(R.id.tvTermLength);
        tvInterestPayment = findViewById(R.id.tvInterestPayment);
        tvPrincipal = findViewById(R.id.tvPrincipal);
        tvEstimatedInterest = findViewById(R.id.tvEstimatedInterest);
        tvTotalMaturity = findViewById(R.id.tvTotalMaturity);
        layoutAlert = findViewById(R.id.layoutAlert);
        layoutActions = findViewById(R.id.layoutActions);
        btnCloseAccount = findViewById(R.id.btnCloseAccount);
        btnExtendTerm = findViewById(R.id.btnExtendTerm);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCloseAccount.setOnClickListener(v -> handleCloseAccount());

        btnExtendTerm.setOnClickListener(v -> handleExtendTerm());
    }

    private void loadData() {
        // Sample data - replace with actual data from Intent
        SavingsAccountDetail account = new SavingsAccountDetail(
            "1",
            "STK-001-2024-0001",
            "Chi nhánh Quận 1, TP. Hồ Chí Minh",
            150_000_000,
            "VND",
            "2024-01-15",
            "2025-01-15",
            6.5,
            "Tiết kiệm Phát Lộc - Kỳ hạn 12 tháng",
            "12 tháng",
            "Nhận lãi cuối kỳ",
            9_750_000,
            159_750_000,
            AccountStatus.ACTIVE
        );

        displayAccountDetail(account);
    }

    private void displayAccountDetail(SavingsAccountDetail account) {
        // Status badge
        tvStatusBadge.setText(account.getStatus().toDisplayString());
        tvStatusBadge.setBackgroundResource(
            FormatUtils.getStatusBadgeDrawable(account.getStatus())
        );
        FormatUtils.ColorPair colors = FormatUtils.getStatusBadgeColors(account.getStatus());
        tvStatusBadge.setTextColor(colors.textColor);

        // General info
        tvAccountNumber.setText(account.getAccountNumber());
        tvBranch.setText(account.getBranch());
        tvStartDate.setText(FormatUtils.formatDate(account.getStartDate()));
        tvMaturityDate.setText(FormatUtils.formatDate(account.getMaturityDate()));

        // Interest policy
        tvPolicyName.setText(account.getPolicyName());
        tvInterestRate.setText(account.getInterestRate() + "%");
        tvTermLength.setText(account.getTermLength());
        tvInterestPayment.setText(account.getInterestPaymentMethod());

        // Current status
        tvPrincipal.setText(FormatUtils.formatCurrency(account.getBalance(), account.getCurrency()));
        tvEstimatedInterest.setText("+" + FormatUtils.formatCurrency(account.getEstimatedInterest(), account.getCurrency()));
        tvTotalMaturity.setText(FormatUtils.formatCurrency(account.getTotalAtMaturity(), account.getCurrency()));

        // Show/hide actions based on status
        if (account.getStatus() == AccountStatus.ACTIVE) {
            layoutAlert.setVisibility(View.VISIBLE);
            layoutActions.setVisibility(View.VISIBLE);
        } else {
            layoutAlert.setVisibility(View.GONE);
            layoutActions.setVisibility(View.GONE);
        }
    }

    private void handleCloseAccount() {
        Toast.makeText(this, "Chức năng đóng sổ tiết kiệm", Toast.LENGTH_SHORT).show();
        // TODO: Implement close account logic
    }

    private void handleExtendTerm() {
        Toast.makeText(this, "Chức năng gia hạn kỳ hạn mới", Toast.LENGTH_SHORT).show();
        // TODO: Implement extend term logic
    }
}

