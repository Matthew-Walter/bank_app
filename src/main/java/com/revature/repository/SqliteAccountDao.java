package com.revature.repository;

import com.revature.entity.Account;
import com.revature.entity.User;
import com.revature.exception.LoginFail;
import com.revature.exception.UserSQLException;
import com.revature.utility.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteAccountDao implements AccountDao {
    @Override
    public Account createAccount(double balance, User user) {
        String sql = "insert into accounts (balance, user_id) values (?, ?)";
        try (Connection connection = DatabaseConnector.createConnection()) {
            // we can use a PreparedStatement to control how the user data is injected
            // into our query. the PreparedStatement helps to format the data so to help
            // protect us from SQL injection (someone trying to mess with our database
            // via the data they provide)
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // remember that indexing for Java sql resources starts at 1, not 0
            preparedStatement.setDouble(1, balance);
            preparedStatement.setInt(2, user.getId());


            // executeUpdate returns the row count affected, since we want a single
            // record created we can check that the rowCount value is 1 to know if we
            // have success or not
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();

                int newId = resultSet.getInt(1);
                int newUserId = resultSet.getInt(2);
                double newBalance = resultSet.getDouble(3);
                System.out.println(newBalance);
                System.out.println(newId);
                System.out.println(newUserId);
                return new Account(newBalance, newId, newUserId);
            }
            // if we did not create the new user we throw a custom exception and handle
            // the problem somewhere else
            throw new UserSQLException("User could not be created: please try again");
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }
}