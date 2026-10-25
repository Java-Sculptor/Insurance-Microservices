package com.ampta.premiumservice.exception;

public class DuplicatePaymentException extends RuntimeException{
    public DuplicatePaymentException(String message){
        super(message);
    }
}
