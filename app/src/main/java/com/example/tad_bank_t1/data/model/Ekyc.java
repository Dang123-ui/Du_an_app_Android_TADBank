package com.example.tad_bank_t1.data.model;

import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ekyc {
    public String id;
    private String userId;
    public boolean verified;
    public String nationalIdNumber;
    /**
     * Full legal name of the person as printed on the front of the ID card.
     */
    private String fullName;
    /**
     * Gender of the card holder. Typical values are "Nam" (male) or
     * "Nữ" (female) on Vietnamese national ID cards.
     */
    private String gender;
    /**
     * Date of birth of the card holder in ISO‑8601 format (yyyy‑MM‑dd).
     */
    private String dateOfBirth;
    /**
     * Date the ID card was issued in ISO‑8601 format (yyyy‑MM‑dd). This
     * information is printed on the back of the card.
     */
    private String dateOfIssue;
    /**
     * Place of issue for the ID card, typically the city/province in
     * Vietnamese (e.g. "TP Hồ Chí Minh").
     */
    private String placeOfIssue;
    /**
     * Permanent address of the card holder, taken from the front of the card.
     */
    private String address;
    public String faceImagePath;
    public String idFrontPath;
    public String idBackPath;
    public Timestamp verifiedAt;
    // Thêm các trường này vào cùng với các trường khác trong Ekyc.java
    public String idFrontUrl;
    public String idBackUrl;
    public String faceImageUrl;

    // Thêm các phương thức getter/setter này vào cuối file
    public String getIdFrontUrl() {
        return idFrontUrl;
    }

    public void setIdFrontUrl(String idFrontUrl) {
        this.idFrontUrl = idFrontUrl;
    }

    public String getIdBackUrl() {
        return idBackUrl;
    }

    public void setIdBackUrl(String idBackUrl) {
        this.idBackUrl = idBackUrl;
    }

    public String getFaceImageUrl() {
        return faceImageUrl;
    }

    public void setFaceImageUrl(String faceImageUrl) {
        this.faceImageUrl = faceImageUrl;
    }
    public Ekyc() {}
    public Ekyc(String id,
                String userId,
                boolean verified,
                String nationalIdNumber,
                String faceImagePath,
                String idFrontPath,
                String idBackPath,
                String verifiedBy,
                Timestamp verifiedAt,
                String fullName,
                String gender,
                String dateOfBirth,
                String dateOfIssue,
                String placeOfIssue,
                String address) {
        this.id = id;
        this.userId = userId;
        this.verified = verified;
        this.nationalIdNumber = nationalIdNumber;
        this.faceImagePath = faceImagePath;
        this.idFrontPath = idFrontPath;
        this.idBackPath = idBackPath;
        this.verifiedAt = verifiedAt;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dateOfIssue = dateOfIssue;
        this.placeOfIssue = placeOfIssue;
        this.address = address;
    }

    /**
     * Convenience constructor that leaves optional identity fields unspecified.
     */
    public Ekyc(String id,
                String userId,
                boolean verified,
                String nationalIdNumber,
                String faceImagePath,
                String idFrontPath,
                String idBackPath,
                String verifiedBy,
                Timestamp verifiedAt) {
        this(id, userId, verified, nationalIdNumber, faceImagePath, idFrontPath, idBackPath,
                verifiedBy, verifiedAt, null, null, null, null, null, null);
    }

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
    public String getIdFrontPath() {
        return idFrontPath;
    }
    public void setIdFrontPath(String idFrontPath) {
        this.idFrontPath = idFrontPath;
    }

    public String getIdBackPath() {
        return idBackPath;
    }

    public void setIdBackPath(String idBackPath) {
        this.idBackPath = idBackPath;
    }
    public void setVerifiedAt(Timestamp verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Timestamp getVerifiedAt() {
        return verifiedAt;
    }
    public boolean isFullyPopulated() {
        return nationalIdNumber != null && !nationalIdNumber.isEmpty() &&
                fullName != null && !fullName.isEmpty() &&
                dateOfBirth != null && !dateOfBirth.isEmpty() &&
                dateOfIssue != null && !dateOfIssue.isEmpty();
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("userId", userId);
        map.put("verified", verified);
        map.put("nationalIdNumber", nationalIdNumber);
        map.put("fullName", fullName);
        map.put("gender", gender);
        map.put("dateOfBirth", dateOfBirth);
        map.put("dateOfIssue", dateOfIssue);
        map.put("placeOfIssue", placeOfIssue);
        map.put("address", address);
        map.put("faceImagePath", faceImagePath);
        map.put("idFrontPath", idFrontPath);
        map.put("idBackPath", idBackPath);
        map.put("verifiedAt", verifiedAt);
        return map;
    }
}
