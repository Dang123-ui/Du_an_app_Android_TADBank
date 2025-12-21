package com.example.tad_bank_t1.ui.viewadapter.officer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.data.model.RiskLog;
import com.example.tad_bank_t1.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RiskLogAdapter extends ListAdapter<RiskLog, RiskLogAdapter.VH> {
    public RiskLogAdapter() { super(DIFF); }
    private static final DiffUtil.ItemCallback<RiskLog> DIFF = new DiffUtil.ItemCallback<RiskLog>() {
        @Override
        public boolean areItemsTheSame(@NonNull RiskLog oldItem, @NonNull RiskLog newItem) {
            if (oldItem.id == null || newItem.id == null) return oldItem == newItem;
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull RiskLog oldItem, @NonNull RiskLog newItem) {
            String o = String.valueOf(oldItem.fromRisk) + "|" + String.valueOf(oldItem.toRisk) + "|" +
                    String.valueOf(oldItem.reason) + "|" + String.valueOf(oldItem.createdAt);
            String n = String.valueOf(newItem.fromRisk) + "|" + String.valueOf(newItem.toRisk) + "|" +
                    String.valueOf(newItem.reason) + "|" + String.valueOf(newItem.createdAt);
            return o.equals(n);
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_risk_alert, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        RiskLog item = getItem(position);

        h.tvTitle.setText("Risk changed");

        String desc = (item.fromRisk == null ? "—" : item.fromRisk.name()) +
                " -> " + (item.toRisk == null ? "—" : item.toRisk.name());
        h.tvDesc.setText(desc);

        String reason = (item.reason == null || item.reason.trim().isEmpty())
                ? "Reason: —"
                : "Reason: " + item.reason.trim();
        h.tvReason.setText(reason);

        if (item.createdAt != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            h.tvTime.setText(df.format(item.createdAt));
        } else {
            h.tvTime.setText("—");
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvReason, tvTime;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRiskEventTitle);
            tvDesc = itemView.findViewById(R.id.tvRiskEventDesc);
            tvReason = itemView.findViewById(R.id.tvRiskEventReason);
            tvTime = itemView.findViewById(R.id.tvRiskEventTime);
        }
    }
}
