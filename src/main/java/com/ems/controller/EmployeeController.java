package com.ems.controller;

import com.ems.model.Employee;
import com.ems.model.Employee.EmployeeStatus;
import com.ems.service.EmployeeService;
import com.ems.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


//  REST Controller for Employee operations.
//
//  Base URL: /api/employees


@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> createEmployee(
            @Valid @RequestBody Employee employee) {

        Employee saved = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", saved));
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size) {

        List<Employee> employees = (page > 0 && size > 0)
                ? employeeService.getAllEmployeesWithPagination(page, size)
                : employeeService.getAllEmployees();

        return ResponseEntity.ok(
                ApiResponse.success("Employees fetched successfully", employees));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getEmployeeById(id)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Employee>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getEmployeeByEmail(email)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Employee>>> search(
            @RequestParam(defaultValue = "") String q) {

        return ResponseEntity.ok(
                ApiResponse.success("Search results", employeeService.searchEmployees(q)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Employee>>> getByStatus(
            @PathVariable EmployeeStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getEmployeesByStatus(status)));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<Employee>>> getByDepartment(
            @PathVariable Long departmentId) {

        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getEmployeesByDepartment(departmentId)));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCount() {
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("totalEmployees", employeeService.getTotalCount())));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody Employee employee) {

        return ResponseEntity.ok(
                ApiResponse.success("Employee updated", employeeService.updateEmployee(id, employee)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Employee>> updateStatus(
            @PathVariable Long id,
            @RequestParam EmployeeStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success("Status updated", employeeService.updateEmployeeStatus(id, status)));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee deleted successfully", "Deleted id: " + id));
    }
}


//  Endpoints:
//   POST    /api/employees                  – Create employee
//   GET     /api/employees                  – Get all employees (supports ?page=&size=)
//   GET     /api/employees/{id}             – Get employee by ID
//   GET     /api/employees/email/{email}    – Get employee by email
//   GET     /api/employees/search?q=        – Search employees by name
//   GET     /api/employees/status/{status}  – Filter by status
//   GET     /api/employees/department/{id}  – Get by department
//   GET     /api/employees/count            – Total count
//   PUT     /api/employees/{id}             – Full update
//   PATCH   /api/employees/{id}/status      – Update status only
//   DELETE  /api/employees/{id}             – Delete employee