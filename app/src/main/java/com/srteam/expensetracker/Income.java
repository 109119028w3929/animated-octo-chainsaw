package com.srteam.expensetracker;

public class Income {
    private int id;
    private double amount;
    private String description;
    private String date;

    public Income(int id, double amount, String description, String date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Income(double amount, String description, String date) {
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Double setAmount(Double amount){
        this.amount = amount;
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String setDescription(String description){
        this.description = description;
        return description;
    }

    public String setDate(String date){
        this.date = date;
        return date;
    }
}
