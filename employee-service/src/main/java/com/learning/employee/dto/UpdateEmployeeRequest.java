package com.learning.employee.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest {
    private String designation;
    private BigDecimal salary;
    private String department;
    private String phone;
    private String gender;

    @Valid
    private AddressDto address;

    @Pattern(regexp = "^(active|inactive|terminated)$",
             message = "Invalid status value. Allowed: active, inactive, terminated")
    private String status;
}
