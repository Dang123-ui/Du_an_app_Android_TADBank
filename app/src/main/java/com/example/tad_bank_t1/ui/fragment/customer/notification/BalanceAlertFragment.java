package com.example.tad_bank_t1.ui.fragment.customer.notification;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
//import com.example.tad_bank_t1.data.fake_data.NotiFakeData;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.enums.NotificationType;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewadapter.NotiAdapter;
import com.example.tad_bank_t1.ui.viewmodel.NotificationViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BalanceAlertFragment extends Fragment implements UiConfig {
    private RecyclerView rvNotiBalanceAlert;
    private NotiAdapter notiAdapter;
    private TextInputEditText edtSearchBalanceAlert;
    private NotificationViewModel notificationViewModel;
    private SessionViewModel sessionViewModel;

    private List<Notification> fullList = new ArrayList<>();




    @Override
    public String getAppBarTitle() {
        return getString(R.string.thong_bao);
    }

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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotiBalanceAlert = view.findViewById(R.id.rvNotiBalanceAlert);
        edtSearchBalanceAlert = view.findViewById(R.id.edtSearchBalanceAlert);



        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        notificationViewModel.startListeningTransaction(sessionViewModel.getUserId().getValue());


        notificationViewModel.transactionNotifications.observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null){
                Log.d("BalanceAlertFragment", "notifications: " + notifications.size() + "");
                // adapter
                fullList.clear();
                fullList.addAll(notifications);

                notiAdapter = new NotiAdapter();
                notiAdapter.setNotiData(fullList); // full data

                rvNotiBalanceAlert.setAdapter(notiAdapter);
                rvNotiBalanceAlert.setLayoutManager(new LinearLayoutManager(getContext()));

                // ⭐ CHỈ MARK KHI ĐÃ CÓ DỮ LIỆU
                notificationViewModel.markAllRead(NotificationType.TRANSACTION);            }
        });

        // search
        edtSearchBalanceAlert.addTextChangedListener(new TextWatcher() {
            private long lastEditTime = 0;
            private final long DELAY = 300;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastEditTime = System.currentTimeMillis();

                edtSearchBalanceAlert.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long now = System.currentTimeMillis();
                        if (now - lastEditTime < DELAY) return;

                        String key = s.toString().trim().toLowerCase();

                        if (key.isEmpty()){
                            notiAdapter.setNotiData(fullList);
                            return;
                        }

                        List<Notification> rs = new ArrayList<>();

                        for (Notification n : fullList){
                            String tilte = n.getTitle().toLowerCase();
                            String content = n.getMessage().toLowerCase();

                            if (tilte.contains(key) || content.contains(key)){
                                rs.add(n);
                            }
                        }

                        notiAdapter.setNotiData(rs);

                    }
                }, DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}