package com.example.tad_bank_t1.ui.fragment.customer.transfer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.databinding.FragmentSearchTransferInfomationBinding;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.viewadapter.BankAdapter;
import com.example.tad_bank_t1.ui.viewadapter.ProviderAdapter;
import com.example.tad_bank_t1.ui.viewmodel.BankViewModel;
import com.example.tad_bank_t1.ui.viewmodel.ProviderViewModel;
import com.example.tad_bank_t1.util.TadConstants;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Collections;
import java.util.List;

public class SearchTransferInfomationFragment extends Fragment implements BaseCustomFragment {
    private static final String TITLE = "title_search";
    private static final String HINT_SEARCH = "hint_search";
    private static final String LIST_RESULT_TITLE = "list_result_title";
    private static final String SEARCH_FOR = "search_for";

    // view binding
    private FragmentSearchTransferInfomationBinding binding;


    private String title_search, hint_search, list_result_title, search_for;



    // view model

    // -- bank ----
    private BankViewModel bankViewModel;
    private RecyclerView.Adapter adapter;

    // -- bill ----
    private ProviderViewModel providerViewModel;
    private RecyclerView.Adapter adapterBill;


    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchJob;
    private String lastQuery = "";


    public SearchTransferInfomationFragment() {
        // Required empty public constructor
    }

    public static SearchTransferInfomationFragment newInstance(String title_search, String hint_search,
                                                               String list_result_title, String search_for) {
        SearchTransferInfomationFragment fragment = new SearchTransferInfomationFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title_search);
        args.putString(HINT_SEARCH, hint_search);
        args.putString(LIST_RESULT_TITLE, list_result_title);
        args.putString(SEARCH_FOR, search_for);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title_search = getArguments().getString(TITLE);
            hint_search = getArguments().getString(HINT_SEARCH);
            list_result_title = getArguments().getString(LIST_RESULT_TITLE);
            search_for = getArguments().getString(SEARCH_FOR);
        }
        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.BOTTOM));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.BOTTOM));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchTransferInfomationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        // Reset search input
