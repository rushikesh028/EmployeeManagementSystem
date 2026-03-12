package com.ems.dao;

import com.ems.model.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DepartmentDAOImpl implements DepartmentDAO {

    private final JdbcTemplate jdbc;

    private final RowMapper<Department> deptRowMapper = (rs, rowNum) -> {
        Department d = new Department();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        d.setLocation(rs.getString("location"));
        d.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        try { d.setEmployeeCount(rs.getInt("employee_count")); }
        catch (Exception ignored) {}
        return d;
    };

    private static final String BASE_SELECT = """
            SELECT d.*,
                   COUNT(e.id) AS employee_count
            FROM departments d
            LEFT JOIN employees e ON e.department_id = d.id
            """;

    @Override
    public Department save(Department dept) {
        String sql = "INSERT INTO departments (name, location) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getLocation());
            return ps;
        }, keyHolder);

        dept.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return findById(dept.getId()).orElseThrow();
    }

    @Override
    public Optional<Department> findById(Long id) {
        String sql = BASE_SELECT + " WHERE d.id=? GROUP BY d.id";
        List<Department> list = jdbc.query(sql, deptRowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Department> findByName(String name) {
        String sql = BASE_SELECT + " WHERE d.name=? GROUP BY d.id";
        List<Department> list = jdbc.query(sql, deptRowMapper, name);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Department> findAll() {
        return jdbc.query(BASE_SELECT + " GROUP BY d.id ORDER BY d.name", deptRowMapper);
    }

    @Override
    public Department update(Department dept) {
        jdbc.update("UPDATE departments SET name=?, location=? WHERE id=?",
                dept.getName(), dept.getLocation(), dept.getId());
        return findById(dept.getId()).orElseThrow();
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbc.update("DELETE FROM departments WHERE id=?", id) > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM departments WHERE id=?", Integer.class, id);
        return c != null && c > 0;
    }

    @Override
    public boolean existsByName(String name) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM departments WHERE name=?", Integer.class, name);
        return c != null && c > 0;
    }
}
