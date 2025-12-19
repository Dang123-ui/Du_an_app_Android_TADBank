package com.example.tad_bank_t1.ui.fragment.customer.savings;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.databinding.FragmentSavingAccountCreateBinding;
import com.example.tad_bank_t1.databinding.FragmentSavingAccountDetailBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.SavingPolicyViewModel;
import com.example.tad_bank_t1.util.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavingAccountCreateFragment extends Fragment implements UiConfig, BaseCustomFragment {
    private static final String USER_ID = "userId";

    // TODO: Rename and change types of parameters
    private String userId;

    private FragmentSavingAccountCreateBinding binding;

    // view model
    private AccountViewModel accountViewModel;
    private SavingPolicyViewModel savingPolicyViewModel; 
    
    private List<SavingsRatePolicy> policies;
    private SavingsRatePolicy selectedPolicy;
    private Date selectedDate;


    public SavingAccountCreateFragment() {
        // Required empty public constructor
    }
 
    public static SavingAccountCreateFragment newInstance(String userId, String param2) {
        SavingAccountCreateFragment fragment = new SavingAccountCreateFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSavingAccountCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initFragment();


    }

    private void initViews(View view) { 
    }

    private void setupListeners() {
//        btnBack.setOnClickListener(v -> finish());

        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.btnConfirmCreate.setOnClickListener(v -> handleSubmit());

        binding.etAmount.addTextChangedListener(new TextWatcher() {
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
//        policies.add(new SavingsRatePolicy("1", "Tiết kiệm Phát Lộc", 12, 6.5));
//        policies.add(new SavingsRatePolicy("2", "Tiết kiệm Linh hoạt", 6, 5.8));
//        policies.add(new SavingsRatePolicy("3", "Tiết kiệm Thịnh Vượng", 12, 7.2));
//        policies.add(new SavingsRatePolicy("4", "Tiết kiệm Tích Lũy", 24, 7.0));
    }

    private void setupPolicySpinner() {
        List<String> policyNames = new ArrayList<>();
        for (SavingsRatePolicy policy : policies) {
            policyNames.add(policy.getPolicyName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                policyNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPolicy.setAdapter(adapter);

        binding.spinnerPolicy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            binding.layoutPolicyInfo.setVisibility(View.VISIBLE);
            binding.tvPolicyName.setText(selectedPolicy.getPolicyName());
            binding.tvPolicyDetail.setText(getString(
                    R.string.policy_detail_format,
                    selectedPolicy.getTermMonths(),
                    selectedPolicy.getInterestRate()
            ));
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
                    binding.btnSelectDate.setText(sdf.format(selectedDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateEstimation() {
        String amountStr = binding.etAmount.getText().toString();

        if (selectedPolicy != null && !amountStr.isEmpty()) {
            try {
                long amount = Long.parseLong(amountStr);
                if (amount > 0) {
                    double rate = selectedPolicy.getInterestRate() / 100;
                    int months = selectedPolicy.getTermMonths();
                    long interest = (long) (amount * rate * months / 12);
                    long total = amount + interest;

                    binding.tvEstimatedInterest.setText("+" + FormatUtils.formatCurrency(interest, "VND"));
                    binding.tvTotalReceive.setText(FormatUtils.formatCurrency(total, "VND"));
                    binding.cardEstimation.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid number
            }
        }

        binding.cardEstimation.setVisibility(View.GONE);
    }

    private void handleSubmit() {
        // Validate
        if (selectedPolicy == null) {
            Toast.makeText(requireActivity(), "Vui lòng chọn chính sách", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = binding.etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(requireActivity(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long amount = Long.parseLong(amountStr);
            if (amount < 1000000) {
                Toast.makeText(requireActivity(), "Số tiền tối thiểu là 1.000.000 ₫", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireActivity(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(requireActivity(), "Vui lòng chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show OTP dialog
        showOTPDialog();
    }

    private void showOTPDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_otp, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
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
            Toast.makeText(requireActivity(), R.string.otp_resent, Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(requireActivity(), R.string.success_account_created, Toast.LENGTH_LONG).show();
//                finish();
            }
        });

        dialog.show();
    }

    @Override
    public void initView() {
        initViews(binding.getRoot());
        setupPolicySpinner();
    }

    @Override
    public void initViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        savingPolicyViewModel = new ViewModelProvider(requireActivity()).get(SavingPolicyViewModel.class);

        accountViewModel.getCreateState().observe(getViewLifecycleOwner(), result -> {

        });

        savingPolicyViewModel.getAll();
        savingPolicyViewModel.getListState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isLoading()) {
                toggleLoading(true);
            }
            toggleLoading(false);


            if (result.getData() != null) {
                policies = result.getData();
                setupPolicySpinner();
            }

            if (result.getError() != null) {
                showError(requireContext(), "Lỗi", result.getError());
            }
        });
    }

    @Override
    public void setUpEvents() {
        setupListeners();
    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    @Override
    public String getAppBarTitle() {
        return getString(R.string.tao_moi_tai_khoanr);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}