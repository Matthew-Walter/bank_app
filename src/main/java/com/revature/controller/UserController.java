package com.revature.controller;

import com.revature.entity.Account;
import com.revature.exception.LoginFail;
import com.revature.exception.UsernameInUse;
import com.revature.service.AccountService;
import com.revature.service.UserService;
import com.revature.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserController {

    private Scanner scanner;
    private UserService userService;
    private AccountService accountService;

    /*
        The controller takes in a scanner and service object
            - scanner is defined outside the class and passed in, so we can
              control closing the scanner when we are done in an easier way
              (try with resources)
            - userService gives the controller access to the service layer, which will
              handle enforcing our business and software requirements
     */
    public UserController(Scanner scanner, UserService userService, AccountService accountService){
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }

    /*
        this promptUserForService method is our entrypoint to the bank application:
        it currently contains code to handle registering an account or exiting the
        app, but this can be refactored to handle more options (like logging). It
        also can be refactored to call helper methods to control the flow of the application
        in a more organized and readable way
     */
    public void promptUserForService(Map<String,String> controlMap){
        // user needs to prompt they want to make an account
        System.out.println("What would you like to do?");
        System.out.println("1. register an account");
        System.out.println("2. login");
        System.out.println("q. quit");
        try{
            String userActionIndicated = scanner.nextLine();
            switch (userActionIndicated) {
                case "1":
                    registerNewUser();
                    break;
                case "2":
                    // If the user provides correct credentials their username is saved in the controlMap
                    // to be used in the main method for facilitating getting the correct bank account information
                    controlMap.put("User", login().getUsername());
                    break;
                case "q":
                    System.out.println("Goodbye!");
                    // set the loopApplication boolean to false to exit the while loop in the main method
                    controlMap.put("Continue Loop", "false");
                    controlMap.remove("User");
            }
            // this exception triggers if the user enters invalid credentials
        } catch(LoginFail exception){
            System.out.println(exception.getMessage());
        }
    }

    public void promptUserAfterLogin(Map<String,String> controlMap){
        System.out.println("What would you like to do?");
        System.out.println("1. create a checking account");
        System.out.println("2. view your accounts");
        System.out.println("3. withdraw/deposit from an account");
        System.out.println("4. delete an account");
        System.out.println("5. logout");
        System.out.println("q. quit");

        try{
            String userActionIndicated = scanner.nextLine();
            switch (userActionIndicated) {
                case "1":
                    createNewAccount(controlMap);
                    break;
                case "2":
                    handleGetAccounts(userService.getSpecificUser(controlMap.get("User")));
                    break;
                case "3":
                    handleWithdrawOrDeposit(userService.getSpecificUser(controlMap.get("User")));
                    break;
                case "4":
                    handleDeleteAccount(userService.getSpecificUser(controlMap.get("User")));
                    break;
                case "5":
                    System.out.println("Got it, logging you out...");
                    controlMap.remove("User");
                    System.out.println("Done!");
                    System.out.println();
                    break;
                case "q":
                    System.out.println("Goodbye!");
                    // set the loopApplication boolean to false to exit the while loop in the main method
                    controlMap.put("Continue Loop", "false");
                    controlMap.remove("User");
            }
            // this exception triggers if the user enters invalid credentials
        } catch(LoginFail exception){
            System.out.println(exception.getMessage());
        }
    }

    public void registerNewUser(){
        // this either returns details on the new account or returns a failure message
        // TODO: generic runtime exception is thrown, make it more specific
        User newCredentials = getUserCredentials();
        User newUser = new User();
        try{
            newUser = userService.validateNewCredentials(newCredentials);
        }catch(UsernameInUse exception){
            newUser = null;
        }
        if (newUser != null){
            System.out.printf("New account created: %s", newUser);
        }
    }

    public User login(){
        // we can re-use getUserCredentials() here to avoid rewriting the same logic
        return userService.checkLoginCredentials(getUserCredentials());
    }

    public User getUserCredentials(){
        String newUsername;
        String newPassword;
        System.out.print("Please enter a username: ");
        newUsername = scanner.nextLine();
        System.out.print("Please enter a password: ");
        newPassword = scanner.nextLine();
        return new User(newUsername, newPassword,0);
    }

    public void createNewAccount(Map<String,String> controlMap) {
        System.out.println("How much is your starting balance?");
        double startingBalance = scanner.nextDouble();
        System.out.printf("Got it. Creating a checking account with %s$...\n", startingBalance);
        accountService.createAccount(startingBalance, userService.getSpecificUser(controlMap.get("User")));
        System.out.println("Done.");
        System.out.println("Enter any key to continue");
        scanner.nextLine();
        scanner.nextLine();
    }

    public void handleGetAccounts(User user){
        System.out.println("Sure, grabbing your accounts...");
        List<Account> accounts = accountService.getAccountsByUser(user);
        System.out.printf("Done. Here are the accounts registered to user %s:\n", user.getUsername());
        for(Account account:accounts){
            System.out.printf("id: %s | balance: %s | user id: %s\n", account.getId(), account.getBalance(), account.getUser_id());
        }
        System.out.println("Enter any key to continue.");
        scanner.nextLine();
    }

    public void handleWithdrawOrDeposit(User user){
        System.out.println("Which would you like to do?");
        System.out.println("1. withdraw");
        System.out.println("2. deposit");
        try{
            String userActionIndicated = scanner.nextLine();
            switch (userActionIndicated) {
                case "1":
                    handleWithraw(user);
                    break;
                case "2":
                    handleDeposit(user);
                    break;
            }
            // this exception triggers if the user enters invalid credentials
        } catch(LoginFail exception){
            System.out.println(exception.getMessage());
        }

    }

    public void handleWithraw(User user){
        System.out.println("Which account would you like to withdraw from?");
        List<Account> accounts = accountService.getAccountsByUser(user);

        for(Account account:accounts){
            System.out.printf("id: %s | balance: %s\n", account.getId(), account.getBalance());
        }

        int account_id = scanner.nextInt();
        Account accountToWithdraw = new Account();
        boolean validAccount = false;

        for(Account account:accounts){
            if(account_id == account.getId()){
                validAccount = true;
                accountToWithdraw.setUser_id(account.getUser_id());
                accountToWithdraw.setId(account.getId());
                accountToWithdraw.setBalance(account.getBalance());
            }
        }

        if(validAccount == true){
            boolean amountValid = false;
            while(amountValid == false){
                System.out.println("How much would you like to withdraw?");
                double amount = scanner.nextDouble();
                if(amount <= accountToWithdraw.getBalance() && amount >= 0.0){
                    amountValid = true;
                    System.out.printf("Got it. Withdrawing %s from account %s...\n", amount, account_id);
                    accountService.withdrawFromAccount(account_id, amount);
                    System.out.println("Done.");
                }
                else{
                    System.out.println("amount not valid, try again.");
                }
            }
        } else System.out.println("Invalid account id.");
        System.out.println("Enter any key to continue");
        scanner.nextLine();
        scanner.nextLine();
    }

    public void handleDeposit(User user){
        System.out.println("Which account would you like to deposit into?");
        List<Account> accounts = accountService.getAccountsByUser(user);

        for(Account account:accounts){
            System.out.printf("id: %s | balance: %s\n", account.getId(), account.getBalance());
        }

        int account_id = scanner.nextInt();
        Account accountToDeposit = new Account();
        boolean validAccount = false;

        for(Account account:accounts){
            if(account_id == account.getId()){
                validAccount = true;
                accountToDeposit.setUser_id(account.getUser_id());
                accountToDeposit.setId(account.getId());
                accountToDeposit.setBalance(account.getBalance());
            }
        }

        if(validAccount == true){
            boolean amountValid = false;
            while(amountValid == false) {
                System.out.println("How much would you like to deposit?");
                double amount = scanner.nextDouble();
                if (amount > 0.0) {
                    amountValid = true;
                    System.out.printf("Got it. Depositing %s into account %s...\n", amount, account_id);
                    accountService.depositIntoAccount(account_id, amount);
                    System.out.println("Done.");
                } else {
                    System.out.println("amount not valid, try again.");
                }
            }
        }else System.out.println("Invalid account id.");
        System.out.println("Enter any key to continue");
        scanner.nextLine();
        scanner.nextLine();
    }

    public void handleDeleteAccount(User user){
        System.out.println("Which account would you like to delete?");
        List<Account> accounts = accountService.getAccountsByUser(user);

        for(Account account:accounts){
            System.out.printf("id: %s | balance: %s\n", account.getId(), account.getBalance());
        }

        int account_id = scanner.nextInt();
        Account accountToDelete = new Account();
        boolean validAccount = false;

        for(Account account:accounts){
            if(account_id == account.getId()){
                validAccount = true;
                accountToDelete.setUser_id(account.getUser_id());
                accountToDelete.setId(account.getId());
                accountToDelete.setBalance(account.getBalance());
            }
        }

        if(validAccount == true){
            System.out.printf("Got it. Deleting account %s...\n", account_id);
            accountService.deleteAccount(account_id);
            System.out.println("Done.");
        }else System.out.println("Invalid account id");
        System.out.println("Enter any key to continue");
        scanner.nextLine();
        scanner.nextLine();
    }
}