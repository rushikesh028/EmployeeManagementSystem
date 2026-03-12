package com.ems.dao;

import com.ems.model.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentDAO {

    Department save(Department department);

    Optional<Department> findById(Long id);

    Optional<Department> findByName(String name);

    List<Department> findAll();

    Department update(Department department);

    boolean deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByName(String name);
}
