package com.ems.dao;

import com.ems.model.Employee;
import com.ems.model.Employee.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


// Pure JDBC implementation of EmployeeDAO.

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeDAOImpl implements EmployeeDAO {

    private final JdbcTemplate jdbc;

    // ──────────────────────────────────────────────────────────────────────────
    // RowMapper – converts ResultSet row → Employee object
    // ──────────────────────────────────────────────────────────────────────────
    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> {

        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        e.setSalary(rs.getBigDecimal("salary"));
        e.setDesignation(rs.getString("designation"));
        e.setDepartmentId(rs.getLong("department_id"));
        e.setHireDate(rs.getDate("hire_date") != null
                ? rs.getDate("hire_date").toLocalDate() : null);
        e.setStatus(EmployeeStatus.valueOf(rs.getString("status")));
        e.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        e.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);

        // departmentName comes from JOIN queries
        try {
            e.setDepartmentName(rs.getString("department_name"));
        } catch (SQLException ignored) {

        }
        return e;
    };

    // ──────────────────────────────────────────────────────────────────────────
    // SQL Constants
    // ──────────────────────────────────────────────────────────────────────────
    private static final String BASE_SELECT = """
            SELECT e.*, d.name AS department_name
            FROM employees e
            LEFT JOIN departments d ON e.department_id = d.id
            """;

    // ──────────────────────────────────────────────────────────────────────────
    // CREATE
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public Employee save(Employee emp) {
        String sql = """
                INSERT INTO employees
                    (first_name, last_name, email, phone, salary, designation,
                     department_id, hire_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setBigDecimal(5, emp.getSalary());
            ps.setString(6, emp.getDesignation());
            if (emp.getDepartmentId() != null)
                ps.setLong(7, emp.getDepartmentId());
            else
                ps.setNull(7, Types.BIGINT);
            ps.setDate(8, emp.getHireDate() != null
                    ? Date.valueOf(emp.getHireDate()) : null);
            ps.setString(9, emp.getStatus() != null
                    ? emp.getStatus().name() : EmployeeStatus.ACTIVE.name());
            return ps;
        }, keyHolder);

        emp.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.debug("Saved employee with id={}", emp.getId());
        return findById(emp.getId()).orElseThrow();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public Optional<Employee> findById(Long id) {
        String sql = BASE_SELECT + " WHERE e.id = ?";
        List<Employee> list = jdbc.query(sql, employeeRowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        String sql = BASE_SELECT + " WHERE e.email = ?";
        List<Employee> list = jdbc.query(sql, employeeRowMapper, email);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Employee> findAll() {
        return jdbc.query(BASE_SELECT + " ORDER BY e.id", employeeRowMapper);
    }

    @Override
    public List<Employee> findAllWithPagination(int page, int size) {
        String sql = BASE_SELECT + " ORDER BY e.id LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return jdbc.query(sql, employeeRowMapper, size, offset);
    }

    @Override
    public List<Employee> findByDepartmentId(Long departmentId) {
        String sql = BASE_SELECT + " WHERE e.department_id = ? ORDER BY e.first_name";
        return jdbc.query(sql, employeeRowMapper, departmentId);
    }

    @Override
    public List<Employee> findByStatus(EmployeeStatus status) {
        String sql = BASE_SELECT + " WHERE e.status = ? ORDER BY e.id";
        return jdbc.query(sql, employeeRowMapper, status.name());
    }

    @Override
    public List<Employee> searchByName(String keyword) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        String sql = BASE_SELECT +
                " WHERE LOWER(e.first_name) LIKE ? OR LOWER(e.last_name) LIKE ?" +
                " ORDER BY e.first_name";
        return jdbc.query(sql, employeeRowMapper, pattern, pattern);
    }

    @Override
    public long count() {
        Long c = jdbc.queryForObject("SELECT COUNT(*) FROM employees", Long.class);
        return c != null ? c : 0L;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public Employee update(Employee emp) {
        String sql = """
                UPDATE employees
                SET first_name=?, last_name=?, email=?, phone=?,
                    salary=?, designation=?, department_id=?, hire_date=?, status=?
                WHERE id=?
                """;

        jdbc.update(sql,
                emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getPhone(),
                emp.getSalary(), emp.getDesignation(), emp.getDepartmentId(),
                emp.getHireDate() != null ? Date.valueOf(emp.getHireDate()) : null,
                emp.getStatus().name(),
                emp.getId());
        emp.setCreatedAt(LocalDateTime.now());

        log.debug("Updated employee id={}", emp.getId());
        return findById(emp.getId()).orElseThrow();
    }

    @Override
    public boolean updateStatus(Long id, EmployeeStatus status) {
        int rows = jdbc.update(
                "UPDATE employees SET status='INACTIVE'", status.name(), id);
        return rows > 0;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public boolean deleteById(Long id) {
        int rows = jdbc.update("DELETE FROM employees WHERE id=?", id);
        return rows > 0;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // UTILITIES
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public boolean existsById(Long id) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM employees WHERE id=?", Integer.class, id);
        return c != null && c > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM employees WHERE email=?", Integer.class, email);
        return c != null && c > 0;
    }
}
