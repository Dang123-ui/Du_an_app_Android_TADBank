package com.example.tad_bank_t1.ui.fragment.customer.savings;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.SavingsAccount;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.databinding.FragmentSavingAccountDetailBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.MockAccountFactory;
import com.example.tad_bank_t1.util.SavingUtil;

public class SavingAccountDetailFragment extends Fragment implements UiConfig, BaseCustomFragment {
    // truyen vao id tai khoan
    private static final String ACCOUNT_ID = "accountId";
    private String accountId;
    private Account currentAccount;

    private FragmentSavingAccountDetailBinding binding;

    // view model
    private AccountViewModel accountViewModel;
    private AccountType accountType = AccountType.SAVING;


    public SavingAccountDetailFragment() {
        // Required empty public constructor
    }

    
    public static SavingAccountDetailFragment newInstance(String accountId) {
        SavingAccountDetailFragment fragment = new SavingAccountDetailFragment();
        Bundle args = new Bundle();
        args.putString(ACCOUNT_ID, accountId); 
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountId = getArguments().getString(ACCOUNT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavingAccountDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFragment();
    }

    @Override
    public void initView() {
        if (currentAccount == null) return;

        renderAccount(currentAccount);
    }

    @Override
    public void initViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        // load account theo id
        accountViewModel.getAccountById(accountId);

        // mock
        if ("demo".equals(accountId)) {
            Account mock = MockAccountFactory.createMockSavingAccount();
            renderAccount(mock); // hoặc gọi initView() nếu bạn bind ở initView
            return;
        }

        // quan sat nếu có dữ liệu
        accountViewModel.getAccountState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isLoading()) {
                toggleLoading(true);
            }
            toggleLoading(false);

            if (result.getData() != null && currentAccount == null) {
                if (result.getData().getType() == AccountType.SAVING){
                    currentAccount = result.getData();
                    renderAccount(currentAccount);
                } else {
                    showError(requireContext(), "Lỗi", "Không phải tài khoản tiết kiệm");
                }
            }

            if (result.getError() != null) {
                showError(requireContext(), "Lỗi", result.getError());
            }
        });
    }

    @Override
    public void setUpEvents() {
    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }


    @Override
    public String getAppBarTitle() {
        return getString(R.string.chi_tiet_tai_khoan);
    }

    // render account
    private void renderAccount(Account account){
        if (account != null){
            binding.txtSavingDetailAccountNumber.setText(account.getAccountNumber());
            binding.txtSavingDetailStatus.setText(account.getStatus() == AccountStatus.OPEN ? "Đang hoạt động" : "Đã đóng");
            binding.txtSavingDetailBalance.setText(CurrencyUtil.formatVND(account.getBalance()));


            if (account.getSaving() != null){
                SavingsAccount savingsAccount = account.getSaving();
                binding.txtSavingDetailStartDate.setText(DateTimeUtil.formatDateToVNTime(savingsAccount.getStartDate()));
                binding.txtSavingDetailMaturityDate.setText(DateTimeUtil.formatDateToVNTime(savingsAccount.getMaturityDate()));

                binding.txtSavingDetailPolicyName.setText(savingsAccount.getPolicyName());
                binding.txtSavingDetailInterestRate.setText(savingsAccount.getAprAtOpen() + "%");
                binding.txtSavingDetailInterestPaymentMethod.setText(savingsAccount.getInterestPaymentMethod().toString());
                binding.txtSavingDetailTermLength.setText(savingsAccount.getCapitalization().toString());

                double rate = savingsAccount.getAprAtOpen();
                Long principal = account.getBalance();
                long profitPerMonth = SavingUtil.calProfitPerMonth(rate, principal);

                int termMonths = SavingUtil.calculateTermMonths(savingsAccount.getStartDate(), savingsAccount.getMaturityDate());
                binding.txtSavingDetailTermLength.setText(termMonths + " tháng");

                binding.txtSavingDetailProfitMonthInterest.setText(CurrencyUtil.formatVND(profitPerMonth));
                binding.txtSavingDetailTotalMaturity.setText(CurrencyUtil.formatVND(profitPerMonth * termMonths));
            }
        }
    }
}