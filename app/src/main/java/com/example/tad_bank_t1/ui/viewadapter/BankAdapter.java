package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Bank;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {
    private List<Bank> data;
    private OnItemClickListener listener;

    public BankAdapter(){}
    public BankAdapter(List<Bank> data) {
        this.data = data;
    }

    public void setData(List<Bank> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Bank bank);
    }



    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_bank, parent, false);

        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
            return;
        }

        Bank bank = data.get(pos);
        holder.bind(bank);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(bank);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BankViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivBankLogo;
        private TextView txtBankName;
        private TextView txtBankLongName;

        public BankViewHolder(View itemView) {
            super(itemView);
            ivBankLogo = itemView.findViewById(R.id.imbtBankLogo);
            txtBankName = itemView.findViewById(R.id.txtBankName);
            txtBankLongName = itemView.findViewById(R.id.txtBankLongName);
        }

        public void bind(Bank bank) {
            // load anh
            Glide.with(itemView.getContext())
                    .load(bank.getBankImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.bg_card_white_with_ripple)
                    .into(ivBankLogo);
            txtBankName.setText(bank.getBankName());
            txtBankLongName.setText(bank.getBankLongName());
        }
    }


}
