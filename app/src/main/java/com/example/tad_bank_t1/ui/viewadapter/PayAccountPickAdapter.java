package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;

import java.util.ArrayList;
import java.util.List;

public class PayAccountPickAdapter extends RecyclerView.Adapter<PayAccountPickAdapter.VH> {

    public interface OnPick { void onPick(Account acc); }

    private final List<Account> data = new ArrayList<>();
    private final OnPick onPick;

    // dùng field unique của bạn (accountId / accountNumber)
    private String selectedId;

    public PayAccountPickAdapter(OnPick onPick) {
        this.onPick = onPick;
    }

    public void setData(List<Account> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    public void setSelected(Account acc) {
        selectedId = (acc == null) ? null : acc.getAccountId(); // đổi theo field của bạn
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pay_account_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Account a = data.get(pos);

        h.tvTitle.setText(a.getAccountName() + " • " + a.getAccountNumber());
        h.tvSub.setText("Số dư: " + a.getBalance()); // format lại theo app bạn

        boolean isSelected = selectedId != null && selectedId.equals(a.getAccountId());
        h.itemView.setAlpha(isSelected ? 1f : 0.85f);

        h.itemView.setOnClickListener(v -> onPick.onPick(a));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPayAccountTitle);
            tvSub = itemView.findViewById(R.id.tvPayAccountSub);
        }
    }
}
