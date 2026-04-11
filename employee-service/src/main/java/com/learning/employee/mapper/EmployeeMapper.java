package com.learning.employee.mapper;

import com.learning.employee.dto.*;
import com.learning.employee.model.Address;
import com.learning.employee.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(CreateEmployeeRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .salary(request.getSalary())
                .managerId(request.getManagerId())
                .address(toAddressEntity(request.getAddress()))
                .status(request.getStatus() != null ? request.getStatus() : "active")
                .build();
    }

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .salary(employee.getSalary())
                .managerId(employee.getManagerId())
                .address(toAddressDto(employee.getAddress()))
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    public EmployeeListResponse toListResponse(Employee employee) {
        return EmployeeListResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    public void updateEntity(Employee employee, UpdateEmployeeRequest request) {
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());
        if (request.getAddress() != null) {
            if (employee.getAddress() == null) {
                employee.setAddress(toAddressEntity(request.getAddress()));
            } else {
                mergeAddress(employee.getAddress(), request.getAddress());
            }
        }
    }

    private void mergeAddress(Address address, AddressDto dto) {
        if (dto.getStreet() != null) address.setStreet(dto.getStreet());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getState() != null) address.setState(dto.getState());
        if (dto.getCountry() != null) address.setCountry(dto.getCountry());
        if (dto.getPincode() != null) address.setPincode(dto.getPincode());
    }

    private Address toAddressEntity(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .pincode(dto.getPincode())
                .build();
    }

    private AddressDto toAddressDto(Address address) {
        if (address == null) return null;
        return AddressDto.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .pincode(address.getPincode())
                .build();
    }
}
