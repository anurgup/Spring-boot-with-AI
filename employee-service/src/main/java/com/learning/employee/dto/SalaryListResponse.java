package com.learning.employee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryListResponse {
    private String id;
    private String employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String department;
    private BigDecimal baseSalary;
    private BigDecimal deductions;
    private BigDecimal bonuses;
    private BigDecimal netSalary;
    private String currency;
    private String status;
    private Instant createdAt;
}
