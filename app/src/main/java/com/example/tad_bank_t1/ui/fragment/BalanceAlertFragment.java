package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.fake_data.NotiFakeData;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.ui.viewadapter.NotiAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class BalanceAlertFragment extends Fragment {
    private RecyclerView rvNotiBalanceAlert;
    private NotiAdapter notiAdapter;
    private TextInputEditText edtSearchBalanceAlert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance_alert, container, false);

        rvNotiBalanceAlert = view.findViewById(R.id.rvNotiBalanceAlert);
        edtSearchBalanceAlert = view.findViewById(R.id.edtSearchBalanceAlert);

        // adapter
        rvNotiBalanceAlert.setLayoutManager(new LinearLayoutManager(getContext()));
        notiAdapter = new NotiAdapter(NotiFakeData.getNotiData());
        rvNotiBalanceAlert.setAdapter(notiAdapter);

        // search
        edtSearchBalanceAlert.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = s.toString().trim();
                List<Notification> dataSearch = NotiFakeData.search(key);
                notiAdapter.setNotiData(dataSearch); // cập nhật adapter
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        return view;
    }
}