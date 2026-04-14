package com.learning.employee.mapper;

import com.learning.employee.dto.CreateEmployeeRequest;
import com.learning.employee.dto.EmployeeResponse;
import com.learning.employee.dto.UpdateEmployeeRequest;
import com.learning.employee.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EmployeeMapper();
    }

    @Test
    void toEntity_WithGender_MapsGenderCorrectly() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("John").lastName("Doe")
                .email("john.doe@example.com")
                .gender("Male")
                .build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getGender()).isEqualTo("Male");
    }

    @Test
    void toEntity_WithoutGender_GenderIsNull() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("Jane").lastName("Doe")
                .email("jane.doe@example.com")
                .build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getGender()).isNull();
    }

    @Test
    void toEntity_WithFreeTextGender_MapsCorrectly() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("Alex").lastName("Smith")
                .email("alex.smith@example.com")
                .gender("Non-binary")
                .build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getGender()).isEqualTo("Non-binary");
    }

    @Test
    void toResponse_WithGender_ExposesGender() {
        Employee employee = Employee.builder()
                .id("emp_1").firstName("John").lastName("Doe")
                .email("john.doe@example.com")
                .gender("Female")
                .build();

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response.getGender()).isEqualTo("Female");
    }

    @Test
    void toResponse_WithoutGender_GenderIsNull() {
        Employee employee = Employee.builder()
                .id("emp_1").firstName("John").lastName("Doe")
                .email("john.doe@example.com")
                .build();

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response.getGender()).isNull();
    }

    @Test
    void updateEntity_WithGender_UpdatesGender() {
        Employee employee = Employee.builder()
                .id("emp_1").gender("Male")
                .build();
        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                .gender("Non-binary")
                .build();

        mapper.updateEntity(employee, request);

        assertThat(employee.getGender()).isEqualTo("Non-binary");
    }

    @Test
    void updateEntity_WithNullGender_DoesNotOverwriteExistingGender() {
        Employee employee = Employee.builder()
                .id("emp_1").gender("Male")
                .build();
        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                .designation("Manager")
                .build();

        mapper.updateEntity(employee, request);

        assertThat(employee.getGender()).isEqualTo("Male");
    }

    @Test
    void toEntity_DefaultsStatusToActive_WhenStatusIsNull() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("John").lastName("Doe")
                .email("john.doe@example.com")
                .gender("Male")
                .build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getStatus()).isEqualTo("active");
        assertThat(entity.getGender()).isEqualTo("Male");
    }
}
