package com.learning.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateSalaryException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.exception.GlobalExceptionHandler;
import com.learning.employee.exception.SalaryNotFoundException;
import com.learning.employee.service.SalaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SalaryControllerTest {

    @Mock
    private SalaryService salaryService;

    @InjectMocks
    private SalaryController salaryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(salaryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private CreateSalaryRequest buildCreateRequest() {
        return CreateSalaryRequest.builder()
                .employeeId("emp_xyz789")
                .baseSalary(BigDecimal.valueOf(85000))
                .deductions(BigDecimal.valueOf(10000))
                .bonuses(BigDecimal.valueOf(5000))
                .currency("INR")
                .status("active")
                .build();
    }

    private SalaryResponse buildSalaryResponse() {
        return SalaryResponse.builder()
                .id("sal_001")
                .employeeId("emp_xyz789")
                .employeeFirstName("Anurag")
                .employeeLastName("Sharma")
                .employeeEmail("anurag.sharma@company.com")
                .department("Engineering")
                .designation("Senior Developer")
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
    void createSalary_Success() throws Exception {
        when(salaryService.createSalary(any())).thenReturn(buildSalaryResponse());
        mockMvc.perform(post("/api/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Salary record created successfully"))
                .andExpect(jsonPath("$.data.employeeId").value("emp_xyz789"))
                .andExpect(jsonPath("$.data.netSalary").value(80000));
    }

    @Test
    void createSalary_ValidationError() throws Exception {
        CreateSalaryRequest invalid = CreateSalaryRequest.builder().build();
        mockMvc.perform(post("/api/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createSalary_EmployeeNotFound() throws Exception {
        when(salaryService.createSalary(any())).thenThrow(new EmployeeNotFoundException("emp_xyz789"));
        mockMvc.perform(post("/api/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void createSalary_DuplicateSalary() throws Exception {
        when(salaryService.createSalary(any())).thenThrow(new DuplicateSalaryException("emp_xyz789"));
        mockMvc.perform(post("/api/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_ERROR"));
    }

    @Test
    void getSalaryByEmployeeId_Success() throws Exception {
        when(salaryService.getSalaryByEmployeeId("emp_xyz789")).thenReturn(buildSalaryResponse());
        mockMvc.perform(get("/api/salary/employee/emp_xyz789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Salary details retrieved successfully"))
                .andExpect(jsonPath("$.data.employeeId").value("emp_xyz789"))
                .andExpect(jsonPath("$.data.baseSalary").value(85000))
                .andExpect(jsonPath("$.data.deductions").value(10000))
                .andExpect(jsonPath("$.data.bonuses").value(5000))
                .andExpect(jsonPath("$.data.netSalary").value(80000));
    }

    @Test
    void getSalaryByEmployeeId_EmployeeNotFound() throws Exception {
        when(salaryService.getSalaryByEmployeeId("unknown")).thenThrow(new EmployeeNotFoundException("unknown"));
        mockMvc.perform(get("/api/salary/employee/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void getSalaryByEmployeeId_SalaryNotFound() throws Exception {
        when(salaryService.getSalaryByEmployeeId("emp_xyz789")).thenThrow(new SalaryNotFoundException("emp_xyz789"));
        mockMvc.perform(get("/api/salary/employee/emp_xyz789"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void getAllSalaries_Success() throws Exception {
        List<SalaryListResponse> list = List.of(
                SalaryListResponse.builder()
                        .id("sal_001").employeeId("emp_xyz789")
                        .employeeFirstName("Anurag").employeeLastName("Sharma")
                        .department("Engineering")
                        .baseSalary(BigDecimal.valueOf(85000))
                        .deductions(BigDecimal.valueOf(10000))
                        .bonuses(BigDecimal.valueOf(5000))
                        .netSalary(BigDecimal.valueOf(80000))
                        .currency("INR").status("active")
                        .build());
        ApiResponse<List<SalaryListResponse>> response = ApiResponse.<List<SalaryListResponse>>builder()
                .success(true).data(list)
                .pagination(ApiResponse.PaginationMeta.builder().total(1).page(1).limit(10)
                        .totalPages(1).hasNextPage(false).hasPrevPage(false).build())
                .build();
        when(salaryService.getAllSalaries(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(response);
        mockMvc.perform(get("/api/salary").param("page", "1").param("limit", "10")
                        .param("sortBy", "netSalary").param("sortOrder", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pagination.total").value(1))
                .andExpect(jsonPath("$.data[0].netSalary").value(80000));
    }
}
