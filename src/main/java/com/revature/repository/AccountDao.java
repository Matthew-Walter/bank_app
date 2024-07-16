package com.revature.repository;

import com.revature.entity.Account;
import com.revature.entity.User;

import java.util.List;

public interface AccountDao {

    Account createAccount(double balance, User user);
    List<Account> getAccounts(User user);
    Account getAccount(int id);
    void withdrawFromAccount(int id, double amount);
    void depositIntoAccount(int id, double amount);
    void deleteAccount(int id);
}
