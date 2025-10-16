package com.example.tad_bank_t1.ui.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Notification;

import java.util.List;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.ViewHolder> {
    private List<Notification> notifications;

    public NotiAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setNotiData(List<Notification> notifications){
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti_balance_alert, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();
        Notification notification = notifications.get(pos);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTimeNoti;
        private TextView txtNotiContent;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTimeNoti = itemView.findViewById(R.id.txtTimeNoti);
            txtNotiContent = itemView.findViewById(R.id.txtNotiContent);
        }

        public void bind(Notification notification) {
            txtTimeNoti.setText(notification.getCreatedAt().toString());
            txtNotiContent.setText(notification.getTitle() + " | | + " + notification.getMessage());
        }
    }
}
