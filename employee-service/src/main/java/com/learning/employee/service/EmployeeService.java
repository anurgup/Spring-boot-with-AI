package com.learning.employee.service;

import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateEmailException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.mapper.EmployeeMapper;
import com.learning.employee.model.Employee;
import com.learning.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MongoTemplate mongoTemplate;

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }
        Employee employee = employeeMapper.toEntity(request);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    public ApiResponse<List<EmployeeListResponse>> getEmployees(
            int page, int limit, String department, String status,
            String search, String sortBy, String sortOrder) {

        Query query = new Query();

        if (department != null && !department.isBlank()) {
            query.addCriteria(Criteria.where("department").is(department));
        }
        if (status != null && !status.isBlank()) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (search != null && !search.isBlank()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(search, "i"),
                    Criteria.where("lastName").regex(search, "i"),
                    Criteria.where("email").regex(search, "i")
            );
            query.addCriteria(searchCriteria);
        }

        long total = mongoTemplate.count(query, Employee.class);

        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sortBy != null ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortField));
        query.with(pageable);

        List<Employee> employees = mongoTemplate.find(query, Employee.class);
        List<EmployeeListResponse> data = employees.stream()
                .map(employeeMapper::toListResponse)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) total / limit);
        ApiResponse.PaginationMeta pagination = ApiResponse.PaginationMeta.builder()
                .total(total)
                .page(page)
                .limit(limit)
                .totalPages(totalPages)
                .hasNextPage(page < totalPages)
                .hasPrevPage(page > 1)
                .build();

        return ApiResponse.<List<EmployeeListResponse>>builder()
                .success(true)
                .data(data)
                .pagination(pagination)
                .build();
    }

    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employeeMapper.updateEntity(employee, request);
        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponse(updated);
    }

    public DeleteResponse deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employeeRepository.delete(employee);
        return DeleteResponse.builder()
                .id(id)
                .deletedAt(Instant.now())
                .build();
    }
}
