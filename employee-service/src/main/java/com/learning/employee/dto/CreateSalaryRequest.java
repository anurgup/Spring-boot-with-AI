package com.learning.employee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalaryRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
    private BigDecimal baseSalary;

    @NotNull(message = "Deductions are required")
    @DecimalMin(value = "0.0", message = "Deductions cannot be negative")
    private BigDecimal deductions;

    @NotNull(message = "Bonuses are required")
    @DecimalMin(value = "0.0", message = "Bonuses cannot be negative")
    private BigDecimal bonuses;

    @NotBlank(message = "Currency is required")
    private String currency;

    private String status;
}
