package com.anil.blog.exceptions;

public class VerificationResendCooldownException extends RuntimeException {
    public VerificationResendCooldownException(String message) {
        super(message);
    }
}