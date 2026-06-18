package com.example.timetracker.timeentry.exception;

public class ActiveTimerAlreadyExistsException extends RuntimeException {
    public ActiveTimerAlreadyExistsException() {
        super("Active timer already exists");
    }
}