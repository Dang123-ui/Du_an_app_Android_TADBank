package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.model.enums.mortgage.MortgageInstallmentStatus;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách các kỳ thanh toán (schedule) của một khoản vay thế chấp.
 * Mỗi item hiển thị ngày đến hạn, số tiền phải trả và trạng thái (PENDING, PAID, OVERDUE).
 * Có một listener để xử lý sự kiện khi người dùng bấm vào một kỳ cụ thể (nếu cần).
 */
public class MortgageScheduleAdapter
        extends RecyclerView.Adapter<MortgageScheduleAdapter.ScheduleViewHolder> {

    /**
     * Interface callback khi click vào item.
     */
    public interface OnScheduleClickListener {
        void onScheduleClick(MortgagePaymentSchedule schedule);
    }

    private final OnScheduleClickListener listener;
    private List<MortgagePaymentSchedule> schedules = new ArrayList<>();

    public MortgageScheduleAdapter(OnScheduleClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mortgage_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        MortgagePaymentSchedule schedule = schedules.get(position);
        holder.bind(schedule, listener);
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    public void setSchedules(List<MortgagePaymentSchedule> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    /**
     * Trả về danh sách hiện tại đang hiển thị.  Hữu ích để kiểm tra
     * trạng thái của các kỳ thanh toán khi cần lựa chọn kỳ cần trả.
     */
    public List<MortgagePaymentSchedule> getSchedules() {
        return schedules;
    }

    /**
     * ViewHolder cho từng kỳ thanh toán.
     */
    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDueDate;
        private final TextView txtStatus;
        private final TextView txtAmountDue;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDueDate = itemView.findViewById(R.id.txtScheduleDueDate);
            txtStatus = itemView.findViewById(R.id.txtScheduleStatus);
            txtAmountDue = itemView.findViewById(R.id.txtScheduleAmountDue);
        }

        /**
         * Bind dữ liệu cho view.
         *
         * @param schedule đối tượng kỳ thanh toán
         * @param listener callback khi click
         */
        public void bind(MortgagePaymentSchedule schedule, OnScheduleClickListener listener) {
            // Format ngày đến hạn
            if (schedule.getDueDate() != null) {
                // Nếu dueDate lưu dạng yyyy-MM-dd thì dùng DateTimeUtil để format
                // else set raw string
                txtDueDate.setText(DateTimeUtil.formatDateToVNDate((schedule.getDueDate())));

            } else {
                txtDueDate.setText("--");
            }

            // Số tiền phải trả
            if (schedule.getAmountDue() != null) {
                txtAmountDue.setText(CurrencyUtil.formatVND(schedule.getAmountDue().longValue()));
            } else {
                txtAmountDue.setText("--");
            }

            // Trạng thái
            MortgageInstallmentStatus status = schedule.getStatus();
            if (status != null) {
                txtStatus.setText(status.name());
            } else {
                txtStatus.setText("PENDING");
            }

            // Click event
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onScheduleClick(schedule);
                }
            });
        }
    }
}
