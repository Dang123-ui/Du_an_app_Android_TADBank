package com.example.tad_bank_t1.ui.fragment.officer.saving;

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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.OfficerMainActivity;
import com.example.tad_bank_t1.ui.fragment.officer.CustomUserProfileFragment;
import com.example.tad_bank_t1.ui.viewadapter.officer.SavingContractAdapter;
import com.example.tad_bank_t1.ui.viewmodel.officer.saving.SavingContractListViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavingPolicyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavingPolicyListFragment extends Fragment {

    private SavingContractListViewModel vm;

    private ImageButton btnAddPolicy;
    private EditText searchEditText;
    private ChipGroup chipFilters;
    private Chip chipActive, chipInactive, chipAll;

    private RecyclerView rv;
    private SavingContractAdapter adapter;

    private View emptyLottie;
    private View emptyText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SavingPolicyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavingPolicyListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavingPolicyListFragment newInstance(String param1, String param2) {
        SavingPolicyListFragment fragment = new SavingPolicyListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saving_policy_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(SavingContractListViewModel.class);

        bindViews(view);
        setupRecycler();
        setupFilters();
        setupSearch();
        setupActions();
        observe();
        vm.setChip(SavingContractListViewModel.FilterChip.ALL);
        vm.setQuery("");
        vm.start();
    }

    private void bindViews(View v) {
        btnAddPolicy = v.findViewById(R.id.btnAddPolicy);
        searchEditText = v.findViewById(R.id.search_edit_text);

        chipFilters = v.findViewById(R.id.chipFilters);
        chipActive = v.findViewById(R.id.chipActive);
        chipInactive = v.findViewById(R.id.chipInactive);
        chipAll = v.findViewById(R.id.chipAll);

        rv = v.findViewById(R.id.rvSavingPolicies);

        emptyLottie = v.findViewById(R.id.emptyLottie);
        emptyText = v.findViewById(R.id.emptyText);
    }
    private void setupRecycler() {
        adapter = new SavingContractAdapter(item -> {
            if (item == null) {
                Toast.makeText(requireContext(), "Item null", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ưu tiên field savingPolicyId, fallback docId
            String id = item.getSavingPolicyId();
            if (id == null || id.trim().isEmpty()) {
                id = item.getDocId();
            }

            if (id == null || id.trim().isEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy policyId", Toast.LENGTH_SHORT).show();
                return;
            }

            CreatSavingContractFragment f = CreatSavingContractFragment.newInstance(id);
            if (getActivity() instanceof OfficerMainActivity) {
                ((OfficerMainActivity) getActivity()).navigateTo(f, true);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
    }
    private void setupFilters() {
        chipFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int id = (checkedIds == null || checkedIds.isEmpty()) ? R.id.chipAll : checkedIds.get(0);
            if (id == R.id.chipActive) vm.setChip(SavingContractListViewModel.FilterChip.ACTIVE);
            else if (id == R.id.chipInactive) vm.setChip(SavingContractListViewModel.FilterChip.INACTIVE);
            else vm.setChip(SavingContractListViewModel.FilterChip.ALL);
        });
    }
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                vm.setQuery(s == null ? "" : s.toString());
            }
        });
    }
    private void setupActions() {
        btnAddPolicy.setOnClickListener(v -> {
            CreatSavingContractFragment fragment =
                    CreatSavingContractFragment.newInstance(null);

            if (getActivity() instanceof OfficerMainActivity) {
                ((OfficerMainActivity) getActivity())
                        .navigateTo(fragment, true);
            }
        });
    }

    private void observe() {
        vm.getFilteredList().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list == null ? new ArrayList<>() : list);
            renderList(list);
        });

        vm.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            if (state.toastMessage != null && !state.toastMessage.trim().isEmpty()) {
                Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_SHORT).show();
                vm.clearToast();
            }
        });
    }
    private void renderList(List<?> list) {
        boolean empty = (list == null || list.isEmpty());
        emptyLottie.setVisibility(empty ? View.VISIBLE : View.GONE);
        emptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        vm.stop();
    }
}