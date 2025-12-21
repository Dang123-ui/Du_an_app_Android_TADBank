package com.example.tad_bank_t1.ui.fragment.officer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.OfficerMainActivity;
import com.example.tad_bank_t1.ui.viewadapter.officer.AccListOfficerAdapter;
import com.example.tad_bank_t1.ui.viewmodel.officer.AccountListViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Collections;

public class AccountListOfficerFragment extends Fragment implements AccListOfficerAdapter.Listener {

    private RecyclerView rvAccounts;
    private EditText searchEditText;

    private ChipGroup chipFilters;
    private Chip chipToday, chip7d, chip30d, chipAll;

    private AccListOfficerAdapter adapter;
    private AccountListViewModel viewModel;

    private LottieAnimationView emptyLottie;
    private TextView emptyText;

    private String currentQuery = "";
    private AccountListViewModel.FilterKey currentFilter = AccountListViewModel.FilterKey.ALL;

    public AccountListOfficerFragment() {}

    public static AccountListOfficerFragment newInstance(String param1, String param2) {
        AccountListOfficerFragment fragment = new AccountListOfficerFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_list2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupRecycler();
        setupViewModel();
        if (chipAll != null) chipAll.setChecked(true);
        currentFilter = AccountListViewModel.FilterKey.ALL;

        setupChips();
        setupSearchRealtime();

        setEmptyState(true, false); // chờ data
        viewModel.loadAccount();
        runSearch();
    }

    private void bindViews(View v) {
        rvAccounts = v.findViewById(R.id.rvAccounts);
        searchEditText = v.findViewById(R.id.search_edit_text);

        chipFilters = v.findViewById(R.id.chipFilters1);
        chipToday = v.findViewById(R.id.chipToday1);
        chip7d = v.findViewById(R.id.chip7d_);
        chip30d = v.findViewById(R.id.chip30d_);
        chipAll = v.findViewById(R.id.chipAllChannels1);

        emptyLottie = v.findViewById(R.id.emptyLottie);
        emptyText = v.findViewById(R.id.emptyText);
    }

    private void setupRecycler() {
        adapter = new AccListOfficerAdapter(this);
        rvAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAccounts.setAdapter(adapter);
        rvAccounts.setHasFixedSize(true);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AccountListViewModel.class);

        viewModel.getAccounts().observe(getViewLifecycleOwner(), list -> {
            if (list == null) list = Collections.emptyList();
            adapter.submitList(list);

            boolean hasTyped = currentQuery != null && !currentQuery.trim().isEmpty();
            if (list.isEmpty()) {
                setEmptyState(true, hasTyped);
            } else {
                setEmptyState(false, false);
            }
        });
    }

    private void setupChips() {
        chipFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds == null || checkedIds.isEmpty()) return;

            int id = checkedIds.get(0);
            if (id == R.id.chipToday1) {
                currentFilter = AccountListViewModel.FilterKey.TODAY;
            } else if (id == R.id.chip7d_) {
                currentFilter = AccountListViewModel.FilterKey.D7;
            } else if (id == R.id.chip30d_) {
                currentFilter = AccountListViewModel.FilterKey.D30;
            } else if (id == R.id.chipAllChannels1) {
                currentFilter = AccountListViewModel.FilterKey.ALL;
            } else {
                currentFilter = AccountListViewModel.FilterKey.ALL;
            }

            runSearch(); // ✅ chọn chip là search + hiện kết quả ngay
        });
    }

    private void setupSearchRealtime() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = (s == null) ? "" : s.toString().trim();
                runSearch();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void runSearch() {
        if (viewModel == null) return;

        // 1) apply filter
        viewModel.applyFilter(currentFilter);

        // 2) apply search (kể cả query rỗng)
        viewModel.applySearch(currentQuery);

        // UI empty state sẽ do observer quyết định sau khi list trả về
    }

    @Override
    public void onCustomerProfile(String userId) {
        CustomUserProfileFragment customUserProfileFragment = CustomUserProfileFragment.newInstance(userId);
        if (getActivity() instanceof OfficerMainActivity) {
            ((OfficerMainActivity) getActivity()).navigateTo(customUserProfileFragment, true);
        }
    }

    @Override public void onSavings(String userId) { toast("Savings: " + userId); }
    @Override public void onChecking(String userId) {
        CreateCheckingAccountForOfficerFragment createCheckingAccountForOfficerFragment = CreateCheckingAccountForOfficerFragment.newInstance(userId);
        if (getActivity() instanceof OfficerMainActivity) {
            ((OfficerMainActivity) getActivity()).navigateTo(createCheckingAccountForOfficerFragment, true);
        }
    }
    @Override public void onMortgage(String userId) { toast("Mortgage: " + userId); }

    private void toast(String msg) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setEmptyState(boolean show, boolean noResult) {
        if (!isAdded()) return;

        if (show) {
            rvAccounts.setVisibility(View.GONE);
            emptyLottie.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(noResult ? "Không tìm thấy kết quả" : "Không có dữ liệu");
            emptyLottie.playAnimation();
        } else {
            emptyLottie.cancelAnimation();
            emptyLottie.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
            rvAccounts.setVisibility(View.VISIBLE);
        }
    }
}
