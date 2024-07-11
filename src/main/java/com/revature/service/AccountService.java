package com.revature.service;

import com.revature.entity.Account;
import com.revature.entity.User;
import com.revature.repository.AccountDao;
import com.revature.repository.SqliteAccountDao;

public class AccountService {
    private AccountDao accountDao;

    public AccountService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    public Account createAccount(double balance, User user){
        return accountDao.createAccount(balance, user);
    }

}
