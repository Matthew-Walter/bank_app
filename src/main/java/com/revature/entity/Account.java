package com.revature.entity;

public class Account {
    private double balance;
    private int id;
    private int user_id;

    public Account(double balance, int id, int user_id){
        this.balance = balance;
        this.id = id;
        this.user_id = user_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
