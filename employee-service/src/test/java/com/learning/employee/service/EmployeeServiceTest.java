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
import java.util.Collections;
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

    @Test
    void searchByEmployeeId_ReturnsMatchingEmployees() {
        Employee employee = Employee.builder().id("emp_xyz789").firstName("Anurag").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_xyz789").firstName("Anurag").build();

        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        List<EmployeeResponse> results = employeeService.searchByEmployeeId("emp_xyz");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo("emp_xyz789");
    }

    @Test
    void searchByEmployeeId_NoMatches_ReturnsEmptyList() {
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(Collections.emptyList());

        List<EmployeeResponse> results = employeeService.searchByEmployeeId("nonexistent");
        assertThat(results).isEmpty();
    }
}
