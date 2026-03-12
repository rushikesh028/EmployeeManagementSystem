package com.ems.controller;

import com.ems.model.Department;
import com.ems.service.DepartmentService;
import com.ems.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST Controller for Department operations.
//Base URL: /api/departments


//  POST   /api/departments        – Create department
//  GET    /api/departments        – Get all departments
//  GET    /api/departments/{id}   – Get department by ID
//  PUT    /api/departments/{id}   – Update department
//  DELETE /api/departments/{id}   – Delete department

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Department>> create(
            @Valid @RequestBody Department department) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Department created", departmentService.createDepartment(department)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(departmentService.getAllDepartments()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(departmentService.getDepartmentById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> update(
            @PathVariable Long id,
            @Valid @RequestBody Department department) {

        return ResponseEntity.ok(
                ApiResponse.success("Department updated", departmentService.updateDepartment(id, department)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(
                ApiResponse.success("Department deleted", "Deleted id: " + id));
    }
}
