package com.ems.service;

import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAO;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Employee;
import com.ems.model.Employee.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


 // logic layer for Employee operations.

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final DepartmentDAO departmentDAO;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public Employee createEmployee(Employee employee) {
        log.info("Creating employee: {}", employee.getEmail());

        if (employeeDAO.existsByEmail(employee.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee with email '" + employee.getEmail() + "' already exists.");
        }

        validateDepartment(employee.getDepartmentId());

        if (employee.getStatus() == null) employee.setStatus(EmployeeStatus.ACTIVE);

        Employee saved = employeeDAO.save(employee);
        log.info("Employee created successfully with id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByEmail(String email) {
        return employeeDAO.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployeesWithPagination(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;
        return employeeDAO.findAllWithPagination(page, size);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        validateDepartment(departmentId);
        return employeeDAO.findByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        return employeeDAO.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllEmployees();
        return employeeDAO.searchByName(keyword.trim());
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return employeeDAO.count();
    }

    // UPDATE
    @Transactional
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        log.info("Updating employee id={}", id);

        Employee existing = employeeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Check email uniqueness only if changed
        if (!existing.getEmail().equalsIgnoreCase(updatedEmployee.getEmail())
                && employeeDAO.existsByEmail(updatedEmployee.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + updatedEmployee.getEmail() + "' is already taken.");
        }

        validateDepartment(updatedEmployee.getDepartmentId());

        updatedEmployee.setId(id);
        return employeeDAO.update(updatedEmployee);
    }

    @Transactional
    public Employee updateEmployeeStatus(Long id, EmployeeStatus status) {
        if (!employeeDAO.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeDAO.updateStatus(id, status);
        return employeeDAO.findById(id).orElseThrow();
    }

    //  DELETE
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee id={}", id);
        if (!employeeDAO.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeDAO.deleteById(id);
        log.info("Employee id={} deleted", id);
    }

    private void validateDepartment(Long departmentId) {
        if (departmentId != null && !departmentDAO.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }
    }
}
