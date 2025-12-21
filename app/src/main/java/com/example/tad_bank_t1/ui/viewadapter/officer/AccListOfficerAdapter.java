package com.example.tad_bank_t1.ui.viewadapter.officer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.ui.viewmodel.officer.AccountItemUIModel;
import com.google.android.material.chip.Chip;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccListOfficerAdapter extends ListAdapter<AccountItemUIModel, AccListOfficerAdapter.VH> {

    public interface Listener {
        void onCustomerProfile(String userId);
        void onChecking(String userId);
        void onSavings(String userId);
        void onMortgage(String userId);
    }

    private final Listener listener;

    public AccListOfficerAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new VH(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static final DiffUtil.ItemCallback<AccountItemUIModel> DIFF = new DiffUtil.ItemCallback<AccountItemUIModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull AccountItemUIModel oldItem, @NonNull AccountItemUIModel newItem) {
            return oldItem.userId != null && oldItem.userId.equals(newItem.userId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull AccountItemUIModel o, @NonNull AccountItemUIModel n) {
            return safeEq(o.userName, n.userName)
                    && safeEq(o.phone, n.phone)
                    && safeEq(o.email, n.email)
                    && o.checkingCount == n.checkingCount
                    && o.savingsCount == n.savingsCount
                    && o.mortgageCount == n.mortgageCount
                    && listSize(o.checkingAccounts) == listSize(n.checkingAccounts)
                    && listSize(o.savingsAccounts) == listSize(n.savingsAccounts)
                    && listSize(o.mortgageAccounts) == listSize(n.mortgageAccounts);
        }

        private int listSize(List<?> l) { return l == null ? 0 : l.size(); }

        private boolean safeEq(String a, String b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.equals(b);
        }
    };

    static class VH extends RecyclerView.ViewHolder {
        private static final int COLOR_ACTIVE = Color.parseColor("#c22449");
        private static final int COLOR_EMPTY  = Color.parseColor("#9a9a9a");

        private final TextView tvUserName, tvUserId, tvPhone, tvEmail;
        private final ImageView ivExpand;
        private final View detailsContainer;

        private final Chip chipChecking, chipSaving, chipMortgage;
        private final BoomMenuButton boomMenuButton;
        private final Listener listener;

        private final TextView tvSectionChecking, tvCheckingAccounts, tvCheckingTotal;
        private final TextView tvSectionSaving, tvSectionSavings;
        private final TextView tvSectionMortgage, tvSectionMortgages;
        private final TextView txtBadgeChecking, txtBadgeSaving, txtBadgeMortgage;
        private boolean expanded = false;

        VH(@NonNull View itemView, Listener listener) {
            super(itemView);
            this.listener = listener;

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserId   = itemView.findViewById(R.id.tvUserId);
            tvPhone    = itemView.findViewById(R.id.tvPhone);
            tvEmail    = itemView.findViewById(R.id.tvEmail);

            ivExpand = itemView.findViewById(R.id.ivExpand);
            detailsContainer = itemView.findViewById(R.id.detailsContainer);

            chipChecking = itemView.findViewById(R.id.chipChecking);
            chipSaving   = itemView.findViewById(R.id.chipSaving);
            chipMortgage = itemView.findViewById(R.id.chipMortgage);

            boomMenuButton = itemView.findViewById(R.id.bmbAccounts);

            tvSectionChecking  = itemView.findViewById(R.id.tvSectionChecking);
            tvCheckingAccounts = itemView.findViewById(R.id.tvCheckingAccounts);
            tvCheckingTotal    = itemView.findViewById(R.id.tvCheckingTotal);

            tvSectionSaving  = itemView.findViewById(R.id.tvSectionSaving);
            tvSectionSavings = itemView.findViewById(R.id.tvSectionSavings);

            tvSectionMortgage  = itemView.findViewById(R.id.tvSectionMortgage);
            tvSectionMortgages = itemView.findViewById(R.id.tvSectionMortgages);
            txtBadgeChecking = itemView.findViewById(R.id.txtBadgeChecking);
            txtBadgeSaving = itemView.findViewById(R.id.txtBadgeSaving);
            txtBadgeMortgage = itemView.findViewById(R.id.txtBadgeMortgage);

            // expand)
            ivExpand.setOnClickListener(v -> {
                expanded = !expanded;
                applyExpanded();
            });

            expanded = false;
            applyExpanded();
        }

        void bind(AccountItemUIModel u) {
            tvUserName.setText(!TextUtils.isEmpty(u.userName) ? u.userName : "Customer");
            tvUserId.setText(u.userId != null ? u.userId : "");
            tvPhone.setText(!TextUtils.isEmpty(u.phone) ? u.phone : "");
            tvEmail.setText(!TextUtils.isEmpty(u.email) ? u.email : "");

            chipChecking.setText("Checking");
            chipSaving.setText("Savings");
            chipMortgage.setText("Mortgage");

            applyChipStyleAndBadge(chipChecking, txtBadgeChecking, u.checkingCount);
            applyChipStyleAndBadge(chipSaving,   txtBadgeSaving,   u.savingsCount);
            applyChipStyleAndBadge(chipMortgage, txtBadgeMortgage, u.mortgageCount);

            bindExpandedOverview(u);
            setupBoomMenu(u);

            expanded = false;
            applyExpanded();
        }

        private void applyExpanded() {
            detailsContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
            ivExpand.setRotation(expanded ? 180f : 0f);
        }
        private void applyChipStyleAndBadge(Chip chip, TextView badge, int count) {
            boolean has = count > 0;

            chip.setChipBackgroundColor(ColorStateList.valueOf(has ? COLOR_ACTIVE : COLOR_EMPTY));
            chip.setTextColor(Color.WHITE);
            if (!has) {
                badge.setVisibility(View.GONE);
                return;
            }
            String text = (count > 99) ? "99+" : String.valueOf(count);
            badge.setText(text);
            badge.setVisibility(View.VISIBLE);
        }

        private void bindExpandedOverview(AccountItemUIModel u) {
            // ===== CHECKING =====
            if (u.checkingAccounts != null && !u.checkingAccounts.isEmpty()) {
                tvSectionChecking.setVisibility(View.VISIBLE);
                tvCheckingAccounts.setVisibility(View.VISIBLE);
                tvCheckingTotal.setVisibility(View.VISIBLE);

                tvCheckingAccounts.setText(joinMaskedLines(u.checkingAccounts));
                tvCheckingTotal.setText("💰 Tổng số dư: " + formatVnd(sumBalance(u.checkingAccounts)));
            } else {
                tvSectionChecking.setVisibility(View.VISIBLE);
                tvCheckingAccounts.setVisibility(View.VISIBLE);
                tvCheckingTotal.setVisibility(View.VISIBLE);

                tvCheckingAccounts.setText("-");
                tvCheckingTotal.setText("💰 Tổng số dư: 0 VND");
            }

            // ===== SAVINGS =====
            tvSectionSaving.setVisibility(View.VISIBLE);
            tvSectionSavings.setVisibility(View.VISIBLE);

            if (u.savingsAccounts != null && !u.savingsAccounts.isEmpty()) {
                tvSectionSavings.setText(buildSavingsSummary(u.savingsAccounts));
            } else {
                tvSectionSavings.setText("-");
            }

            // ===== MORTGAGE =====
            if (u.mortgageAccounts != null && !u.mortgageAccounts.isEmpty()) {
                tvSectionMortgage.setVisibility(View.VISIBLE);
                tvSectionMortgages.setVisibility(View.VISIBLE);
                tvSectionMortgages.setText(buildMortgageSummary(u.mortgageAccounts));
            } else {
                tvSectionMortgage.setVisibility(View.GONE);
                tvSectionMortgages.setVisibility(View.GONE);
            }
        }

        private String buildSavingsSummary(List<Account> list) {
            int total = list.size();
            int term = 0;
            int demand = 0;

            long sum = 0L;
            long max = -1L;
            String maxName = null;

            int active = 0;

            for (Account a : list) {
                if (a == null) continue;

                Long b = a.getBalance();
                if (b != null) {
                    sum += b;
                    if (b > max) {
                        max = b;
                        maxName = safeName(a.getAccountName());
                    }
                }

                String st = a.getStatus() != null ? a.getStatus().name() : "";
                if ("OPEN".equalsIgnoreCase(st) || st.toUpperCase(Locale.ROOT).contains("ACTIVE")) active++;

                if (isTermSaving(a)) term++; else demand++;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("📊 ").append(total).append(" sổ tiết kiệm (")
                    .append(term).append(" có kỳ hạn, ")
                    .append(demand).append(" không kỳ hạn)\n");
            sb.append("💰 Tổng số dư: ").append(formatVnd(sum)).append("\n");

            if (max >= 0) {
                sb.append("📈 Sổ cao nhất: ").append(formatVnd(max));
                if (!TextUtils.isEmpty(maxName)) sb.append(" (").append(maxName).append(")");
                sb.append("\n");
            }

            sb.append("🟢 ").append(active).append("/").append(total).append(" sổ đang hoạt động");
            return sb.toString();
        }

        private boolean isTermSaving(Account a) {
            String name = safeName(a.getAccountName()).toLowerCase(Locale.ROOT);
            return name.contains("fix")
                    || name.contains("term")
                    || name.contains("ky han")
                    || name.contains("kỳ hạn")
                    || name.matches(".*\\d+m.*")
                    || name.matches(".*\\d+y.*");
        }

        private String buildMortgageSummary(List<Account> list) {
            int total = list.size();

            long sumDebt = 0L;
            long max = -1L;

            long overdueSum = 0L;
            int overdueCount = 0;

            Date nearest = null;
            long nearestAmount = 0L;

            for (Account a : list) {
                if (a == null) continue;

                Long b = a.getBalance();
                long debt = (b != null ? b : 0L);
                sumDebt += debt;
                if (debt > max) max = debt;

                String st = a.getStatus() != null ? a.getStatus().name() : "";
                boolean overdue = st.toUpperCase(Locale.ROOT).contains("OVERDUE")
                        || st.toUpperCase(Locale.ROOT).contains("LATE");

                if (overdue) {
                    overdueCount++;
                    overdueSum += debt;
                }

                Date d = a.getCreatedAt(); // nếu có dueDate thật thì thay bằng dueDate
                if (d != null) {
                    if (nearest == null || d.before(nearest)) {
                        nearest = d;
                        nearestAmount = debt;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("🏠 ").append(total).append(" khoản vay thế chấp\n");
            sb.append("💰 Tổng dư nợ: ").append(formatVnd(sumDebt)).append("\n");
            sb.append("⚠️ Quá hạn: ").append(formatVnd(overdueSum))
                    .append(" (").append(overdueCount).append(" khoản)\n");

            if (nearest != null) {
                sb.append("📅 Đến hạn gần nhất: ").append(formatVnd(nearestAmount))
                        .append(" (").append(formatDate(nearest)).append(")\n");
            }

            sb.append("📈 Khoản vay lớn nhất: ").append(formatVnd(Math.max(0L, max)));
            return sb.toString();
        }

        private String formatDate(Date d) {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
        }

        private void setupBoomMenu(AccountItemUIModel u) {
            boomMenuButton.clearBuilders();
            final String userId = u.userId;

            String[] titles = new String[]{
                    !TextUtils.isEmpty(u.userName) ? u.userName : "Customer",
                    "Customer Profile",
                    "Checking Account",
                    "Savings Account",
                    "Mortgage Account"
            };

            String[] subs = new String[]{
                    "UID: " + (u.userId != null ? u.userId : ""),
                    "Personal info & KYC",
                    u.checkingCount > 0 ? ("Total: " + u.checkingCount) : "No checking account",
                    u.savingsCount > 0 ? ("Total: " + u.savingsCount) : "No savings account",
                    u.mortgageCount > 0 ? ("Total: " + u.mortgageCount) : "No mortgage account"
            };

            int n = boomMenuButton.getPiecePlaceEnum().pieceNumber(); // ham_5 => 5
            for (int i = 0; i < n; i++) {
                final int index = i;
                HamButton.Builder builder = new HamButton.Builder()
                        .normalText(titles[index])
                        .subNormalText(subs[index])
                        .listener(clickedIndex -> {
                            if (listener == null || userId == null) return;
                            switch (index) {
                                case 0:
                                case 1:
                                    listener.onCustomerProfile(userId);
                                    break;
                                case 2:
                                    listener.onChecking(userId);
                                    break;
                                case 3:
                                    listener.onSavings(userId);
                                    break;
                                case 4:
                                    listener.onMortgage(userId);
                                    break;
                            }
                        });
                boomMenuButton.addBuilder(builder);
            }
        }

        // ===== helpers =====

        private String joinMaskedLines(List<Account> list) {
            StringBuilder sb = new StringBuilder();
            int limit = Math.min(list.size(), 10);
            for (int i = 0; i < limit; i++) {
                Account a = list.get(i);
                if (a == null) continue;
                if (sb.length() > 0) sb.append("\n");
                sb.append(maskAccountNumber(a.getAccountNumber()));
            }
            if (list.size() > 10) sb.append("\n...");
            return sb.length() == 0 ? "-" : sb.toString();
        }

        private long sumBalance(List<Account> list) {
            long sum = 0L;
            for (Account a : list) {
                if (a == null) continue;
                Long b = a.getBalance();
                if (b != null) sum += b;
            }
            return sum;
        }

        private String formatVnd(long v) {
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(v) + " VND";
        }

        private String maskAccountNumber(String accNum) {
            if (accNum == null) return "****";
            String digits = accNum.replaceAll("\\s+", "");
            if (digits.length() <= 4) return "****" + digits;
            return "****" + digits.substring(digits.length() - 4);
        }

        private String safeName(String s) {
            return s == null ? "" : s.trim();
        }
    }
}
