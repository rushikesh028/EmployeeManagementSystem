package com.ems.dao;

import com.ems.model.Employee;
import java.util.List;
import java.util.Optional;

// This interface defines the contract for Employee data access operations.
// It abstracts the underlying data access implementation, allowing for flexibility
// in how Employee data is stored and retrieved. The actual implementation can use
// JDBC, JPA, Hibernate, or any other data access technology without affecting the rest of the application. This design promotes separation of concerns and makes it easier
// to switch data access strategies in the future if needed, without requiring changes to the business logic or service layers that depend on this DAO.
// raw JDBC.

public interface EmployeeDAO {

    //  Create
    Employee save(Employee employee);

    //  Read
    Optional<Employee> findById(Long id);

    Optional<Employee> findByEmail(String email);

    List<Employee> findAll();

    List<Employee> findAllWithPagination(int page, int size);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByStatus(Employee.EmployeeStatus status);

    List<Employee> searchByName(String keyword);

    long count();

    //  Update
    Employee update(Employee employee);

    boolean updateStatus(Long id, Employee.EmployeeStatus status);

    //  Delete
    boolean deleteById(Long id);

    //  Utilities
    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
