package com.example.tad_bank_t1.ui.viewadapter.ai;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.data.model.ai.HeadlineItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.tad_bank_t1.R;
public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineAdapter.VH> {

    private final List<HeadlineItem> items = new ArrayList<>();

    public void setItems(List<HeadlineItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_headline, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        HeadlineItem it = items.get(position);
        h.tvTitle.setText(it.title);

        String meta = String.format(Locale.US,
                "impact=%.2f | sentiment=%.2f | %s",
                it.impact, it.sentiment,
                (it.published_at == null ? "" : it.published_at)
        );
        h.tvMeta.setText(meta);

        h.itemView.setOnClickListener(v -> {
            if (it.link == null || it.link.trim().isEmpty()) return;
            Context ctx = v.getContext();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(it.link));
            ctx.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHeadlineTitle);
            tvMeta = itemView.findViewById(R.id.tvHeadlineMeta);
        }
    }
}
