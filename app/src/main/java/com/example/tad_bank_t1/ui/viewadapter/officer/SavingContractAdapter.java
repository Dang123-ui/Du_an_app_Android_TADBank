package com.example.tad_bank_t1.ui.viewadapter.officer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.google.android.material.chip.Chip;
import com.example.tad_bank_t1.R;

import java.util.Locale;

public class SavingContractAdapter extends ListAdapter<SavingsRatePolicy, SavingContractAdapter.VH> {
    public interface Listener{
        void onClick(SavingsRatePolicy item);
    }
    private final Listener listener;

    public SavingContractAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }
    static final DiffUtil.ItemCallback<SavingsRatePolicy> DIFF = new DiffUtil.ItemCallback<SavingsRatePolicy>() {
        @Override
        public boolean areItemsTheSame(@NonNull SavingsRatePolicy oldItem, @NonNull SavingsRatePolicy newItem) {
            String a = oldItem.getSavingPolicyId();
            String b = newItem.getSavingPolicyId();
            if (a == null || b == null) return oldItem == newItem;
            return a.equals(b);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SavingsRatePolicy oldItem, @NonNull SavingsRatePolicy newItem) {
            // đủ dùng cho UI list
            return safe(oldItem.getContractCode()).equals(safe(newItem.getContractCode()))
                    && safe(oldItem.getPolicyName()).equals(safe(newItem.getPolicyName()))
                    && safe(oldItem.getShortDescription()).equals(safe(newItem.getShortDescription()))
                    && oldItem.getTermMonths() == newItem.getTermMonths()
                    && Double.compare(oldItem.getInterestRate(), newItem.getInterestRate()) == 0
                    && String.valueOf(oldItem.getStatus()).equals(String.valueOf(newItem.getStatus()));
        }

        private String safe(String s) { return s == null ? "" : s; }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saving_contract, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        final SavingsRatePolicy it = getItem(position);
        if (it == null) {
            h.tvCode.setText("");
            h.tvName.setText("Đang tải...");
            h.tvDesc.setText("");
            h.tvTermRate.setText("");
            h.chipStatus.setText("UNKNOWN");
            h.itemView.setOnClickListener(null);
            return;
        }


        h.tvCode.setText(nullToEmpty(it.getContractCode()));
        h.tvName.setText(nullToEmpty(it.getPolicyName()));

        String desc = nullToEmpty(it.getShortDescription());
        h.tvDesc.setText(desc.isEmpty() ? "(Không có mô tả)" : desc);

        String status = it.getStatus() == null ? "ACTIVE" : it.getStatus().name();
        h.chipStatus.setText(status);

        // termMonths = 0 => không kỳ hạn
        String termText = (it.getTermMonths() <= 0) ? "Không kỳ hạn" : (it.getTermMonths() + "M");
        String rateText = String.format(Locale.US, "%.2f", it.getInterestRate());
        h.tvTermRate.setText("Kỳ hạn: " + termText + "  •  Lãi suất: " + rateText + "%/năm");

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(it);
        });
    }

    static class VH extends RecyclerView.ViewHolder{
        TextView tvCode, tvName, tvDesc, tvTermRate;
        Chip chipStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTermRate = itemView.findViewById(R.id.tvTermAndRate);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
