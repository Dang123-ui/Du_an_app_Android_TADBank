package com.example.tad_bank_t1.ui.fragment.ai;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.ai.ApiClient;
import com.example.tad_bank_t1.data.model.ai.PredictAutoRequest;
import com.example.tad_bank_t1.data.model.ai.PredictAutoResponse;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewadapter.ai.HeadlineAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AIPredictFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AIPredictFragment extends Fragment implements UiConfig {
    private TextInputEditText etAsset;
    private TextView tvStatus, tvResult, tvStats;
    private Button btnPredict;

    private HeadlineAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AIPredictFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AIPredictFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AIPredictFragment newInstance(String param1, String param2) {
        AIPredictFragment fragment = new AIPredictFragment();
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
        return inflater.inflate(R.layout.fragment_a_i_predict, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAsset = view.findViewById(R.id.etAsset);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvResult = view.findViewById(R.id.tvResult);
        tvStats = view.findViewById(R.id.tvStats);
        btnPredict = view.findViewById(R.id.btnPredict);
        RecyclerView rvHeadlines = view.findViewById(R.id.rvHeadlines);
        rvHeadlines.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HeadlineAdapter();
        rvHeadlines.setAdapter(adapter);
        // default cho tiện test
        etAsset.setText("VIC");
        btnPredict.setOnClickListener(v -> {
            runPredict();
        });
    }
    private void runPredict(){
        String asset = etAsset.getText().toString().trim().toUpperCase(Locale.US);
        if (asset.isEmpty()) {
            tvStatus.setText("Trạng thái: bạn chưa nhập asset");
            return;
        }
        tvStatus.setText("Trạng thái: đang gọi API...");
        tvResult.setText("...");
        tvStats.setText("...");
        adapter.setItems(null);

        ApiClient.api().predictAuto(new PredictAutoRequest(asset))
                .enqueue(new Callback<PredictAutoResponse>() {
                    @Override
                    public void onResponse(Call<PredictAutoResponse> call, Response<PredictAutoResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            tvStatus.setText("Trạng thái: lỗi API code=" + response.code());
                            return;
                        }

                        PredictAutoResponse r = response.body();
                        tvStatus.setText("Trạng thái: OK");

                        String label = (r.pred == 1) ? "Dự đoán: TĂNG" : "Dự đoán: GIẢM";
                        String prob = String.format(Locale.US, "%.1f%%", r.prob_up * 100.0);
                        tvResult.setText(label + " | Xác suất tăng: " + prob);

                        String stats = String.format(
                                Locale.US,
                                "Asset=%s | Price=%.2f | News=%d | Sent=%.3f | Impact=%.3f",
                                r.asset, r.price, r.news_count, r.avg_sentiment, r.avg_impact
                        );
                        tvStats.setText(stats);

                        adapter.setItems(r.top_headlines);
                    }

                    @Override
                    public void onFailure(Call<PredictAutoResponse> call, Throwable t) {
                        tvStatus.setText("Trạng thái: network fail: " + t.getMessage());
                    }
                });
    }
}