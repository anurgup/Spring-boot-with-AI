package com.learning.employee.service;

import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateSalaryException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.exception.SalaryNotFoundException;
import com.learning.employee.model.Employee;
import com.learning.employee.model.Salary;
import com.learning.employee.repository.EmployeeRepository;
import com.learning.employee.repository.SalaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private SalaryService salaryService;

    private Employee mockEmployee;
    private Salary mockSalary;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id("emp_xyz789")
                .firstName("Anurag")
                .lastName("Sharma")
                .email("anurag.sharma@company.com")
                .department("Engineering")
                .designation("Senior Developer")
                .status("active")
                .build();

        mockSalary = Salary.builder()
                .id("sal_001")
                .employeeId("emp_xyz789")
                .baseSalary(BigDecimal.valueOf(85000))
                .deductions(BigDecimal.valueOf(10000))
                .bonuses(BigDecimal.valueOf(5000))
                .netSalary(BigDecimal.valueOf(80000))
                .currency("INR")
                .status("active")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createSalary_Success() {
        CreateSalaryRequest request = CreateSalaryRequest.builder()
                .employeeId("emp_xyz789")
                .baseSalary(BigDecimal.valueOf(85000))
                .deductions(BigDecimal.valueOf(10000))
                .bonuses(BigDecimal.valueOf(5000))
                .currency("INR")
                .status("active")
                .build();

        when(employeeRepository.findById("emp_xyz789")).thenReturn(Optional.of(mockEmployee));
        when(salaryRepository.existsByEmployeeId("emp_xyz789")).thenReturn(false);
        when(salaryRepository.save(any(Salary.class))).thenReturn(mockSalary);

        SalaryResponse response = salaryService.createSalary(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmployeeId()).isEqualTo("emp_xyz789");
        assertThat(response.getBaseSalary()).isEqualByComparingTo(BigDecimal.valueOf(85000));
        assertThat(response.getNetSalary()).isEqualByComparingTo(BigDecimal.valueOf(80000));
        assertThat(response.getEmployeeFirstName()).isEqualTo("Anurag");
        verify(salaryRepository, times(1)).save(any(Salary.class));
    }

    @Test
    void createSalary_EmployeeNotFound() {
        CreateSalaryRequest request = CreateSalaryRequest.builder()
                .employeeId("unknown")
                .baseSalary(BigDecimal.valueOf(85000))
                .deductions(BigDecimal.valueOf(10000))
                .bonuses(BigDecimal.valueOf(5000))
                .currency("INR")
                .build();

        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.createSalary(request))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void createSalary_DuplicateSalary() {
        CreateSalaryRequest request = CreateSalaryRequest.builder()
                .employeeId("emp_xyz789")
                .baseSalary(BigDecimal.valueOf(85000))
                .deductions(BigDecimal.valueOf(10000))
                .bonuses(BigDecimal.valueOf(5000))
                .currency("INR")
                .build();

        when(employeeRepository.findById("emp_xyz789")).thenReturn(Optional.of(mockEmployee));
        when(salaryRepository.existsByEmployeeId("emp_xyz789")).thenReturn(true);

        assertThatThrownBy(() -> salaryService.createSalary(request))
                .isInstanceOf(DuplicateSalaryException.class);
    }

    @Test
    void getSalaryByEmployeeId_Success() {
        when(employeeRepository.findById("emp_xyz789")).thenReturn(Optional.of(mockEmployee));
        when(salaryRepository.findByEmployeeId("emp_xyz789")).thenReturn(Optional.of(mockSalary));

        SalaryResponse response = salaryService.getSalaryByEmployeeId("emp_xyz789");

        assertThat(response).isNotNull();
        assertThat(response.getEmployeeId()).isEqualTo("emp_xyz789");
        assertThat(response.getBaseSalary()).isEqualByComparingTo(BigDecimal.valueOf(85000));
        assertThat(response.getDeductions()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.getBonuses()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        assertThat(response.getNetSalary()).isEqualByComparingTo(BigDecimal.valueOf(80000));
        assertThat(response.getDepartment()).isEqualTo("Engineering");
    }

    @Test
    void getSalaryByEmployeeId_EmployeeNotFound() {
        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.getSalaryByEmployeeId("unknown"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getSalaryByEmployeeId_SalaryNotFound() {
        when(employeeRepository.findById("emp_xyz789")).thenReturn(Optional.of(mockEmployee));
        when(salaryRepository.findByEmployeeId("emp_xyz789")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.getSalaryByEmployeeId("emp_xyz789"))
                .isInstanceOf(SalaryNotFoundException.class);
    }
}
