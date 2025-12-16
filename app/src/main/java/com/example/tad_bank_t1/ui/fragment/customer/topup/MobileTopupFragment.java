package com.example.tad_bank_t1.ui.fragment.customer.topup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.FragmentMobileTopupTransferBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.CardPaymentFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.SearchTransferInfomationFragment;
import com.example.tad_bank_t1.ui.viewmodel.ProviderViewModel;
import com.example.tad_bank_t1.util.Constants;
import com.example.tad_bank_t1.util.FragmentUtil;

public class MobileTopupFragment extends Fragment implements UiConfig {
    // binding
    private FragmentMobileTopupTransferBinding binding;

    // view model
    private ProviderViewModel providerViewModel;

    @Override
    public String getAppBarTitle() {
        return getString(R.string.nap_tien_dien_thoai);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMobileTopupTransferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_topup,
                false);


        initVM();

        setUpEvents();
    }

    private void initVM(){
        providerViewModel = new ViewModelProvider(requireActivity()).get(ProviderViewModel.class);

    }

    private void setUpEvents(){
        binding.btnContinueTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = binding.edtReceiverPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty()){
                    binding.edtReceiverPhoneNumber.setError("Vui lòng nhập số điện thoại");
                    return;
                }

                providerViewModel.checkTopup(phoneNumber).observe(getViewLifecycleOwner(), result -> {
                    if (result == null) return;

                    if (result.isLoading()) {
                        showLoading(true);
                        return;
                    }

                    showLoading(false);

                    if (result.getError() != null) {
                        showError(result.getError());
                        return;
                    }

                    if (result.getData() != null && result.getData()) {
                        // Valid phone
                        Toast.makeText(requireContext(), "Số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.btnContinueTopup.setEnabled(!isLoading);
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    public void showError(String error) {
        binding.btnContinueTopup.setEnabled(true);
        ((MainActivity) requireActivity()).showLoadingFeature(false);
        binding.edtReceiverPhoneNumber.setError(error);
    }
}