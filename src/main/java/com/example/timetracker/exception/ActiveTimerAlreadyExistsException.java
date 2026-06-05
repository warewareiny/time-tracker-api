package com.example.timetracker.exception;

public class ActiveTimerAlreadyExistsException extends RuntimeException {
    public ActiveTimerAlreadyExistsException() {
        super("Active timer already exists");
    }
}