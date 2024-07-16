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
                return new Account(balance, newId, user.getId());
            }
            // if we did not create the new user we throw a custom exception and handle
            // the problem somewhere else
            throw new UserSQLException("User could not be created: please try again");
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }

    public List<Account> getAccounts(User user){
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection connection = DatabaseConnector.createConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while(resultSet.next()){
                Account accountRecord = new Account();
                accountRecord.setBalance(resultSet.getDouble("balance"));
                accountRecord.setId(resultSet.getInt("id"));
                accountRecord.setUser_id(resultSet.getInt("user_id"));
                accounts.add(accountRecord);
            }
            return accounts;
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }

    public Account getAccount(int id){
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection connection = DatabaseConnector.createConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Account account = new Account();
            account.setBalance(resultSet.getDouble("balance"));
            account.setId(resultSet.getInt("id"));
            account.setUser_id(resultSet.getInt("user_id"));
            return account;
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }

    public void withdrawFromAccount(int id, double amount){
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        try (Connection connection = DatabaseConnector.createConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }

    public void depositIntoAccount(int id, double amount){
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (Connection connection = DatabaseConnector.createConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }

    public void deleteAccount(int id){
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection connection = DatabaseConnector.createConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UserSQLException(exception.getMessage());
        }
    }
}