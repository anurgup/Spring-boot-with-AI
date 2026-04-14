package com.learning.employee.mapper;

import com.learning.employee.dto.CreateEmployeeRequest;
import com.learning.employee.dto.EmployeeResponse;
import com.learning.employee.dto.UpdateEmployeeRequest;
import com.learning.employee.model.Employee;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = new EmployeeMapper();

    @Test
    void toEntity_MapsGenderFromRequest() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("John").lastName("Doe").email("john@example.com")
                .gender("Male").build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getGender()).isEqualTo("Male");
    }

    @Test
    void toEntity_GenderIsNullWhenNotProvided() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("John").lastName("Doe").email("john@example.com").build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getGender()).isNull();
    }

    @Test
    void toResponse_MapsGenderFromEmployee() {
        Employee employee = Employee.builder()
                .id("emp_1").firstName("Jane").lastName("Doe")
                .email("jane@example.com").gender("Female").build();

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response.getGender()).isEqualTo("Female");
    }

    @Test
    void toResponse_GenderIsNullWhenNotSet() {
        Employee employee = Employee.builder()
                .id("emp_1").firstName("Jane").lastName("Doe")
                .email("jane@example.com").build();

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response.getGender()).isNull();
    }

    @Test
    void updateEntity_UpdatesGenderWhenProvided() {
        Employee employee = Employee.builder().id("emp_1").gender("Male").build();
        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder().gender("Non-binary").build();

        mapper.updateEntity(employee, request);

        assertThat(employee.getGender()).isEqualTo("Non-binary");
    }

    @Test
    void updateEntity_DoesNotOverwriteGenderWhenNull() {
        Employee employee = Employee.builder().id("emp_1").gender("Female").build();
        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder().designation("Manager").build();

        mapper.updateEntity(employee, request);

        assertThat(employee.getGender()).isEqualTo("Female");
    }

    @Test
    void toEntity_GenderFreeTextAllowed() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .firstName("Alex").lastName("Smith").email("alex@example.com")
                .gender("Prefer not to say").build();

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getGender()).isEqualTo("Prefer not to say");
    }
}
