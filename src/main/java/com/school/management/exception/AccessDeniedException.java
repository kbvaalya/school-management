package com.school.management.exception;
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) { super(message); }
}
