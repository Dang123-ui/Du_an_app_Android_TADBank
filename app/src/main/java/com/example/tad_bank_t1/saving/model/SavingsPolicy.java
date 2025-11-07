package com.example.tad_bank_t1.saving.model;

public class SavingsPolicy {
    private String id;
    private String name;
    private int termMonths;
    private double interestRate;

    public SavingsPolicy(String id, String name, int termMonths, double interestRate) {
        this.id = id;
        this.name = name;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public double getInterestRate() {
        return interestRate;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}

