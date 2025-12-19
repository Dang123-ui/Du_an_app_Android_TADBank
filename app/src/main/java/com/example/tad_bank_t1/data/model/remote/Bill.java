package com.example.tad_bank_t1.data.model.remote;

public class Bill {
    public String billId;
    public String providerId;
    public Customer customer;
    public String period;
    public long amount;
    public String dueDate;
    public String status;

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", customer=" + customer +
                ", period='" + period + '\'' +
                ", amount=" + amount +
                ", dueDate='" + dueDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
