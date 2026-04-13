package com.learning.employee.service;

import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateEmailException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.mapper.EmployeeMapper;
import com.learning.employee.model.Employee;
import com.learning.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeMapper employeeMapper;
    @Mock private MongoTemplate mongoTemplate;
    @InjectMocks private EmployeeService employeeService;

    @Test
    void createEmployee_Success() {
        CreateEmployeeRequest req = CreateEmployeeRequest.builder()
                .firstName("Anurag").lastName("Sharma")
                .email("a@b.com").status("active").build();
        Employee entity = Employee.builder().id("emp_1").email("a@b.com").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").email("a@b.com").build();

        when(employeeRepository.existsByEmail("a@b.com")).thenReturn(false);
        when(employeeMapper.toEntity(req)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(entity);
        when(employeeMapper.toResponse(entity)).thenReturn(response);

        EmployeeResponse result = employeeService.createEmployee(req);
        assertThat(result.getId()).isEqualTo("emp_1");
    }

    @Test
    void createEmployee_DuplicateEmail_ThrowsException() {
        CreateEmployeeRequest req = CreateEmployeeRequest.builder()
                .firstName("A").lastName("B").email("dup@b.com").build();
        when(employeeRepository.existsByEmail("dup@b.com")).thenReturn(true);
        assertThatThrownBy(() -> employeeService.createEmployee(req))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void getEmployees_NoDateFilter_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").department("Engineering").build();
        EmployeeListResponse listResponse = EmployeeListResponse.builder().id("emp_1").email("a@b.com").build();

        when(mongoTemplate.count(any(Query.class), eq(Employee.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toListResponse(employee)).thenReturn(listResponse);

        ApiResponse<List<EmployeeListResponse>> result = employeeService.getEmployees(
                1, 10, null, null, null, "createdAt", "desc", null, null);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getPagination().getTotal()).isEqualTo(1);
    }

    @Test
    void getEmployees_WithDateRange_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com")
                .joiningDate(LocalDate.of(2024, 6, 15)).build();
        EmployeeListResponse listResponse = EmployeeListResponse.builder().id("emp_1").email("a@b.com").build();

        when(mongoTemplate.count(any(Query.class), eq(Employee.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toListResponse(employee)).thenReturn(listResponse);

        ApiResponse<List<EmployeeListResponse>> result = employeeService.getEmployees(
                1, 10, null, null, null, "createdAt", "desc",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).hasSize(1);
    }

    @Test
    void getEmployees_WithOnlyJoinDateFrom_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com")
                .joiningDate(LocalDate.of(2024, 6, 15)).build();
        EmployeeListResponse listResponse = EmployeeListResponse.builder().id("emp_1").email("a@b.com").build();

        when(mongoTemplate.count(any(Query.class), eq(Employee.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toListResponse(employee)).thenReturn(listResponse);

        ApiResponse<List<EmployeeListResponse>> result = employeeService.getEmployees(
                1, 10, null, null, null, "createdAt", "desc",
                LocalDate.of(2024, 1, 1), null);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).hasSize(1);
    }

    @Test
    void getEmployees_WithOnlyJoinDateTo_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com")
                .joiningDate(LocalDate.of(2024, 6, 15)).build();
        EmployeeListResponse listResponse = EmployeeListResponse.builder().id("emp_1").email("a@b.com").build();

        when(mongoTemplate.count(any(Query.class), eq(Employee.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toListResponse(employee)).thenReturn(listResponse);

        ApiResponse<List<EmployeeListResponse>> result = employeeService.getEmployees(
                1, 10, null, null, null, "createdAt", "desc",
                null, LocalDate.of(2024, 12, 31));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).hasSize(1);
    }

    @Test
    void getEmployees_InvalidDateRange_ThrowsException() {
        assertThatThrownBy(() -> employeeService.getEmployees(
                1, 10, null, null, null, "createdAt", "desc",
                LocalDate.of(2024, 12, 31), LocalDate.of(2024, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("joinDateFrom must not be after joinDateTo");
    }

    @Test
    void updateEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.updateEmployee("unknown", new UpdateEmployeeRequest()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void updateEmployee_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").build();
        UpdateEmployeeRequest req = UpdateEmployeeRequest.builder()
                .designation("Lead").salary(BigDecimal.valueOf(90000)).build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").designation("Lead").build();

        when(employeeRepository.findById("emp_1")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        EmployeeResponse result = employeeService.updateEmployee("emp_1", req);
        assertThat(result.getDesignation()).isEqualTo("Lead");
    }

    @Test
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.deleteEmployee("unknown"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void deleteEmployee_Success() {
        Employee employee = Employee.builder().id("emp_1").build();
        when(employeeRepository.findById("emp_1")).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        DeleteResponse result = employeeService.deleteEmployee("emp_1");
        assertThat(result.getId()).isEqualTo("emp_1");
        assertThat(result.getDeletedAt()).isNotNull();
    }
}
