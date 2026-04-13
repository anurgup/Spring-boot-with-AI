package com.learning.employee.controller;

import com.learning.employee.dto.*;
import com.learning.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/by-department")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(
            @RequestParam(required = false) String department) {
        if (department == null || department.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALIDATION_ERROR", "Department parameter is required and cannot be blank"));
        }
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(ApiResponse.success(employees, "Employees retrieved successfully"));
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
