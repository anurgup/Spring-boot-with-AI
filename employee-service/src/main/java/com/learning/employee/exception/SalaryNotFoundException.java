package com.learning.employee.exception;

public class SalaryNotFoundException extends RuntimeException {
    public SalaryNotFoundException(String employeeId) {
        super("Salary details not found for employee ID: " + employeeId);
    }
}
