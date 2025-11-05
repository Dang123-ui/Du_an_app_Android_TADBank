package com.example.tad_bank_t1.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ekyc implements Serializable {
    @DocumentId
    public String id;
    private String userId;
    public boolean verified;
    public String nationalIdNumber;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String dateOfIssue;
    private String placeOfIssue;
    private String address;
    public Date verifiedAt;
    public String faceImagePath;
    public Ekyc() {}

    public Ekyc(String id,
                String userId,
                boolean verified,
                String nationalIdNumber,
                String fullName,
                String gender,
                String dateOfBirth,
                String dateOfIssue,
                String placeOfIssue,
                String address,
                Date verifiedAt,
                String faceImagePath) {
        this.id = id;
        this.userId = userId;
        this.verified = verified;
        this.nationalIdNumber = nationalIdNumber;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dateOfIssue = dateOfIssue;
        this.placeOfIssue = placeOfIssue;
        this.address = address;
        this.verifiedAt = verifiedAt;
        this.faceImagePath = faceImagePath;
    }
    @Exclude
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setVerifiedAt(Date verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public String getNationalIdNumber() {
        return nationalIdNumber;
    }
    public void setNationalIdNumber(String nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }
    /**
     * Returns the full legal name of the customer.
     */
    public String getFullName() {
        return fullName;
    }
    /**
     * Sets the customer's full legal name as extracted from the front of
     * the ID card.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    /**
     * Returns the gender of the customer ("Nam" or "Nữ").
     */
    public String getGender() {
        return gender;
    }
    /**
     * Sets the gender of the customer.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    /**
     * Gets the customer's date of birth as a string (yyyy‑MM‑dd).
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    /**
     * Sets the date of birth of the customer.
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    /**
     * Gets the date the ID was issued (yyyy‑MM‑dd).
     */
    public String getDateOfIssue() {
        return dateOfIssue;
    }
    /**
     * Sets the date the ID was issued.
     */
    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }
    /**
     * Gets the place where the ID was issued.
     */
    public String getPlaceOfIssue() {
        return placeOfIssue;
    }
    /**
     * Sets the place where the ID was issued.
     */
    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }
    /**
     * Gets the permanent address of the customer.
     */
    public String getAddress() {
        return address;
    }
    /**
     * Sets the permanent address of the customer.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    public String getFaceImagePath() {
        return faceImagePath;
    }
    public void setFaceImagePath(String faceImagePath) {
        this.faceImagePath = faceImagePath;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("verified", verified);
        map.put("nationalIdNumber", nationalIdNumber);
        map.put("fullName", fullName);
        map.put("gender", gender);
        map.put("dateOfBirth", dateOfBirth);
        map.put("dateOfIssue", dateOfIssue);
        map.put("placeOfIssue", placeOfIssue);
        map.put("address", address);
        map.put("verifiedAt", verifiedAt);
        map.put("faceImagePath", faceImagePath);
        return map;
    }
}