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


public class NotiItemFragment extends Fragment {
    private RecyclerView rvNotiItem;
    private NotiAdapter notiAdapter;
    private TextInputEditText edtNotiItem;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_noti_item, container, false);

        rvNotiItem = view.findViewById(R.id.rvNotiItem);
        edtNotiItem = view.findViewById(R.id.edtSearchNotiSystem);


        // adapter
        rvNotiItem.setLayoutManager(new LinearLayoutManager(getContext()));
        notiAdapter = new NotiAdapter(NotiFakeData.getNotiData());
        rvNotiItem.setAdapter(notiAdapter);

        // search
        edtNotiItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString();
                List<Notification> dataSearch = NotiFakeData.search(key);
                notiAdapter.setNotiData(dataSearch);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        return view;
    }
}