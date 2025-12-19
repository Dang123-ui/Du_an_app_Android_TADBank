package com.example.tad_bank_t1.saving.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.saving.model.AccountStatus;
import com.example.tad_bank_t1.saving.model.PaymentMethod;
import com.example.tad_bank_t1.saving.model.SavingsAccount;
import com.example.tad_bank_t1.saving.utils.FormatUtils;

import java.util.List;

public class SavingsAccountAdapter extends RecyclerView.Adapter<SavingsAccountAdapter.ViewHolder> {

    private List<SavingsAccount> accounts;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewDetailsClick(SavingsAccount account);
    }

    public SavingsAccountAdapter(List<SavingsAccount> accounts, OnItemClickListener listener) {
        this.accounts = accounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_savings_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingsAccount account = accounts.get(position);

        // Account number
        holder.tvAccountNumber.setText(account.getAccountNumber());

        // Status dot
        holder.statusDot.setBackgroundResource(FormatUtils.getStatusDotDrawable(account.getStatus()));

        // Status badge
        holder.tvStatusBadge.setBackgroundResource(FormatUtils.getStatusBadgeDrawable(account.getStatus()));
        FormatUtils.ColorPair colors = FormatUtils.getStatusBadgeColors(account.getStatus());
        holder.tvStatusBadge.setBackgroundColor(colors.backgroundColor);
        holder.tvStatusBadge.setTextColor(colors.textColor);

        if (account.getStatus() == AccountStatus.ACTIVE) {
            holder.tvStatusBadge.setText(R.string.status_active);
        } else {
            holder.tvStatusBadge.setText(R.string.status_closed);
        }

        // Balance
        holder.tvBalance.setText(FormatUtils.formatCurrency(account.getBalance(), account.getCurrency()));

        // Term
        String term = FormatUtils.formatDate(account.getStartDate()) + " – " +
                      FormatUtils.formatDate(account.getMaturityDate());
        holder.tvTerm.setText(term);

        // Interest rate
        holder.tvInterestRate.setText(String.format("%.1f%%", account.getInterestRate()));

        // Payment method
        if (account.getPaymentMethod() == PaymentMethod.MONTHLY) {
            holder.tvPaymentMethod.setText(R.string.payment_monthly);
        } else {
            holder.tvPaymentMethod.setText(R.string.payment_quarterly);
        }

        // View details button
        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(account);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void updateData(List<SavingsAccount> newAccounts) {
        this.accounts = newAccounts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View statusDot;
        TextView tvAccountNumber;
        TextView tvStatusBadge;
        TextView tvBalance;
        TextView tvTerm;
        TextView tvInterestRate;
        TextView tvPaymentMethod;
        Button btnViewDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusDot = itemView.findViewById(R.id.statusDot);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvBalance = itemView.findViewById(R.id.tvBalance);
            tvTerm = itemView.findViewById(R.id.tvTerm);
            tvInterestRate = itemView.findViewById(R.id.tvInterestRate);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

