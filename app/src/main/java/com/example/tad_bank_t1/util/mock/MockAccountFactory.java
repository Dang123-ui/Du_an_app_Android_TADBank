package com.example.tad_bank_t1.util.mock;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.MortgageAccount;
import com.example.tad_bank_t1.data.model.SavingsAccount;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.example.tad_bank_t1.data.model.enums.mortgage.MortgagePaymentFrequency;
import com.example.tad_bank_t1.data.model.enums.saving.InterestPaymentMethod;
import com.example.tad_bank_t1.data.model.enums.saving.SavingCapitalization;
import com.example.tad_bank_t1.data.model.enums.saving.SavingPolicyStatus;
import com.example.tad_bank_t1.util.TransactionUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Tạo dữ liệu giả để test UI (không gọi DB). */
public final class MockAccountFactory {

    private MockAccountFactory() {}

    /** Giả lập 1 saving account để test màn detail. */
    public static Account createMockSavingAccount() {
        Date now = new Date();

        Account account = new Account();
        account.accountId = "ACC_SAVING_DEMO_01";
        account.userId = "USER_DEMO_01";
        account.accountName = "Sổ tiết kiệm demo";
        account.accountNumber = "STK-001-2024-0001";
        account.branchId = "BR_HCM_Q1";
        account.type = AccountType.SAVING;
        account.currency = "VND";
        account.balance = 150_000_000L; // số dư gốc
        account.status = AccountStatus.OPEN;
        account.createdAt = now;

        SavingsAccount saving = new SavingsAccount();
        saving.policyId = "POLICY_12M";
        // Nếu class SavingsAccount của bạn CÓ policyName thì mở comment:
        // saving.policyName = "Tiết kiệm 12 tháng - lãi cuối kỳ";

        saving.startDate = now;
        saving.maturityDate = addMonths(now, 12);

        saving.aprAtOpen = 6.5; // lãi suất chốt lúc mở (%/năm)
        saving.capitalization = SavingCapitalization.MONTHLY;
        saving.interestPaymentMethod = InterestPaymentMethod.AT_MATURITY;
        saving.payoutAccountId = "ACC_CHECKING_DEMO_01";

        account.saving = saving;
        return account;
    }

    private static Date addMonths(Date start, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static List<SavingsRatePolicy> createDefaultPolicies() {
        Date now = new Date();

        List<SavingsRatePolicy> list = new ArrayList<>();

        list.add(create("POLICY_01M", "Tiết kiệm 1 tháng", 1, 3.5, now));
        list.add(create("POLICY_03M", "Tiết kiệm 3 tháng", 3, 4.2, now));
        list.add(create("POLICY_06M", "Tiết kiệm 6 tháng", 6, 5.3, now));
        list.add(create("POLICY_12M", "Tiết kiệm 12 tháng", 12, 6.5, now));
        list.add(create("POLICY_24M", "Tiết kiệm 24 tháng", 24, 6.9, now));

        return list;
    }

    private static SavingsRatePolicy create(String id, String name, int months, double rate, Date now) {
        SavingsRatePolicy policy = new SavingsRatePolicy();
        policy.setSavingPolicyId(id);
        policy.setPolicyName(name);
        policy.setTermMonths(months);
        policy.setInterestRate(rate);
        policy.setStatus(SavingPolicyStatus.ACTIVE); // nếu enum bạn khác thì đổi lại
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);
//        policy.setCreatedBy("seed");
//        policy.setUpdateBy("seed");
        return policy;
    }

    /**
     * Creates a list of mock mortgage accounts.  The resulting list will
     * contain six accounts with varying principal amounts and start dates.
     *
     * @return a list of Accounts containing mortgage information
     */
    public static List<Account> createMockMortgageAccounts() {
        List<Account> accounts = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            accounts.add(createMockMortgageAccount(i));
        }
        return accounts;
    }

    /**
     * Creates a single mock mortgage account with a sequence index.  The
     * index is used to vary the principal and account number for demo
     * purposes.
     *
     * @param index a sequence number used to vary the account fields
     * @return an Account configured as a mortgage account
     */
    public static Account createMockMortgageAccount(int index) {
        Account account = new Account();
        // Basic account info
        account.setAccountId("MORT" + TransactionUtil.generateIdWithTime());
        account.setUserId("test-+84373436163");
        account.setPinCode("037343");
        account.setDefault(false);
        account.setAccountName("Mortgage Account " + index);
        account.setAccountNumber(String.format("MRG%03d%04d", index, index + 1000));
        account.setBranchId("BR01");
        account.setType(AccountType.MORTGAGE);
        account.setCurrency("VND");
        // Mortgage accounts typically have no liquid balance because the balance
        // refers to deposits/withdrawals on checking and saving accounts
        account.setBalance(0L);
        account.setStatus(AccountStatus.OPEN);
        Date now = new Date();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        // Create mortgage details
        MortgageAccount mortgage = new MortgageAccount();
        long basePrincipal = 300_000_000L; // 300 million VND
        mortgage.setPrincipalAmount(basePrincipal + index * 50_000_000L);
        mortgage.setInterestRateAnnual(9.5);
        mortgage.setTermMonths(240); // 20 years
        mortgage.setPaymentFrequency(MortgagePaymentFrequency.MONTHLY);
        // Start date shifts back by index months
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -index);
        mortgage.setStartDate(cal.getTime());
        // Next due date is one month after start date for demonstration
        cal.add(Calendar.MONTH, 1);
        mortgage.setNextDueDate(cal.getTime());
        mortgage.setOfficerId("officer-001");

        // Attach mortgage to account
        account.setMortgage(mortgage);
        return account;
    }
}
