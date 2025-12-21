package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.MortgageAccount;
import com.example.tad_bank_t1.data.model.enums.mortgage.MortgagePaymentFrequency;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.MortgageCalculator;

import java.util.List;

/**
 * Adapter hiển thị danh sách tài khoản thế chấp.  Mỗi item hiển thị số
 * tài khoản, số tiền phải trả mỗi kỳ và ngày đến hạn tiếp theo.  Adapter
 * này sử dụng layout riêng `item_card_mortgage_account.xml` để tách biệt
 * giao diện khoản vay khỏi các loại tài khoản khác (checking/saving).
 */
public class MortgageAccountListAdapter extends RecyclerView.Adapter<MortgageAccountListAdapter.ViewHolder> {
    private List<Account> data;
    private OnClickMortgageListener listener;

    public MortgageAccountListAdapter(List<Account> data) {
        this.data = data;
    }

    public interface OnClickMortgageListener {
        void onClickOpenMortgageDetail(Account account);
    }

    public void setOnClickMortgageListener(OnClickMortgageListener listener) {
        this.listener = listener;
    }

    public void setData(List<Account> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_mortgage_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();
        Account account = data.get(pos);
        holder.bind(account);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickOpenMortgageDetail(account);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtAccountNumber;
        private final TextView txtAmountDue;
        private final TextView txtNextDueDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAccountNumber = itemView.findViewById(R.id.txtItemMortgageAccountNumber);
            txtAmountDue = itemView.findViewById(R.id.txtItemMortgageAmountDue);
            txtNextDueDate = itemView.findViewById(R.id.txtItemMortgageNextDueDate);
        }

        void bind(Account account) {
            // Số tài khoản hiển thị trực tiếp
            txtAccountNumber.setText(account.getAccountNumber());
            // Lấy thông tin thế chấp để tính toán
            MortgageAccount mortgage = account.getMortgage();
            if (mortgage != null) {
                // Tính số tiền phải trả mỗi kỳ bằng util
                long amountDue = MortgageCalculator.calculateAmountDuePerPeriod(
                        mortgage.getPrincipalAmount(),
                        mortgage.getInterestRateAnnual(),
                        mortgage.getTermMonths(),
                        mortgage.getPaymentFrequency() != null ? mortgage.getPaymentFrequency() : MortgagePaymentFrequency.MONTHLY
                );
                txtAmountDue.setText(CurrencyUtil.formatVND(amountDue));
                // Ngày đến hạn tiếp theo, dùng util để format
                if (mortgage.getNextDueDate() != null) {
                    txtNextDueDate.setText(DateTimeUtil.formatDateToVNDate(mortgage.getNextDueDate()));
                } else {
                    txtNextDueDate.setText("--");
                }
            } else {
                txtAmountDue.setText("--");
                txtNextDueDate.setText("--");
            }
        }
    }
}