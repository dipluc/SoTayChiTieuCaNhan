package com.example.personalexpensemanager.models;

public class Budget {
    private int budgetId;
    private String monthYear;
    private long limitAmount;
    private int userId;

    public Budget(int budgetId, String monthYear, long limitAmount, int userId) {
        this.budgetId = budgetId;
        this.monthYear = monthYear;
        this.limitAmount = limitAmount;
        this.userId = userId;
    }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }
    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    public long getLimitAmount() { return limitAmount; }
    public void setLimitAmount(long limitAmount) { this.limitAmount = limitAmount; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}