package com.example.timetracker.auth.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer id) {
        super("User with id " + id + " not found");
    }

}
