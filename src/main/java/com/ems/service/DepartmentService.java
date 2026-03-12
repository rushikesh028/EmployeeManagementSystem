package com.ems.service;

import com.ems.dao.DepartmentDAO;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    @Transactional
    public Department createDepartment(Department department) {
        if (departmentDAO.existsByName(department.getName())) {
            throw new DuplicateResourceException(
                    "Department '" + department.getName() + "' already exists.");
        }
        return departmentDAO.save(department);
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    @Transactional
    public Department updateDepartment(Long id, Department updated) {
        Department existing = departmentDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!existing.getName().equalsIgnoreCase(updated.getName())
                && departmentDAO.existsByName(updated.getName())) {
            throw new DuplicateResourceException(
                    "Department name '" + updated.getName() + "' is already taken.");
        }
        updated.setId(id);
        return departmentDAO.update(updated);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentDAO.existsById(id)) {
            throw new ResourceNotFoundException("Department", "id", id);
        }
        departmentDAO.deleteById(id);
        log.info("Department id = {} deleted", id);
    }
}
