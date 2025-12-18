package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.util.CurrencyUtil;

import java.util.List;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {
    private List<Account> data;
    public AccountListAdapter(){}

    public AccountListAdapter(List<Account> data) {
        this.data = data;
    }

    public interface OnclickAccountListener{
        void onClickOpenAccountTxnHistory(Account account);
    }
    private OnclickAccountListener listener;
    public void setOnclickAccountListener(OnclickAccountListener listener){
        this.listener = listener;
    }

    public void setData(List<Account> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_account_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();
        Account account = data.get(pos);
        holder.bind(account);

        holder.imbtOpenAccTxnHistory.setOnClickListener(null);
        holder.imbtOpenAccTxnHistory.setOnClickListener(v -> {
            listener.onClickOpenAccountTxnHistory(account);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemAccountAccName, txtItemAccountAccNumber, txtItemAccountBalance;
        private ImageButton imbtOpenAccTxnHistory;

        public ViewHolder(View view) {
            super(view);
            txtItemAccountAccName = view.findViewById(R.id.txtItemAccountAccName);
            txtItemAccountAccNumber = view.findViewById(R.id.txtItemAccountAccNumber);
            txtItemAccountBalance = view.findViewById(R.id.txtItemAccountAccBalance);
            imbtOpenAccTxnHistory = view.findViewById(R.id.imbtOpenAccTxnHistory);
        }

        public void bind(Account account){
            txtItemAccountAccName.setText(account.getAccountName());
            txtItemAccountAccNumber.setText(account.getAccountNumber());
            txtItemAccountBalance.setText(CurrencyUtil.formatAmount(account.getBalance()) + " " + account.getCurrency());

        }
    }
}
