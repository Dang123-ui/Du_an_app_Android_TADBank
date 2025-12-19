package com.example.tad_bank_t1.ui.fragment.customer.account;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.databinding.FragmentAccountListByTypeBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.savings.SavingAccountCreateFragment;
import com.example.tad_bank_t1.ui.fragment.customer.savings.SavingAccountDetailFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionHistoryFragment;
import com.example.tad_bank_t1.ui.viewadapter.AccountListAdapter;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.account.AccountViewModel;

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

        binding.txtAccListByTypeEmpty.setVisibility(View.GONE);
        binding.rvAccListByType.setVisibility(View.VISIBLE);

        accountListAdapter.setData(accounts);
        binding.rvAccListByType.setAdapter(accountListAdapter);
        binding.rvAccListByType.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void initView() {
        if (Objects.equals(accountType, AccountType.MORTGAGE.name())){
            showError(requireContext(), "Lỗi", "Chức năng này chưa được hỗ trợ MORTGAE");
//                    ((MainActivity) requireActivity())
//                            .openFeatureFragment(SavingAccountDetailFragment.newInstance(account.getAccountId()), getString(R.string.tai_khoan_tiet_kiem)));
        }

        accountListAdapter = new AccountListAdapter(List.of());
        accountListAdapter.setOnclickAccountListener(new AccountListAdapter.OnclickAccountListener() {
            @Override
            public void onClickOpenAccountTxnHistory(Account account) {
//                sessionViewModel.setSelectedAccount(account);
                if (Objects.equals(accountType, AccountType.CHECKING.name())){
                    transactionViewModel.setSelectedAccountId(account.getAccountId());
                    ((MainActivity) requireActivity())
                            .openFeatureFragment(new TransactionHistoryFragment(), getString(R.string.lich_su_giao_dich));
                }

                if (Objects.equals(accountType, AccountType.SAVING.name())){
                    ((MainActivity) requireActivity())
                            .openFeatureFragment(SavingAccountDetailFragment.newInstance(account.getAccountId()), getString(R.string.tai_khoan_tiet_kiem));
                }

                if (Objects.equals(accountType, AccountType.MORTGAGE.name())){
                    showError(requireContext(), "Lỗi", "Chức năng này chưa được hỗ trợ MORTGAE");
//                    ((MainActivity) requireActivity())
//                            .openFeatureFragment(SavingAccountDetailFragment.newInstance(account.getAccountId()), getString(R.string.tai_khoan_tiet_kiem)));
                }
            }
        });
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
                            .openFeatureFragment(SavingAccountCreateFragment.newInstance(userId, accountType), getString(R.string.tao_moi_tai_khoanr));
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
}