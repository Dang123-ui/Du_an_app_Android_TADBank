package com.example.tad_bank_t1.util;

public class TadConstants {
    // config rate khi đóng sổ trước hạn
    public static final double RATE_WHEN_CLOSE_SAVING_BEFORE_MATURITY = 0.05;

    // config OTP
    public static final long OTP_TTL_MS = 5 * 60 * 1000;

    // provider
    public static final String BILL_ELECTRICITY = "BILL_ELECTRICITY";
    public static final String BILL_WATER = "BILL_WATER";
    public static final String TOPUP = "TOPUP";


    // transaction
    public static final Long LIMIT_NEED_VERIFY_AMOUNT = 5_000_000L;
    public static final Long LIMIT_TRANSACTION_AMOUNT_ONE_DAY = 50_000_000L;
    public static final Long FEE_AMOUNT = 0L;


    // search
    public static final String MY_BANK = "TADBANK";
    public static final String SEARCH_BANK = "BANK";
    public static final String SEARCH_ACCOUNT = "ACCOUNT";
    public static final String SEARCH_BENEFICIARY_ACCOUNT = "BENEFICIARY_ACCOUNT";
    public static final String SEARCH_BENEFICIARY_PHONE = "BENEFICIARY_PHONE";
    public static final String SEARCH_ELECTRICITY_PROVIDER = "ELECTRICITY_PROVIDER";
    public static final String SEARCH_WATER_PROVIDER = "WATER_PROVIDER";
    public static final String SEARCH_TUITION_PROVIDER = "TUITION_PROVIDER";
}
