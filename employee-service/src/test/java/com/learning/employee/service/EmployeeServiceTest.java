package com.learning.employee.service;

import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateEmailException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.mapper.EmployeeMapper;
import com.learning.employee.model.Address;
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
    void createEmployee_WithGender_Success() {
        CreateEmployeeRequest req = CreateEmployeeRequest.builder()
                .firstName("Anurag").lastName("Sharma")
                .email("a@b.com").status("active").gender("Male").build();
        Employee entity = Employee.builder().id("emp_1").email("a@b.com").gender("Male").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").email("a@b.com").gender("Male").build();

        when(employeeRepository.existsByEmail("a@b.com")).thenReturn(false);
        when(employeeMapper.toEntity(req)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(entity);
        when(employeeMapper.toResponse(entity)).thenReturn(response);

        EmployeeResponse result = employeeService.createEmployee(req);
        assertThat(result.getGender()).isEqualTo("Male");
    }

    @Test
    void createEmployee_WithoutGender_Success() {
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
        assertThat(result.getGender()).isNull();
    }

    @Test
    void createEmployee_WithFreeTextGender_Success() {
        CreateEmployeeRequest req = CreateEmployeeRequest.builder()
                .firstName("Alex").lastName("Smith")
                .email("alex@b.com").status("active").gender("Prefer not to say").build();
        Employee entity = Employee.builder().id("emp_2").email("alex@b.com").gender("Prefer not to say").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_2").email("alex@b.com").gender("Prefer not to say").build();

        when(employeeRepository.existsByEmail("alex@b.com")).thenReturn(false);
        when(employeeMapper.toEntity(req)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(entity);
        when(employeeMapper.toResponse(entity)).thenReturn(response);

        EmployeeResponse result = employeeService.createEmployee(req);
        assertThat(result.getGender()).isEqualTo("Prefer not to say");
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
    void updateEmployee_WithGender_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").build();
        UpdateEmployeeRequest req = UpdateEmployeeRequest.builder()
                .gender("Non-binary").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").gender("Non-binary").build();

        when(employeeRepository.findById("emp_1")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        EmployeeResponse result = employeeService.updateEmployee("emp_1", req);
        assertThat(result.getGender()).isEqualTo("Non-binary");
    }

    @Test
    void updateEmployee_GenderNotOverwrittenWhenNull() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").gender("Female").build();
        UpdateEmployeeRequest req = UpdateEmployeeRequest.builder()
                .designation("Senior Engineer").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").designation("Senior Engineer").gender("Female").build();

        when(employeeRepository.findById("emp_1")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        EmployeeResponse result = employeeService.updateEmployee("emp_1", req);
        assertThat(result.getGender()).isEqualTo("Female");
    }

    @Test
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.deleteEmployee("unknown"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void deleteEmployee_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").build();
        when(employeeRepository.findById("emp_1")).thenReturn(Optional.of(employee));
        DeleteResponse result = employeeService.deleteEmployee("emp_1");
        assertThat(result.getId()).isEqualTo("emp_1");
        verify(employeeRepository).delete(employee);
    }

    @Test
    void getEmployeeById_Success() {
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").email("a@b.com").build();
        when(employeeRepository.findById("emp_1")).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(response);
        EmployeeResponse result = employeeService.getEmployeeById("emp_1");
        assertThat(result.getId()).isEqualTo("emp_1");
    }

    @Test
    void getEmployeeById_NotFound_ThrowsException() {
        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.getEmployeeById("unknown"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getEmployeesByLocation_Success() {
        Address address = Address.builder().city("Delhi").state("Delhi").country("India").build();
        Employee employee = Employee.builder().id("emp_1").email("a@b.com").address(address).build();
        EmployeeListResponse listResponse = EmployeeListResponse.builder().id("emp_1").email("a@b.com").build();

        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toListResponse(employee)).thenReturn(listResponse);

        List<EmployeeListResponse> results = employeeService.getEmployeesByLocation("Delhi");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo("emp_1");
    }

    @Test
    void getEmployeesByLocation_NoMatch_ReturnsEmptyList() {
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(Collections.emptyList());
        List<EmployeeListResponse> results = employeeService.getEmployeesByLocation("UnknownCity");
        assertThat(results).isEmpty();
    }

    @Test
    void getEmployeesByLocation_NullLocation_ThrowsIllegalArgument() {
        assertThatThrownBy(() -> employeeService.getEmployeesByLocation(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("location must not be blank");
    }

    @Test
    void getEmployeesByLocation_BlankLocation_ThrowsIllegalArgument() {
        assertThatThrownBy(() -> employeeService.getEmployeesByLocation("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("location must not be blank");
    }

    @Test
    void searchByPartialEmployeeId_Success() {
        Employee employee = Employee.builder().id("emp_1").build();
        EmployeeResponse response = EmployeeResponse.builder().id("emp_1").build();
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(List.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(response);
        List<EmployeeResponse> results = employeeService.searchByPartialEmployeeId("EMP");
        assertThat(results).hasSize(1);
    }

    @Test
    void searchByPartialEmployeeId_NoMatch_ThrowsEmployeeNotFoundException() {
        when(mongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> employeeService.searchByPartialEmployeeId("NOMATCH"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void searchByPartialEmployeeId_BlankId_ThrowsIllegalArgument() {
        assertThatThrownBy(() -> employeeService.searchByPartialEmployeeId("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
