package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.annotation.Nullable;

import java.security.Timestamp;

public class PersonalCardUiState {
    public final String userId;
    public final String fullName;
    public final String uidText;


    public final String status;   // Active/Locked...
    public final String segment;  // Mass/Priority...
    public final String risk;     // Low/Medium/High...

    public final String phone;
    public final String email;
    public final String dobDisplay;

    public final String gender;
    public final String idNumber;

    @Nullable public final String avatarUrl;
    public final String address;

    public PersonalCardUiState(
            String userId,
            String fullName,
            String uidText,
            String status,
            String segment,
            String risk,
            String phone,
            String email,
            String dobDisplay,
            String gender,
            String idNumber,
            @Nullable String avatarUrl,
            String address
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.uidText = uidText;
        this.status = status;
        this.segment = segment;
        this.risk = risk;
        this.phone = phone;
        this.email = email;
        this.dobDisplay = dobDisplay;
        this.gender = gender;
        this.idNumber = idNumber;
        this.avatarUrl = avatarUrl;
        this.address = address;
    }
}
