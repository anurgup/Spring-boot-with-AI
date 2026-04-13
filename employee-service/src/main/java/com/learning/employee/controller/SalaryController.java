package com.learning.employee.controller;

import com.learning.employee.dto.*;
import com.learning.employee.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<SalaryResponse>> createSalary(
            @Valid @RequestBody CreateSalaryRequest request) {
        SalaryResponse salary = salaryService.createSalary(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(salary, "Salary record created successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<SalaryResponse>> getSalaryByEmployeeId(
            @PathVariable String employeeId) {
        SalaryResponse salary = salaryService.getSalaryByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(salary, "Salary details retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalaryListResponse>>> getAllSalaries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "netSalary") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(
                salaryService.getAllSalaries(page, limit, department, status, sortBy, sortOrder));
    }
}
