package com.learning.employee.exception;

public class DuplicateSalaryException extends RuntimeException {
    public DuplicateSalaryException(String employeeId) {
        super("Salary record already exists for employee ID: " + employeeId);
    }
}
