package com.learning.employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String phone;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private BigDecimal salary;
    private String managerId;
    private Address address;
    private String status;
    private String gender;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
