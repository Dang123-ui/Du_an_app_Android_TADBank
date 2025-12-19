package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.remote.Provider;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
 
public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
    private List<com.example.tad_bank_t1.data.model.remote.Provider> data;
    private OnItemClickListener listener;

    public ProviderAdapter(){}
    public ProviderAdapter(List<Provider> data) {
        this.data = data;
    }

    public void setData(List<Provider> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Provider Provider);
    }



    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_bank, parent, false);

        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
            return;
        }

        Provider Provider = data.get(pos);
        holder.bind(Provider);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(Provider);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ProviderViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProviderLogo;
        private TextView txtProviderName;
        private TextView txtProviderLongName;

        public ProviderViewHolder(View itemView) {
            super(itemView);
            ivProviderLogo = itemView.findViewById(R.id.imbtBankLogo);
            txtProviderName = itemView.findViewById(R.id.txtBankName);
            txtProviderLongName = itemView.findViewById(R.id.txtBankLongName);
        }

        public void bind(Provider provider) {
            // load anh
            Glide.with(itemView.getContext())
                    .load(provider.getLogo())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.bg_card_white_with_ripple)
                    .into(ivProviderLogo);
            txtProviderName.setText(provider.getProviderId());
            txtProviderLongName.setText(provider.getName());
        }
    }


}

