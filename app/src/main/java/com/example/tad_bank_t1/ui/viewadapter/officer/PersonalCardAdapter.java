package com.example.tad_bank_t1.ui.viewadapter.officer;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.tad_bank_t1.ui.viewmodel.officer.PersonalCardUiState;
import com.example.tad_bank_t1.util.DataUriUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.tad_bank_t1.R;
public class PersonalCardAdapter {
    public interface Actions {
        void onEdit();
        void onCall(String phone);
        void onSms(String phone);
        void onEmail(String email);
    }
    private final View root;
    private final Actions actions;

    private final ShapeableImageView imgAvatar;
    private final ImageButton btnEdit;

    private final TextView tvCustomerName;
    private final TextView tvCustomerUid;

    private final Chip chipStatus;
    private final Chip chipSegment;
    private final Chip chipRisk;

    private final TextView tvDobValue;
    private final TextView tvGenderValue;
    private final TextView tvIdValue;
    private final TextView tvAddress;
    private final TextView tvPhone;
    private final TextView tvEmail;
    public PersonalCardAdapter(@NonNull View root, @NonNull Actions actions){
        this.root = root;
        this.actions = actions;

        imgAvatar = root.findViewById(R.id.imgAvatar);
        btnEdit = root.findViewById(R.id.btnEditProfile);

        tvCustomerName = root.findViewById(R.id.tvCustomerName);
        tvCustomerUid = root.findViewById(R.id.tvCustomerUid);

        chipStatus = root.findViewById(R.id.chipStatus);
        chipSegment = root.findViewById(R.id.chipSegment);
        chipRisk = root.findViewById(R.id.chipRisk);

        tvDobValue = root.findViewById(R.id.tvDobValue);
        tvGenderValue = root.findViewById(R.id.tvGenderValue);
        tvIdValue = root.findViewById(R.id.tvIdValue);
        tvAddress = root.findViewById(R.id.tvAddressValue);
        tvPhone = root.findViewById(R.id.tvPhoneValue);
        tvEmail = root.findViewById(R.id.tvEmailValue2);
        root.findViewById(R.id.btnEditProfile).setOnClickListener(v -> actions.onEdit());
        root.findViewById(R.id.btnCall).setOnClickListener(v -> actions.onCall(getSafeText(tvPhone.getText().toString())));
        root.findViewById(R.id.btnSms).setOnClickListener(v -> actions.onSms(getSafeText(tvPhone.getText().toString())));
        root.findViewById(R.id.btnEmail).setOnClickListener(v -> actions.onEmail(getSafeText(tvEmail.getText().toString())));
    }
    public void bind(@NonNull PersonalCardUiState state){
        tvCustomerName.setText(state.fullName);
        tvCustomerUid.setText(state.uidText);

        chipStatus.setText(state.status);
        chipSegment.setText(state.segment);
        chipRisk.setText(state.risk);

        tvDobValue.setText(state.dobDisplay);
        tvGenderValue.setText(state.gender);
        tvIdValue.setText(state.idNumber);

        tvPhone.setText(state.phone);
        tvEmail.setText(state.email);
        tvAddress.setText(state.address);
        if (state.avatarUrl != null && !state.avatarUrl.trim().isEmpty()) {
            Bitmap bmp = DataUriUtil.decodeToBitmap(state.avatarUrl);
            if (bmp != null) {
                imgAvatar.setImageBitmap(bmp);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_user);
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_user);
        }
        applyChipStyle(chipStatus, state.status);
        applyRiskStyle(chipRisk, state.risk);
    }
    private void applyChipStyle(Chip chip, String status) {
        if (chip == null) return;
        String s = status == null ? "" : status.trim().toUpperCase();

        int bg, fg;

        switch (s) {
            case "ACTIVE":
                bg = Color.parseColor("#F7E7EA"); // đỏ nhạt
                fg = Color.parseColor("#C32248"); // đỏ đậm
                break;

            case "LOCKED":
                bg = Color.parseColor("#D1E7DD"); // xanh nhạt
                fg = Color.parseColor("#0F5132"); // xanh đậm
                break;

            case "CLOSED":
                bg = Color.parseColor("#E9ECEF"); // xám nhạt
                fg = Color.parseColor("#6C757D"); // xám đậm
                break;

            default:
                bg = Color.parseColor("#F1F3F5");
                fg = Color.parseColor("#495057");
                break;
        }

        chip.setChipBackgroundColor(ColorStateList.valueOf(bg));
        chip.setTextColor(fg);
        chip.setChecked(false);
        chip.setCheckable(false);
    }

    private void applyRiskStyle(Chip chip, String risk) {
        if (chip == null) return;
        String r = risk == null ? "" : risk.trim().toUpperCase();

        int bg, fg;

        switch (r) {
            case "LOW":
                bg = Color.parseColor("#D1E7DD"); // xanh nhạt
                fg = Color.parseColor("#0F5132"); // xanh đậm
                break;

            case "MEDIUM":
                bg = Color.parseColor("#FFF3CD"); // vàng nhạt
                fg = Color.parseColor("#FF8A00"); // vàng/cam đậm
                break;

            case "HIGH":
                bg = Color.parseColor("#F8D7DA"); // đỏ nhạt
                fg = Color.parseColor("#C32248"); // đỏ đậm
                break;

            default:
                bg = Color.parseColor("#F1F3F5");
                fg = Color.parseColor("#495057");
                break;
        }

        chip.setChipBackgroundColor(ColorStateList.valueOf(bg));
        chip.setTextColor(fg);
        chip.setChecked(false);
        chip.setCheckable(false);
    }

    private static String getSafeText(String s) {
        return s == null ? "" : s.trim();
    }
}
