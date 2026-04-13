package com.learning.employee.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String identifier) {
        super("Employee not found: " + identifier);
    }
}
