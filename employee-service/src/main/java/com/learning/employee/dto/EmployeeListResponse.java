package com.learning.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String designation;
    private String status;
    private Instant createdAt;
}
