package com.example.tad_bank_t1.ui.fragment.customer.bills;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.google.android.material.card.MaterialCardView;

public class BillsPaymentFragment extends Fragment implements UiConfig {
    private MaterialCardView cvBillPaymentElectricity, cvBillPaymentWater, cvBillPaymentTuition, cvBillPaymentAirTicket, cvBillPaymentTrainTicket, cvBillPaymentMovieTicket;


    @Override
    public boolean showBottomNav() {
        return false;
    }

    @Override
    public boolean showAppBar() {
        return true;
    }

    @Override
    public String getAppBarTitle() {
        return getString(R.string.thanh_toan_hoa_don);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bills_payment, container, false);




        cvBillPaymentElectricity = view.findViewById(R.id.cvBillPaymentElectricity);
        cvBillPaymentWater = view.findViewById(R.id.cvBillPaymentWater);
        cvBillPaymentTuition = view.findViewById(R.id.cvBillPaymentTuition);
        cvBillPaymentAirTicket = view.findViewById(R.id.cvBillPaymentAirTicket);
        cvBillPaymentTrainTicket = view.findViewById(R.id.cvBillPaymentTrainTicket);
        cvBillPaymentMovieTicket = view.findViewById(R.id.cvBillPaymentMovieTicket);

        cvBillPaymentElectricity.setOnClickListener(v -> {
            onClickBillPayment(new ElectricBillPaymentFragment(), getString(R.string.thanh_toan_tien_dien));
        });
        cvBillPaymentWater.setOnClickListener(v -> {
//            onClickBillPayment(new WaterBillFragment());
        });
        cvBillPaymentTuition.setOnClickListener(v -> {
//            onClickBillPayment(new TuitionBillFragment());
        });
        cvBillPaymentAirTicket.setOnClickListener(v -> {
//            onClickBillPayment(new AirTicketBill());
                            });
        cvBillPaymentTrainTicket.setOnClickListener(v -> {
//            onClickBillPayment(new TrainTicketBillFragment());
        });
        cvBillPaymentMovieTicket.setOnClickListener(v -> {
//            onClickBillPayment(new MovieTicketBillFragment());
        });


//        FragmentUtil.replaceFragment(new CardPaymentFragment(),
//                getParentFragmentManager(),
//                R.id.fragment_card_electricity_bill,
//                false);

        return view;
    }

    public void onClickBillPayment(Fragment fr, String title){
        ((MainActivity) requireActivity()).openFeatureFragment(fr, title);
    }
}