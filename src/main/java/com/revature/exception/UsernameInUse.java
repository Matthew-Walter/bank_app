package com.revature.exception;

public class UsernameInUse extends RuntimeException{
    public UsernameInUse(String message){
        super(message);
    }
}
