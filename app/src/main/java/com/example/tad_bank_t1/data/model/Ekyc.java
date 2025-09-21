package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class Ekyc {
    public String id;                  // PK
    public boolean verified;
    public String nationalIdNumber;
    public String faceImagePath;
    public String idFrontPath;
    public String idBackPath;
    public String verifiedBy;          // userId OFFICER
    public Date verifiedAt;
    public Ekyc() {}
}
