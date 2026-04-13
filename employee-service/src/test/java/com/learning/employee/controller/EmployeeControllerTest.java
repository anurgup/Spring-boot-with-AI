package com.learning.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learning.employee.dto.*;
import com.learning.employee.exception.DuplicateEmailException;
import com.learning.employee.exception.EmployeeNotFoundException;
import com.learning.employee.exception.GlobalExceptionHandler;
import com.learning.employee.service.EmployeeService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private CreateEmployeeRequest buildCreateRequest() {
        AddressDto address = AddressDto.builder()
                .street("123 MG Road").city("Delhi").state("Delhi")
                .country("India").pincode("110001").build();
        return CreateEmployeeRequest.builder()
                .firstName("Anurag").lastName("Sharma")
                .email("anurag.sharma@company.com")
                .phone("+91-9876543210").department("Engineering")
                .designation("Senior Developer")
                .joiningDate(LocalDate.of(2024, 1, 15))
                .salary(BigDecimal.valueOf(85000))
                .managerId("emp_abc123")
                .address(address).status("active").build();
    }

    private EmployeeResponse buildEmployeeResponse() {
        return EmployeeResponse.builder()
                .id("emp_xyz789").firstName("Anurag").lastName("Sharma")
                .email("anurag.sharma@company.com").department("Engineering")
                .designation("Senior Developer").status("active")
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
    }

    @Test
    void createEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any())).thenReturn(buildEmployeeResponse());
        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee created successfully"))
                .andExpect(jsonPath("$.data.email").value("anurag.sharma@company.com"));
    }

    @Test
    void createEmployee_ValidationError() throws Exception {
        CreateEmployeeRequest invalid = CreateEmployeeRequest.builder().build();
        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createEmployee_DuplicateEmail() throws Exception {
        when(employeeService.createEmployee(any())).thenThrow(new DuplicateEmailException());
        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_ERROR"));
    }

    @Test
    void getEmployees_Success() throws Exception {
        List<EmployeeListResponse> list = List.of(
                EmployeeListResponse.builder().id("emp_xyz789").firstName("Anurag")
                        .email("anurag.sharma@company.com").department("Engineering").status("active").build());
        ApiResponse<List<EmployeeListResponse>> response = ApiResponse.<List<EmployeeListResponse>>builder()
                .success(true).data(list)
                .pagination(ApiResponse.PaginationMeta.builder().total(1).page(1).limit(10)
                        .totalPages(1).hasNextPage(false).hasPrevPage(false).build())
                .build();
        when(employeeService.getEmployees(anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenReturn(response);
        mockMvc.perform(get("/api/employee").param("page", "1").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pagination.total").value(1));
    }

    @Test
    void updateEmployee_Success() throws Exception {
        EmployeeResponse updated = buildEmployeeResponse();
        updated.setDesignation("Lead Developer");
        when(employeeService.updateEmployee(eq("emp_xyz789"), any())).thenReturn(updated);
        UpdateEmployeeRequest req = UpdateEmployeeRequest.builder()
                .designation("Lead Developer").status("active").build();
        mockMvc.perform(patch("/api/employee/emp_xyz789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"));
    }

    @Test
    void updateEmployee_NotFound() throws Exception {
        when(employeeService.updateEmployee(eq("unknown"), any()))
                .thenThrow(new EmployeeNotFoundException("unknown"));
        UpdateEmployeeRequest req = UpdateEmployeeRequest.builder().designation("Dev").build();
        mockMvc.perform(patch("/api/employee/unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void deleteEmployee_Success() throws Exception {
        DeleteResponse deleteResponse = DeleteResponse.builder().id("emp_xyz789").deletedAt(Instant.now()).build();
        when(employeeService.deleteEmployee("emp_xyz789")).thenReturn(deleteResponse);
        mockMvc.perform(delete("/api/employee/emp_xyz789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    void deleteEmployee_NotFound() throws Exception {
        when(employeeService.deleteEmployee("unknown")).thenThrow(new EmployeeNotFoundException("unknown"));
        mockMvc.perform(delete("/api/employee/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void searchByEmployeeId_Success() throws Exception {
        List<EmployeeResponse> results = List.of(buildEmployeeResponse());
        when(employeeService.searchByEmployeeId("emp_xyz")).thenReturn(results);
        mockMvc.perform(get("/api/employee/search").param("employeeId", "emp_xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("emp_xyz789"));
    }

    @Test
    void searchByEmployeeId_CaseInsensitivePartialMatch() throws Exception {
        List<EmployeeResponse> results = List.of(buildEmployeeResponse());
        when(employeeService.searchByEmployeeId("EMP_XYZ")).thenReturn(results);
        mockMvc.perform(get("/api/employee/search").param("employeeId", "EMP_XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void searchByEmployeeId_NoMatches_ReturnsEmptyList() throws Exception {
        when(employeeService.searchByEmployeeId("nonexistent")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/employee/search").param("employeeId", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void searchByEmployeeId_BlankId_Returns400() throws Exception {
        mockMvc.perform(get("/api/employee/search").param("employeeId", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void searchByEmployeeId_MissingParam_Returns400() throws Exception {
        mockMvc.perform(get("/api/employee/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
