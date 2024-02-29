package com.edstem.taxibookingandbillingsystem.exception;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException() {
        super("Insufficient balance");
    }
}
