package com.learning.employee.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Employee with this email already exists");
    }
}
