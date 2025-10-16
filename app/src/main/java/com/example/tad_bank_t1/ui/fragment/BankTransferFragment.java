package com.example.tad_bank_t1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.dto.TransferInfoDTO;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.util.Constants;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.FragmentUtil;


public class BankTransferFragment extends Fragment {
    private ImageButton imbtShowListBank, imbtShowListBeneficiaryTransfer;
    private Button btnContinueTransfer;
    private LottieAnimationView lottie_loading_waiting_transfer;

    private TextView txtReceiverBankName, txtReceiverAccNumber, txtReceiverName,
            txtTransferAmount, txtTransferDescription, txtTransferAccNumber;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.BOTTOM));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.TOP));


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank_transfer, container, false);

        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_transfer,
                false);

        imbtShowListBank = view.findViewById(R.id.imbtShowListBank);
        imbtShowListBeneficiaryTransfer = view.findViewById(R.id.imbtShowListBeneficiaryTransfer);
        btnContinueTransfer = view.findViewById(R.id.btnContinueTransfer);
        lottie_loading_waiting_transfer = view.findViewById(R.id.lottie_loading_waiting_transfer);
        txtReceiverBankName = view.findViewById(R.id.txtReceiverBankName);
        txtReceiverAccNumber = view.findViewById(R.id.txtReceiverAccNumber);
        txtReceiverName = view.findViewById(R.id.txtReceiverName);
        txtTransferAmount = view.findViewById(R.id.txtTransferAmount);
        txtTransferDescription = view.findViewById(R.id.txtTransferDescription);



        imbtShowListBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                        "Chọn ngân hàng nhận",
                        "Nhập ngân hàng",
                        "Danh sách ngân hàng",
                        Constants.SEARCH_BANK
                );
                FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                        getParentFragmentManager(),
                        R.id.fragment_container_search);
            }
        });

        imbtShowListBeneficiaryTransfer.setOnClickListener(v -> {
            SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                    "Chọn danh bạ thụ hưởng",
                    "Nhập người thụ hưởng",
                    "Danh sách thụ hưởng",
                    Constants.SEARCH_BENEFICIARY_ACCOUNT
            );
            FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                    getParentFragmentManager(),
                    R.id.fragment_container_search);
        });

        btnContinueTransfer.setOnClickListener(v -> {
            lottie_loading_waiting_transfer.setVisibility(View.VISIBLE);
            btnContinueTransfer.setEnabled(false);
            btnContinueTransfer.setText("...");

            // Giả lập một tác vụ xử lý (ví dụ: gọi API)
            new Handler().postDelayed(() -> {
                // Khi tác vụ hoàn thành:
                // Ẩn Lottie và hiện lại button
                lottie_loading_waiting_transfer.setVisibility(View.GONE);
//                btnSubmit.setVisibility(View.VISIBLE);

                // Có thể hiển thị thông báo thành công
                Toast.makeText(getContext(), "Chuyen sang trang xac nhan!", Toast.LENGTH_LONG).show();


                TransferInfoDTO transferInfoDTO = new TransferInfoDTO(
                        "debit acount",
                        txtReceiverAccNumber.getText().toString().trim(),
                        txtReceiverName.getText().toString().trim(),
                        txtReceiverBankName.getText().toString().trim(),
                        txtTransferDescription.getText().toString().trim(),
                        CurrencyUtil.convertToDouble(txtTransferAmount.getText().toString().trim()),
                        0,
                        "OTP"
                );

                ((MainActivity) requireActivity())
                        .openFeatureFragment(ConfirmTransactionFragment.newInstance(transferInfoDTO), getString(R.string.xac_nhan_giao_dich));

                btnContinueTransfer.setText(getResources().getString(R.string.tiep_tuc));
            }, 2000); // 3 giây

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "BANK TRANSFER onstart");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "BANK TRANSFER onstop");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }
}