package com.example.tad_bank_t1.ui.viewadapter.officer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewmodel.officer.AccountsCardViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountsCardAdapter {

    public interface Actions {
        void onAccountClicked(AccountsCardViewModel.AccountItem item);
    }

    private final View root;
    private final Actions actions;

    private final Chip chipChecking, chipSaving, chipMortgage;
    private final TextView badgeChecking, badgeSaving, badgeMortgage;
    private final RecyclerView rv;

    private final MiniAccountAdapter miniAdapter;

    public AccountsCardAdapter(@NonNull View cardAccountsRoot, @NonNull Actions actions) {
        this.root = cardAccountsRoot;
        this.actions = actions;

        chipChecking = root.findViewById(R.id.chipChecking);
        chipSaving = root.findViewById(R.id.chipSaving);
        chipMortgage = root.findViewById(R.id.chipMortgage);

        badgeChecking = root.findViewById(R.id.txtBadgeChecking);
        badgeSaving = root.findViewById(R.id.txtBadgeSaving);
        badgeMortgage = root.findViewById(R.id.txtBadgeMortgage);

        rv = root.findViewById(R.id.rvAccountsMini);
        rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
        rv.setNestedScrollingEnabled(false);

        miniAdapter = new MiniAccountAdapter(actions);
        rv.setAdapter(miniAdapter);
    }

    /** Click chip -> filter. Long click chip -> ALL */
    public void wireFilterClicks(@NonNull AccountsCardViewModel vm) {
        chipChecking.setOnClickListener(v -> vm.setFilter(AccountsCardViewModel.FilterType.CHECKING));
        chipSaving.setOnClickListener(v -> vm.setFilter(AccountsCardViewModel.FilterType.SAVING));
        chipMortgage.setOnClickListener(v -> vm.setFilter(AccountsCardViewModel.FilterType.MORTGAGE));

        View.OnLongClickListener backAll = v -> {
            vm.setFilter(AccountsCardViewModel.FilterType.ALL);
            return true;
        };
        chipChecking.setOnLongClickListener(backAll);
        chipSaving.setOnLongClickListener(backAll);
        chipMortgage.setOnLongClickListener(backAll);
    }

    public void bind(@NonNull AccountsCardViewModel.AccountsCardUiState s) {
        setBadge(badgeChecking, s.countChecking);
        setBadge(badgeSaving, s.countSaving);
        setBadge(badgeMortgage, s.countMortgage);

        applyChipSelected(chipChecking, s.filter == AccountsCardViewModel.FilterType.CHECKING);
        applyChipSelected(chipSaving, s.filter == AccountsCardViewModel.FilterType.SAVING);
        applyChipSelected(chipMortgage, s.filter == AccountsCardViewModel.FilterType.MORTGAGE);

        miniAdapter.submit(s.visible);
    }

    private void setBadge(TextView tv, int count) {
        if (count <= 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(String.valueOf(count));
        }
    }

    private void applyChipSelected(Chip chip, boolean selected) {
        if (selected) {
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F7E7EA")));
            chip.setTextColor(Color.parseColor("#C32248"));
        } else {
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F1F3F5")));
            chip.setTextColor(Color.parseColor("#495057"));
        }
    }

    /* ================= RecyclerView Adapter for item_account_mini.xml ================= */

    private static class MiniAccountAdapter extends RecyclerView.Adapter<MiniAccountVH> {

        private final Actions actions;
        private final List<AccountsCardViewModel.AccountItem> data = new ArrayList<>();

        MiniAccountAdapter(Actions actions) {
            this.actions = actions;
        }

        void submit(List<AccountsCardViewModel.AccountItem> list) {
            data.clear();
            if (list != null) data.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MiniAccountVH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_account_mini, parent, false);
            return new MiniAccountVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MiniAccountVH h, int position) {
            AccountsCardViewModel.AccountItem it = data.get(position);

            String type = upper(it.type);

            h.tvTitle.setText(prettyType(type));
            h.tvStatus.setText(upper(it.status));
            applyStatusStyle(h.tvStatus, it.status);

            String accMasked = nz(it.accountNumber);
            h.tvAccountNo.setText("Số TK: " + accMasked);

            // reset all blocks
            h.tvCheckingBalance.setVisibility(View.GONE);

            h.tvSavingContract.setVisibility(View.GONE);
            h.tvSavingTerm.setVisibility(View.GONE);
            h.tvSavingBalance.setVisibility(View.GONE);
            h.tvSavingInterest.setVisibility(View.GONE);

            h.tvMortgageOutstanding.setVisibility(View.GONE);
            h.tvMortgageOverdue.setVisibility(View.GONE);
            h.tvMortgageNextDue.setVisibility(View.GONE);

            if ("CHECKING".equals(type)) {
                h.tvCheckingBalance.setVisibility(View.VISIBLE);
                h.tvCheckingBalance.setText("Số dư: " + AccountsCardViewModel.formatMoney(it.amount, it.currency));

            } else if ("SAVING".equals(type) || "SAVINGS".equals(type)) {
                h.tvSavingContract.setVisibility(View.VISIBLE);
                h.tvSavingTerm.setVisibility(View.VISIBLE);
                h.tvSavingBalance.setVisibility(View.VISIBLE);
                h.tvSavingInterest.setVisibility(View.VISIBLE);

                String contract = nz(it.savingContractCode);
                h.tvSavingContract.setText("Mã HĐ: " + (contract.isEmpty() ? "—" : contract));

                h.tvSavingTerm.setText(buildSavingTermText(it.savingTermMonths));
                h.tvSavingBalance.setText("Số dư: " + AccountsCardViewModel.formatMoney(it.amount, it.currency));

                String rate = (it.savingInterestRate != null)
                        ? stripTrailingZero(it.savingInterestRate) + "%/năm"
                        : "—";
                h.tvSavingInterest.setText("Lãi suất: " + rate);

            } else if ("MORTGAGE".equals(type)) {
                h.tvMortgageOutstanding.setVisibility(View.VISIBLE);
                h.tvMortgageOverdue.setVisibility(View.VISIBLE);
                h.tvMortgageNextDue.setVisibility(View.VISIBLE);

                h.tvMortgageOutstanding.setText("Dư nợ: " + AccountsCardViewModel.formatMoney(it.amount, it.currency));

                long overdue = it.mortgageOverdueAmount != null ? it.mortgageOverdueAmount : 0L;
                h.tvMortgageOverdue.setText("Quá hạn: " + AccountsCardViewModel.formatMoney(overdue, it.currency));

                String nextDue = AccountsCardViewModel.formatDate(it.mortgageNextDueDate);
                h.tvMortgageNextDue.setText("Hạn gần nhất: " + (nextDue.isEmpty() ? "—" : nextDue));
            }

            h.itemView.setOnClickListener(v -> {
                if (actions != null) actions.onAccountClicked(it);
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private static String buildSavingTermText(Integer months) {
            if (months == null || months <= 0) return "Kỳ hạn: Không kỳ hạn";
            return "Kỳ hạn: " + months + " tháng (Có kỳ hạn)";
        }

        private static String prettyType(String t) {
            if ("CHECKING".equals(t)) return "Checking";
            if ("SAVING".equals(t) || "SAVINGS".equals(t)) return "Saving";
            if ("MORTGAGE".equals(t)) return "Mortgage";
            return t;
        }

        private static void applyStatusStyle(TextView tv, String status) {
            String s = upper(status);
            int fg;
            switch (s) {
                case "ACTIVE": fg = Color.parseColor("#C32248"); break;
                case "LOCKED": fg = Color.parseColor("#0F5132"); break;
                case "CLOSED": fg = Color.parseColor("#6C757D"); break;
                default: fg = Color.parseColor("#495057"); break;
            }
            tv.setTextColor(fg);
        }

        private static String nz(String s) { return s == null ? "" : s.trim(); }
        private static String upper(String s) { return nz(s).toUpperCase(Locale.ROOT); }

        private static String stripTrailingZero(Double d) {
            if (d == null) return "";
            double v = d;
            if (Math.floor(v) == v) return String.valueOf((long) v);
            return String.valueOf(v);
        }
    }

    private static class MiniAccountVH extends RecyclerView.ViewHolder {

        TextView tvTitle, tvStatus, tvAccountNo;

        TextView tvCheckingBalance;

        TextView tvSavingContract, tvSavingTerm, tvSavingBalance, tvSavingInterest;

        TextView tvMortgageOutstanding, tvMortgageOverdue, tvMortgageNextDue;

        MiniAccountVH(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAccountNo = itemView.findViewById(R.id.tvAccountNo);

            tvCheckingBalance = itemView.findViewById(R.id.tvCheckingBalance);

            tvSavingContract = itemView.findViewById(R.id.tvSavingContract);
            tvSavingTerm = itemView.findViewById(R.id.tvSavingTerm);
            tvSavingBalance = itemView.findViewById(R.id.tvSavingBalance);
            tvSavingInterest = itemView.findViewById(R.id.tvSavingInterest);

            tvMortgageOutstanding = itemView.findViewById(R.id.tvMortgageOutstanding);
            tvMortgageOverdue = itemView.findViewById(R.id.tvMortgageOverdue);
            tvMortgageNextDue = itemView.findViewById(R.id.tvMortgageNextDue);
        }
    }

    private static String upper(String s) {
        return (s == null ? "" : s.trim()).toUpperCase(Locale.ROOT);
    }
}