//        edtEnterInfoSearch.setText("");
//        lastQuery = "";
//
//        // Reset adapter list
//        if (adapter instanceof BankAdapter) {
//            ((BankAdapter) adapter).setData(Collections.emptyList());
//        }
//
//        // Reset ViewModel state (optional)
//        if (bankViewModel != null) {
//            bankViewModel.clearBankSelected();
//        }

        initFragment();
    }

    // provide reset
    private boolean isProviderSearch(String searchFor) {
        return TadConstants.SEARCH_ELECTRICITY_PROVIDER.equalsIgnoreCase(searchFor)
                || TadConstants.SEARCH_WATER_PROVIDER.equalsIgnoreCase(searchFor)
                || TadConstants.SEARCH_TUITION_PROVIDER.equalsIgnoreCase(searchFor);
    }

    private String mapProviderType(String searchFor) {
        if (TadConstants.SEARCH_ELECTRICITY_PROVIDER.equalsIgnoreCase(searchFor)) return TadConstants.BILL_ELECTRICITY;
        if (TadConstants.SEARCH_WATER_PROVIDER.equalsIgnoreCase(searchFor)) return TadConstants.BILL_WATER;
//        if (TadConstants.SEARCH_TUITION_PROVIDER.equalsIgnoreCase(searchFor)) return TadConstants.TU;
        return "other";
    }

    private void resetProviderModal() {
        handler.removeCallbacksAndMessages(null);
        searchJob = null;
        lastQuery = "";

        if (adapter instanceof ProviderAdapter) {
            ((ProviderAdapter) adapter).setData(Collections.emptyList());
        }

        binding.edtEnterInfoSearch.setText("");
        binding.edtEnterInfoSearch.clearFocus();

        if (providerViewModel != null) {
            providerViewModel.searchCacheProviders(""); // show all cache
        }

        binding.rcvSearchResult.scrollToPosition(0);
    }


    // bank reset
    private void resetBankModal() {
        // huỷ debounce job cũ
        handler.removeCallbacksAndMessages(null);
        searchJob = null;

        // clear input + query state
        lastQuery = "";

        // clear adapter ngay lập tức để không thấy kết quả cũ
        if (adapter instanceof BankAdapter) {
            ((BankAdapter) adapter).setData(Collections.emptyList());
        }

        // clear text (có thể trigger watcher, nhưng mình sẽ load all ngay)

        binding.edtEnterInfoSearch.setText("");
        binding.edtEnterInfoSearch.clearFocus(); // optional

        // load full list
        if (bankViewModel != null) {
            bankViewModel.searchCacheBanks(""); // "" => trả về tất cả bank
        }

        binding.rcvSearchResult.scrollToPosition(0);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (TadConstants.SEARCH_BANK.equals(search_for)) {
            resetBankModal();
        } else if (isProviderSearch(search_for)) {
            resetProviderModal();
        }
    }

    @Override
    public void initView() {
        binding.txtSearchTransferInfomationTitle.setText(title_search);
        binding.edtEnterInfoSearch.setHint(hint_search);
        binding.txtSearchResultListTitle.setText(list_result_title);

    }

    @Override
    public void initViewModel() {

        if (search_for.equals(TadConstants.SEARCH_BANK)) {

            bankViewModel = new ViewModelProvider(requireActivity()).get(BankViewModel.class);

            adapter = new BankAdapter(Collections.emptyList());
            binding.rcvSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rcvSearchResult.setAdapter(adapter);

            bankViewModel.getBanks().observe(getViewLifecycleOwner(), banks -> {
                List<Bank> safe = (banks != null) ? banks : Collections.emptyList();
                ((BankAdapter) adapter).setData(safe);
            });

            ((BankAdapter) adapter).setOnItemClickListener(bank -> {
                if (bank != null) {
                    bankViewModel.setSelectedBank(bank);
                    FragmentUtil.destroyFragment(this, getParentFragmentManager());
                }
            });

            // ✅ reset + show all ngay khi mở
            resetBankModal();
        } else if (search_for.equalsIgnoreCase(TadConstants.SEARCH_ELECTRICITY_PROVIDER)
                || search_for.equalsIgnoreCase(TadConstants.SEARCH_WATER_PROVIDER)
                || search_for.equalsIgnoreCase(TadConstants.SEARCH_TUITION_PROVIDER)) {
            providerViewModel = new ViewModelProvider(requireActivity()).get(ProviderViewModel.class);

            adapter = new ProviderAdapter(Collections.emptyList());
            binding.rcvSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rcvSearchResult.setAdapter(adapter);

            String type = mapProviderType(search_for);   // bạn tự map type gọi API
            providerViewModel.loadProviders(type);

            providerViewModel.getProvidersState().observe(getViewLifecycleOwner(), rs -> {
                if (rs == null) {
                    ((ProviderAdapter) adapter).setData(Collections.emptyList());
                    return;
                }

                if (rs.isLoading()){
                    ((ProviderAdapter) adapter).setData(Collections.emptyList());
                    return;
                }

                if (rs.getError() != null) {
                    ((ProviderAdapter) adapter).setData(Collections.emptyList());
                    Toast.makeText(getContext(), rs.getError(), Toast.LENGTH_SHORT).show();
                    showError(getContext(), "Lỗi", rs.getError());
                    return;
                }

                if (rs.getData() != null) {
                    ((ProviderAdapter) adapter).setData(rs.getData());
                    Toast.makeText(getContext(), "Success: " + rs.getData().size(), Toast.LENGTH_SHORT).show();
                    return;
                }
            });

            ((ProviderAdapter) adapter).setOnItemClickListener(provider -> {
                providerViewModel.setSelectedProvider(provider);
                FragmentUtil.destroyFragment(this, getParentFragmentManager());
            });

            resetProviderModal();
        }

    }

    @Override
    public void setUpEvents() {
        binding.imbtSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtil.destroyFragment(SearchTransferInfomationFragment.this, getParentFragmentManager());
            }
        });

        binding.imbtSearchTransfer.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Search for ...", Toast.LENGTH_LONG).show();
        });


        binding.edtEnterInfoSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString().trim();

                if(search_for.equals(TadConstants.SEARCH_BANK)){
                    if (key.equals(lastQuery)) return;         // tránh gọi lại cùng chuỗi
                    lastQuery = key;

                    handler.removeCallbacks(searchJob);        // huỷ job cũ
                    searchJob = () -> {
                        bankViewModel.searchCacheBanks(key);
                    };
                    handler.postDelayed(searchJob, 350);
                } else if (search_for.equalsIgnoreCase(TadConstants.SEARCH_ACCOUNT)) {
                    Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
                } else if(search_for.equalsIgnoreCase(TadConstants.SEARCH_BENEFICIARY_ACCOUNT)){
                    Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
                } else if(search_for.equalsIgnoreCase(TadConstants.SEARCH_BENEFICIARY_PHONE)){
                    Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
                } else if (isProviderSearch(search_for)) {
                    if (key.equals(lastQuery)) return;
                    lastQuery = key;

                    handler.removeCallbacks(searchJob);
                    searchJob = () -> providerViewModel.searchCacheProviders(key);
                    handler.postDelayed(searchJob, 350);
                } else if(search_for.equalsIgnoreCase(TadConstants.SEARCH_WATER_PROVIDER)){
                    Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
                } else if(search_for.equalsIgnoreCase(TadConstants.SEARCH_TUITION_PROVIDER)){
                    Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }
}