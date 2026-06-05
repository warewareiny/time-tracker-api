package com.example.timetracker.exception;

public class ActiveTimerNotFoundException extends RuntimeException {
    public ActiveTimerNotFoundException() {
        super("Active timer not found");
    }
}