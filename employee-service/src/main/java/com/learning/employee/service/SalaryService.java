package com.learning.employee.service;

import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateSalaryException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.exception.SalaryNotFoundException;
import com.learning.employee.model.Employee;
import com.learning.employee.model.Salary;
import com.learning.employee.repository.EmployeeRepository;
import com.learning.employee.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final MongoTemplate mongoTemplate;

    public SalaryResponse createSalary(CreateSalaryRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        if (salaryRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateSalaryException(request.getEmployeeId());
        }

        BigDecimal netSalary = request.getBaseSalary()
                .subtract(request.getDeductions())
                .add(request.getBonuses());

        Salary salary = Salary.builder()
                .employeeId(request.getEmployeeId())
                .baseSalary(request.getBaseSalary())
                .deductions(request.getDeductions())
                .bonuses(request.getBonuses())
                .netSalary(netSalary)
                .currency(request.getCurrency())
                .status(request.getStatus() != null ? request.getStatus() : "active")
                .build();

        Salary saved = salaryRepository.save(salary);
        return toSalaryResponse(saved, employee);
    }

    public SalaryResponse getSalaryByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Salary salary = salaryRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new SalaryNotFoundException(employeeId));

        return toSalaryResponse(salary, employee);
    }

    public ApiResponse<List<SalaryListResponse>> getAllSalaries(
            int page, int limit, String department, String status,
            String sortBy, String sortOrder) {

        Query salaryQuery = new Query();
        if (status != null && !status.isBlank()) {
            salaryQuery.addCriteria(Criteria.where("status").is(status));
        }

        String sortField = (sortBy != null && !sortBy.isBlank()) ? sortBy : "netSalary";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortField));
        salaryQuery.with(pageable);

        long total = mongoTemplate.count(new Query(), Salary.class);
        if (status != null && !status.isBlank()) {
            Query countQuery = new Query();
            countQuery.addCriteria(Criteria.where("status").is(status));
            total = mongoTemplate.count(countQuery, Salary.class);
        }

        List<Salary> salaries = mongoTemplate.find(salaryQuery, Salary.class);

        List<SalaryListResponse> data = salaries.stream().map(salary -> {
            Employee employee = employeeRepository.findById(salary.getEmployeeId()).orElse(null);
            return toSalaryListResponse(salary, employee, department);
        }).filter(r -> {
            if (department != null && !department.isBlank()) {
                return department.equals(r.getDepartment());
            }
            return true;
        }).collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) total / limit);
        ApiResponse.PaginationMeta pagination = ApiResponse.PaginationMeta.builder()
                .total(total)
                .page(page)
                .limit(limit)
                .totalPages(totalPages)
                .hasNextPage(page < totalPages)
                .hasPrevPage(page > 1)
                .build();

        return ApiResponse.<List<SalaryListResponse>>builder()
                .success(true)
                .data(data)
                .pagination(pagination)
                .build();
    }

    private SalaryResponse toSalaryResponse(Salary salary, Employee employee) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployeeId())
                .employeeFirstName(employee != null ? employee.getFirstName() : null)
                .employeeLastName(employee != null ? employee.getLastName() : null)
                .employeeEmail(employee != null ? employee.getEmail() : null)
                .department(employee != null ? employee.getDepartment() : null)
                .designation(employee != null ? employee.getDesignation() : null)
                .baseSalary(salary.getBaseSalary())
                .deductions(salary.getDeductions())
                .bonuses(salary.getBonuses())
                .netSalary(salary.getNetSalary())
                .currency(salary.getCurrency())
                .status(salary.getStatus())
                .createdAt(salary.getCreatedAt())
                .updatedAt(salary.getUpdatedAt())
                .build();
    }

    private SalaryListResponse toSalaryListResponse(Salary salary, Employee employee, String departmentFilter) {
        String dept = employee != null ? employee.getDepartment() : null;
        return SalaryListResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployeeId())
                .employeeFirstName(employee != null ? employee.getFirstName() : null)
                .employeeLastName(employee != null ? employee.getLastName() : null)
                .department(dept)
                .baseSalary(salary.getBaseSalary())
                .deductions(salary.getDeductions())
                .bonuses(salary.getBonuses())
                .netSalary(salary.getNetSalary())
                .currency(salary.getCurrency())
                .status(salary.getStatus())
                .createdAt(salary.getCreatedAt())
                .build();
    }
}
