package com.mexcorgo.exception;

public class LeadAlreadyCompletedException extends RuntimeException{
    public LeadAlreadyCompletedException(String message) {
        super(message);
    }
}
