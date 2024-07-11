package com.revature.repository;

import com.revature.entity.Account;
import com.revature.entity.User;

public interface AccountDao {

    Account createAccount(double balance, User user);
}
