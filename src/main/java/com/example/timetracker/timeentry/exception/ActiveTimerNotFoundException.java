package com.example.timetracker.timeentry.exception;

public class ActiveTimerNotFoundException extends RuntimeException {
    public ActiveTimerNotFoundException() {
        super("Active timer not found");
    }
}