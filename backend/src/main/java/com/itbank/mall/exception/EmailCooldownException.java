package com.itbank.mall.exception;

public class EmailCooldownException extends RuntimeException {
    public EmailCooldownException(String message) { super(message); }
}
