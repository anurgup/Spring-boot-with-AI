package com.learning.employee.controller;

import com.learning.employee.dto.*;
import com.learning.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employee, "Employee created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeListResponse>>> getEmployees(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(employeeService.getEmployees(page, limit, department, status, search, sortBy, sortOrder));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> searchByEmployeeId(
            @RequestParam(required = false) String employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId parameter is required");
        }
        if (employeeId.isBlank()) {
            throw new IllegalArgumentException("employeeId must not be blank");
        }
        List<EmployeeResponse> results = employeeService.searchByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(results, "Search completed successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeResponse updated = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Employee updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteEmployee(@PathVariable String id) {
        DeleteResponse deleteResponse = employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(deleteResponse, "Employee deleted successfully"));
    }
}
