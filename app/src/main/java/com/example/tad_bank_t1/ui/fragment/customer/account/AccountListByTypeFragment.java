package com.example.tad_bank_t1.ui.fragment.customer.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.databinding.FragmentAccountListByTypeBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.savings.SavingAccountCreateFragment;
import com.example.tad_bank_t1.ui.fragment.customer.savings.SavingAccountDetailFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionHistoryFragment;
import com.example.tad_bank_t1.ui.viewadapter.AccountListAdapter;
import com.example.tad_bank_t1.ui.viewadapter.MortgageAccountListAdapter;
import com.example.tad_bank_t1.ui.fragment.customer.mortgage.MortgageAccountDetailFragment;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;

import java.util.List;
import java.util.Objects;

public class AccountListByTypeFragment extends Fragment implements UiConfig, BaseCustomFragment {
    private static final String USER_ID = "userId";
    private static final String ACCOUNT_TYPE = "type";

    private String userId;
    private String accountType;

    // binding
    private FragmentAccountListByTypeBinding binding;

    // view model
    private AccountViewModel accountViewModel;
    private AccountListAdapter accountListAdapter;
    // Adapter riêng cho tài khoản thế chấp
    private MortgageAccountListAdapter mortgageAccountListAdapter;
    private TransactionViewModel transactionViewModel;
    private List<Account> accounts;

    public AccountListByTypeFragment() {
        // Required empty public constructor
    }

    public static AccountListByTypeFragment newInstance(String userId, AccountType type) {
        AccountListByTypeFragment fragment = new AccountListByTypeFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        args.putString(ACCOUNT_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
            accountType = getArguments().getString(ACCOUNT_TYPE);
        }

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountListByTypeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFragment();
    }

    // set up adapter khi co data
    public void setUpAdapter(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            binding.txtAccListByTypeEmpty.setVisibility(View.VISIBLE);
            binding.rvAccListByType.setVisibility(View.GONE);
            binding.txtAccListByTypeEmpty.setText("Không có tài khoản nào");
            return;
        }

        // xoas no data
        binding.txtAccListByTypeEmpty.setVisibility(View.GONE);
        binding.rvAccListByType.setVisibility(View.VISIBLE);

        // bind data theo loại tài khoản
        binding.rvAccListByType.setLayoutManager(new LinearLayoutManager(getContext()));
        if (Objects.equals(accountType, AccountType.MORTGAGE.name())) {
            // Nếu là danh sách mortgage thì dùng adapter mortgage
            if (mortgageAccountListAdapter != null) {
                mortgageAccountListAdapter.setData(accounts);
                binding.rvAccListByType.setAdapter(mortgageAccountListAdapter);
            }
        } else {
            // Các loại khác dùng adapter chung
            if (accountListAdapter != null) {
                accountListAdapter.setData(accounts);
                binding.rvAccListByType.setAdapter(accountListAdapter);
            }
        }

        // bind card tổng số dư và số tài khoản open (đối với mortgage, số dư là 0 nên có thể tuỳ chỉnh)
        binding.tvAccListByTypeTotalBalance.setText(CurrencyUtil.formatVND(getTotalBalance(accounts)));
        String showAccOpen = getString(R.string.co_count_tai_khoan_open, countAccountIsOpen(accounts));
        binding.txtAccListByTypeCountAccount.setText(showAccOpen);
    }

    @Override
    public void initView() {
        // Khởi tạo adapter theo loại tài khoản. Nếu là MORTGAGE thì dùng adapter riêng,
        // ngược lại vẫn dùng adapter chung cho checking/saving.
        if (Objects.equals(accountType, AccountType.MORTGAGE.name())) {
            // Adapter hiển thị khoản vay thế chấp
            mortgageAccountListAdapter = new MortgageAccountListAdapter(java.util.List.of());
            mortgageAccountListAdapter.setOnClickMortgageListener(account -> {
                // Mở màn hình chi tiết tài khoản thế chấp
                ((MainActivity) requireActivity())
                        .openFeatureFragment(
                                MortgageAccountDetailFragment.newInstance(account.getAccountId()),
                                // Dùng tiêu đề chung cho màn chi tiết tài khoản
                                getString(R.string.chi_tiet_tai_khoan)
                        );
            });
        } else {
            // Adapter cho checking và saving
            accountListAdapter = new AccountListAdapter(java.util.List.of());
            accountListAdapter.setOnclickAccountListener(new AccountListAdapter.OnclickAccountListener() {
                @Override
                public void onClickOpenAccountTxnHistory(Account account) {
                    // Xử lý theo loại tài khoản
                    if (Objects.equals(accountType, AccountType.CHECKING.name())) {
                        transactionViewModel.setSelectedAccountId(account.getAccountId());
                        ((MainActivity) requireActivity())
                                .openFeatureFragment(new TransactionHistoryFragment(), getString(R.string.lich_su_giao_dich));
                    }
                    if (Objects.equals(accountType, AccountType.SAVING.name())) {
                        ((MainActivity) requireActivity())
                                .openFeatureFragment(SavingAccountDetailFragment.newInstance(account.getAccountId()), getString(R.string.tai_khoan_tiet_kiem));
                    }
                }
            });
        }
    }

    @Override
    public void initViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // load data
        accountViewModel.getAccountsByUserIdAndType(userId, accountType);

        accountViewModel.getListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                toggleLoading(true);
                return;
            }

            toggleLoading(false);

            if (state.getError() != null) {
                showError(requireContext(), "Lỗi", state.getError());
                return;
            }

            if (state.getData() != null) {
                accounts = state.getData();
                setUpAdapter(accounts);
            }
        });
    }

    @Override
    public void setUpEvents() {
        binding.btnCreateNewAccountByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(accountType, AccountType.MORTGAGE.name())){

                } else if (Objects.equals(accountType, AccountType.SAVING.name())){
                    ((MainActivity) requireActivity())
                            .openFeatureFragment(SavingAccountCreateFragment.newInstance(userId, accountType), getString(R.string.tao_moi_tai_khoan));
                }
            }
        });
    }


    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity)requireActivity()).showLoadingFeature(isLoading);
    }


    @Override
    public String getAppBarTitle() {
        return getString(R.string.danh_sach_tai_khoan);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop(){
        super.onStop();
        accountViewModel.resetState();
    }

    // tính tông tiền từ acccounts
    public long getTotalBalance(List<Account> accounts){
        return accounts.stream().mapToLong(Account::getBalance).sum();
    }

    // dem so account dang OPEN
    public int countAccountIsOpen(List<Account> accounts){
        return (int) accounts.stream().filter(account -> account.getStatus() == AccountStatus.OPEN).count();
    }
}