package com.example.tad_bank_t1.saving;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.saving.model.CapitalizationMethod;
import com.example.tad_bank_t1.saving.model.SavingsPolicy;
import com.example.tad_bank_t1.saving.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAccountActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinnerPolicy;
    private View layoutPolicyInfo;
    private TextView tvPolicyName;
    private TextView tvPolicyDetail;
    private EditText etAmount;
    private Button btnSelectDate;
    private RadioGroup rgCapitalization;
    private View cardEstimation;
    private TextView tvEstimatedInterest;
    private TextView tvTotalReceive;
    private Button btnConfirmCreate;

    private List<SavingsPolicy> policies;
    private SavingsPolicy selectedPolicy;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initViews();
        setupListeners();
        loadPolicies();
        setupPolicySpinner();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        spinnerPolicy = findViewById(R.id.spinnerPolicy);
        layoutPolicyInfo = findViewById(R.id.layoutPolicyInfo);
        tvPolicyName = findViewById(R.id.tvPolicyName);
        tvPolicyDetail = findViewById(R.id.tvPolicyDetail);
        etAmount = findViewById(R.id.etAmount);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        rgCapitalization = findViewById(R.id.rgCapitalization);
        cardEstimation = findViewById(R.id.cardEstimation);
        tvEstimatedInterest = findViewById(R.id.tvEstimatedInterest);
        tvTotalReceive = findViewById(R.id.tvTotalReceive);
        btnConfirmCreate = findViewById(R.id.btnConfirmCreate);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnConfirmCreate.setOnClickListener(v -> handleSubmit());

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateEstimation();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void loadPolicies() {
        policies = new ArrayList<>();
        policies.add(new SavingsPolicy("1", "Tiết kiệm Phát Lộc", 12, 6.5));
        policies.add(new SavingsPolicy("2", "Tiết kiệm Linh hoạt", 6, 5.8));
        policies.add(new SavingsPolicy("3", "Tiết kiệm Thịnh Vượng", 12, 7.2));
        policies.add(new SavingsPolicy("4", "Tiết kiệm Tích Lũy", 24, 7.0));
    }

    private void setupPolicySpinner() {
        List<String> policyNames = new ArrayList<>();
        for (SavingsPolicy policy : policies) {
            policyNames.add(policy.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            policyNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPolicy.setAdapter(adapter);

        spinnerPolicy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPolicy = policies.get(position);
                updatePolicyInfo();
                updateEstimation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updatePolicyInfo() {
        if (selectedPolicy != null) {
            layoutPolicyInfo.setVisibility(View.VISIBLE);
            tvPolicyName.setText(selectedPolicy.getName());
            tvPolicyDetail.setText(getString(
                R.string.policy_detail_format,
                selectedPolicy.getTermMonths(),
                selectedPolicy.getInterestRate()
            ));
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                selectedDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
                btnSelectDate.setText(sdf.format(selectedDate));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateEstimation() {
        String amountStr = etAmount.getText().toString();

        if (selectedPolicy != null && !amountStr.isEmpty()) {
            try {
                long amount = Long.parseLong(amountStr);
                if (amount > 0) {
                    double rate = selectedPolicy.getInterestRate() / 100;
                    int months = selectedPolicy.getTermMonths();
                    long interest = (long) (amount * rate * months / 12);
                    long total = amount + interest;

                    tvEstimatedInterest.setText("+" + FormatUtils.formatCurrency(interest, "VND"));
                    tvTotalReceive.setText(FormatUtils.formatCurrency(total, "VND"));
                    cardEstimation.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid number
            }
        }

        cardEstimation.setVisibility(View.GONE);
    }

    private void handleSubmit() {
        // Validate
        if (selectedPolicy == null) {
            Toast.makeText(this, "Vui lòng chọn chính sách", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long amount = Long.parseLong(amountStr);
            if (amount < 1000000) {
                Toast.makeText(this, "Số tiền tối thiểu là 1.000.000 ₫", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show OTP dialog
        showOTPDialog();
    }

    private void showOTPDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_otp, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create();

        // Setup OTP fields
        EditText[] otpFields = new EditText[6];
        otpFields[0] = dialogView.findViewById(R.id.etOtp1);
        otpFields[1] = dialogView.findViewById(R.id.etOtp2);
        otpFields[2] = dialogView.findViewById(R.id.etOtp3);
        otpFields[3] = dialogView.findViewById(R.id.etOtp4);
        otpFields[4] = dialogView.findViewById(R.id.etOtp5);
        otpFields[5] = dialogView.findViewById(R.id.etOtp6);

        // Setup auto-focus
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < 5) {
                        otpFields[index + 1].requestFocus();
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        TextView tvOtpError = dialogView.findViewById(R.id.tvOtpError);
        TextView tvResendOtp = dialogView.findViewById(R.id.tvResendOtp);
        Button btnVerifyOtp = dialogView.findViewById(R.id.btnVerifyOtp);

        tvResendOtp.setOnClickListener(v -> {
            Toast.makeText(this, R.string.otp_resent, Toast.LENGTH_SHORT).show();
        });

        btnVerifyOtp.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText field : otpFields) {
                otp.append(field.getText().toString());
            }

            if (otp.length() != 6) {
                tvOtpError.setVisibility(View.VISIBLE);
                tvOtpError.setText(getString(R.string.otp_error_incomplete));
            } else {
                // Verify OTP (simulate success)
                dialog.dismiss();
                Toast.makeText(this, R.string.success_account_created, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        dialog.show();
    }
}

