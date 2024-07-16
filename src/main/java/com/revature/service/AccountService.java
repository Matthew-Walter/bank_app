package com.revature.service;

import com.revature.entity.Account;
import com.revature.entity.User;
import com.revature.repository.AccountDao;
import com.revature.repository.SqliteAccountDao;

import java.util.HashMap;
import java.util.List;

public class AccountService {
    private AccountDao accountDao;

    public AccountService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    public Account createAccount(double balance, User user){
        return accountDao.createAccount(balance, user);
    }

    public List<Account> getAccountsByUser(User user){
        return accountDao.getAccounts(user);
    }

    public Account getAccount(int id){
        return accountDao.getAccount(id);
    }

    public void withdrawFromAccount(int id, double amount){
        accountDao.withdrawFromAccount(id, amount);
    }

    public void depositIntoAccount(int id, double amount){
        accountDao.depositIntoAccount(id, amount);
    }

    public void deleteAccount(int id){
        accountDao.deleteAccount(id);
    }
}
