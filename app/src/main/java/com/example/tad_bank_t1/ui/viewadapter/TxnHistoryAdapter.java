package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.TransactionUtil;

import org.w3c.dom.Text;

import java.util.List;

public class TxnHistoryAdapter extends RecyclerView.Adapter<TxnHistoryAdapter.ViewHolder> {
    private List<Transaction> data;

    public TxnHistoryAdapter(List<Transaction> data) {
        this.data = data;
    }
    public void setData(List<Transaction> data) {
        this.data = data;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TxnHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
        return new TxnHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TxnHistoryAdapter.ViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();

        Transaction txn = data.get(pos);
        holder.bind(txn);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTxnHistoryDate, txtTxnHistoryContent, txtTxnHistoryAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTxnHistoryAmount = itemView.findViewById(R.id.txtTxnHistoryAmount);
            txtTxnHistoryContent = itemView.findViewById(R.id.txtTxnHistoryContent);
            txtTxnHistoryDate = itemView.findViewById(R.id.txtTxnHistoryDate);
        }

        public void bind(Transaction txn){
            String dateTimeLocal = DateTimeUtil.localDateTimeToStr(txn.createAt);
            txtTxnHistoryDate.setText(dateTimeLocal);
            txtTxnHistoryContent.setText(txn.getDescription() + " | " +
                        txn.getCounterpartyAccount() + " | " +
                        txn.getCounterpartyName() + " | " +
                        txn.getCounterpartyBankCode() + " | " +
                        txn.getFeeAmount().toString()
                    );
            boolean isIncoming = TransactionUtil.isIncoming(txn);
            txtTxnHistoryAmount.setText((isIncoming ?  "+" : "-") + txn.getAmount().toString());
            if (!isIncoming)
                txtTxnHistoryAmount.setTextColor(itemView.getResources().getColor(R.color.secondaryColor));
            else
                txtTxnHistoryAmount.setTextColor(itemView.getResources().getColor(R.color.green));
        }
    }
}
