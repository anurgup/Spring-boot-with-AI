package com.learning.employee.repository;

import com.learning.employee.model.Salary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryRepository extends MongoRepository<Salary, String> {
    Optional<Salary> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
}
